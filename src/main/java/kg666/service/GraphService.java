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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ResponseVO getUserGraphName(long uid){
        List<HashMap<String, Object>> result;
        try{
            result = driver.getResult(String.format("match (n) where n.uid=%s return distinct n.pic_name", uid));
            return ResponseVO.buildSuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getGraph(String pic_name, Long uid) {
        try {
            DefaultLayout defaultLayout = defaultLayoutMapper.getByName(pic_name, uid);
            List<HashMap<String, Object>> nodes = driver.getGraphNode(String.format("match (n) where n.pic_name='%s' and n.uid=%s return n", pic_name, uid));
            if (nodes.size()==0) return ResponseVO.buildFailure("no such graph!");
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
            if (defaultLayout!=null) {
                addLayoutInfo(defaultLayout, result);
                nodeService.addLayoutInfo(nodes,pic_name, uid);
                relationshipService.addLayoutInfo(relationships,pic_name, uid);
            }
            result.put("nodes", nodes);
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
        if (center.get(0)!=null && center.get(1)!=null)
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
        StringBuilder cypher = new StringBuilder();
        try {
            for (NodeVO nodeVO : graphVO.getNodes()) {
                int index = Integer.parseInt(nodeVO.getCategory());
                nodeVO.setCategory(labels.get(index).get("name"));
                cypher.append(nodeService.createCypher(nodeVO, graphVO.getPic_name(), graphVO.getUid()));
            }
            for (RelationshipVO relationshipVO : graphVO.getLinks()) {
                cypher.append(relationshipService.createCypher(relationshipVO));
            }
            driver.executeCypher(cypher.toString());
            System.out.println(cypher.toString());
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
        DefaultLayout defaultLayout;
        if (graphVO.getCenter()==null) defaultLayout = new DefaultLayout(graphVO.getPic_name(), graphVO.getUid(), null, null, graphVO.getZoom(), itemStyleVO.getColor(), lineStyleVO.getColor(), lineStyleVO.getWidth(), lineStyleVO.getType(), lineStyleVO.getCurveness(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
        else defaultLayout = new DefaultLayout(graphVO.getPic_name(), graphVO.getUid(), graphVO.getCenter().get(0), graphVO.getCenter().get(1), graphVO.getZoom(), itemStyleVO.getColor(), lineStyleVO.getColor(), lineStyleVO.getWidth(), lineStyleVO.getType(), lineStyleVO.getCurveness(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
        try {
            if (defaultLayoutMapper.getByName(defaultLayout.getPic_name(), defaultLayout.getUid())!=null)
                defaultLayoutMapper.update(defaultLayout);
            else defaultLayoutMapper.insert(defaultLayout);
            for (NodeVO nodeVO : graphVO.getNodes()) {
                    itemStyleVO = nodeVO.getItemStyle();
                    labelVO = nodeVO.getLabel();
                    tooltipVO = nodeVO.getTooltip();
                    NodeLayout nodeLayout = new NodeLayout(nodeVO.getId(),  graphVO.getPic_name(), graphVO.getUid(), nodeVO.getX(), nodeVO.getY(), itemStyleVO.getColor(), nodeVO.getSymbol(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
                    if (nodeLayoutMapper.getById(nodeLayout.getId(), graphVO.getUid(), graphVO.getPic_name())!=null)
                        nodeLayoutMapper.update(nodeLayout);
                    else nodeLayoutMapper.insert(nodeLayout);
            }
            for (RelationshipVO relationshipVO : graphVO.getLinks()) {
                lineStyleVO = relationshipVO.getLineStyle();
                labelVO = relationshipVO.getLabel();
                tooltipVO = relationshipVO.getTooltip();
                LinkLayout linkLayout = new LinkLayout(relationshipVO.getId(), graphVO.getPic_name(), graphVO.getUid(), lineStyleVO.getColor(), lineStyleVO.getWidth(), lineStyleVO.getType(), lineStyleVO.getCurveness(), labelVO.getShow(), labelVO.getFontSize(), tooltipVO.getShow());
                if (linkLayoutMapper.getById(linkLayout.getId(), graphVO.getUid(), graphVO.getPic_name())!=null)
                    linkLayoutMapper.update(linkLayout);
                else linkLayoutMapper.insert(linkLayout);
            }
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(MYSQL_RUNNING_ERROR);
        }
    }
}
