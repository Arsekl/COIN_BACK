package kg666.service;

import com.alibaba.fastjson.JSON;
import kg666.data.DefaultLayoutMapper;
import kg666.data.LinkLayoutMapper;
import kg666.data.MyNeo4jDriver;
import kg666.data.NodeLayoutMapper;
import kg666.po.DefaultLayout;
import kg666.po.LinkLayout;
import kg666.po.NodeLayout;
import kg666.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class GraphService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";
    private final static String IMPORT_ERROR = "Please check your cypher statement in file";
    private final static String MYSQL_RUNNING_ERROR = "Something is wrong while mysql is running";

    @Autowired
    private MyNeo4jDriver driver;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private NodeLayoutMapper nodeLayoutMapper;

    @Autowired
    private LinkLayoutMapper linkLayoutMapper;

    @Autowired
    private DefaultLayoutMapper defaultLayoutMapper;

    public ResponseVO getGraph(String pic_name, Long uid) {
        try {
            DefaultLayout defaultLayout = defaultLayoutMapper.getByName(pic_name, uid);
            if (defaultLayout==null){
                return ResponseVO.buildFailure("No such graph!");
            }
            List<HashMap<String, Object>> nodes = driver.getGraphNode(String.format("match (n) where n.pic_name='%s' and n.uid=%s return n", pic_name, uid));
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip(String.format("match (n)-[r]->(m) where n.pic_name='%s' and n.uid=%s return r", pic_name, uid));
            List<String> categoryNames = new ArrayList<>();
            List<HashMap<String, Object>> categories = new ArrayList<>();
            for (HashMap<String, Object> node : nodes) {
                String name = String.valueOf(node.get("category"));
                if (!categoryNames.contains(name)) {
                    HashMap<String, Object> category = new HashMap<>();
                    categoryNames.add(name);
//                    node.replace("category", categoryNames.indexOf(name));
                    category.put("name", name);
                    categories.add(category);
                }
            }
            HashMap<String, Object> result = new HashMap<>();
            result.put("pic_name", pic_name);
            result.put("uid", uid);
            addLayoutInfo(defaultLayout, result);
            nodeService.addLayoutInfo(nodes);
            result.put("nodes", nodes);
            relationshipService.addLayoutInfo(relationships);
            result.put("links", relationships);
            result.put("categories", categories);
            System.out.println(JSON.toJSONString(result));
            return ResponseVO.buildSuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getNodeNum(String pic_name, Long uid){
        String cypher = String.format("match (n) where n.pic_name='%s' and n.uid=%s return count(n)", pic_name, uid);
        Integer res;
        try{
            res = driver.getCount(cypher);
            return ResponseVO.buildSuccess(res);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getLinkNum(String pic_name, Long uid){
        String cypher = String.format("match (n)-[r]->(m) where n.pic_name='%s' and n.uid=%s return count(r)", pic_name, uid);
        Integer res;
        try{
            res = driver.getCount(cypher);
            return ResponseVO.buildSuccess(res);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    private void addLayoutInfo(DefaultLayout defaultLayout, HashMap<String, Object> result) {
        List<Double> center = new ArrayList<>();
        center.add(defaultLayout.getX());
        center.add(defaultLayout.getY());
        result.put("center", center);
        result.put("zoom", defaultLayout.getZoom());
        HashMap<String, Object> itemStyle = new HashMap<>();
        itemStyle.put("color", defaultLayout.getItem_color());
        result.put("itemStyle", itemStyle);
        HashMap<String, Object> lineStyle = new HashMap<>();
        lineStyle.put("color", defaultLayout.getLine_color());
        lineStyle.put("width", defaultLayout.getLine_width());
        lineStyle.put("type", defaultLayout.getLine_type());
        lineStyle.put("curveness", defaultLayout.getLine_cur());
        result.put("lineStyle", lineStyle);
        HashMap<String, Object> label = new HashMap<>();
        label.put("show", defaultLayout.getLabel_show());
        label.put("fontSize", defaultLayout.getLabel_fontsize());
        result.put("label", label);
        HashMap<String, Object> tooltip = new HashMap<>();
        tooltip.put("show", defaultLayout.getTooltip_show());
        result.put("tooltip", tooltip);
    }

    public ResponseVO deleteAll(String pic_name, Long uid) {
        String cypher = String.format("match (n) where n.pic_name='%s' and n.uid=%s detach delete n", pic_name, uid);
        try {
            driver.executeCypher(cypher);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    /**
     * this is a temp method which will be moved in the future
     */
    public void deleteLayout(){
        nodeLayoutMapper.deleteAll();
        linkLayoutMapper.deleteAll();
        defaultLayoutMapper.deleteAll();
    }

    public ResponseVO importGraphByCypher(MultipartFile file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            StringBuilder cypher = new StringBuilder();
            String result = bufferedReader.readLine();
            while (result != null) {
                cypher.append(result).append(" ");
                result = bufferedReader.readLine();
            }
            System.out.println(cypher.toString());
            driver.executeCypher(cypher.toString());
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(IMPORT_ERROR);
        }
    }

    public ResponseVO saveGraph(GraphVO graphVO) {
        List<Map<String, String>> labels = graphVO.getCategories();
        try {
            for (NodeVO nodeVO : graphVO.getNodes()) {
                int index = Integer.parseInt(nodeVO.getCategory());
                nodeVO.setCategory(labels.get(index).get("name"));
                nodeService.createNode(nodeVO);
            }
            driver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", graphVO.getPic_name(), graphVO.getUid()));
            for (RelationshipVO relationshipVO : graphVO.getLinks()) {
                relationshipService.createRelationship(relationshipVO);
            }
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO saveLayout(GraphVO graphVO) {
        ItemStyleVO itemStyleVO = graphVO.getItemStyle();
        LineStyleVO lineStyleVO = graphVO.getLineStyle();
        LabelVO labelVO = graphVO.getLabel();
        TooltipVO tooltipVO = graphVO.getTooltip();
        DefaultLayout defaultLayout = new DefaultLayout(graphVO.getPic_name(), graphVO.getUid(), graphVO.getCenter().get(0), graphVO.getCenter().get(1), graphVO.getZoom(), itemStyleVO.getColor(), lineStyleVO.getColor(), lineStyleVO.getWidth(), lineStyleVO.getType(), lineStyleVO.getCurveness(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
        try {
            defaultLayoutMapper.insert(defaultLayout);
            for (NodeVO nodeVO : graphVO.getNodes()) {
                if (nodeVO.getSymbol() == null) {
                    NodeLayout nodeLayout = new NodeLayout(nodeVO.getId(), nodeVO.getX(), nodeVO.getY(), graphVO.getItemStyle().getColor(), null, graphVO.getLabel().getShow(), graphVO.getLabel().getFontSize(), graphVO.getTooltip().getShow());
                    nodeLayoutMapper.insert(nodeLayout);
                } else {
                    itemStyleVO = nodeVO.getItemStyle();
                    labelVO = nodeVO.getLabel();
                    tooltipVO = nodeVO.getTooltip();
                    NodeLayout nodeLayout = new NodeLayout(nodeVO.getId(), nodeVO.getX(), nodeVO.getY(), itemStyleVO.getColor(), nodeVO.getSymbol(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
                    nodeLayoutMapper.insert(nodeLayout);
                }
            }
            for (RelationshipVO relationshipVO : graphVO.getLinks()) {
                if (relationshipVO.getLabel() == null) continue;
                lineStyleVO = relationshipVO.getLineStyle();
                labelVO = relationshipVO.getLabel();
                tooltipVO = relationshipVO.getTooltip();
                LinkLayout linkLayout = new LinkLayout(relationshipVO.getId(), lineStyleVO.getColor(), lineStyleVO.getWidth(), lineStyleVO.getType(), lineStyleVO.getCurveness(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
                linkLayoutMapper.insert(linkLayout);
            }
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(MYSQL_RUNNING_ERROR);
        }
    }
}
