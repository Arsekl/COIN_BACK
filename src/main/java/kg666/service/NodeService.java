package kg666.service;

import kg666.data.MyNeo4jDriver;
import kg666.data.NodeLayoutMapper;
import kg666.po.NodeLayout;
import kg666.vo.NodeFindVO;
import kg666.vo.NodeVO;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Service
public class NodeService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";
    private final static String NODE_CREATE_FAIL = "Fail to create a new node";
    private final static String NODE_UPDATE_FAIL = "Fail to update a new node. There is not a node match the given id";
    private final static String NODE_DELETE_FAIL = "Fail to delete a new node. There is not a node match the given id";

    @Autowired
    private MyNeo4jDriver driver;

    @Autowired
    private NodeLayoutMapper mapper;


    public ResponseVO createNode(NodeVO nodeVO) {
        String name = nodeVO.getName();
        String label = nodeVO.getCategory();
        Double size = nodeVO.getSymbolSize();
        Long id = nodeVO.getId();
        String property = "{name:'"+name+"', id:" + id +", symbolSize:" + size +"}";
        try {
            String cypher = String.format("create (n:`%s` %s) return n", label, property);
            driver.getGraphNode(cypher);
            return ResponseVO.buildSuccess();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO updateNodeById(long nodeId, String nodeName, Double size) {
        List<HashMap<String, Object>> nodeList;
        try {
            String cypherSql = String.format("MATCH (n) where n.id=%s set n.name='%s' set n.symbolSize=%s return n", nodeId,
                    nodeName, size);
            nodeList = driver.getGraphNode(cypherSql);
            if (nodeList.size() > 0) {
                return ResponseVO.buildSuccess();
            } else {
                return ResponseVO.buildFailure(NODE_UPDATE_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

//    public ResponseVO updateNodeLabel(long nodeId, String oldLabel, String newLabel){
//
//    }

    public ResponseVO deleteNode(long nodeId) {
        List<HashMap<String, Object>> result;
        try {
            String nodeCypher = String.format("MATCH (n)  where n.id=%s return n", nodeId);
            result = driver.getGraphNode(nodeCypher);
            if (result.size() == 0) {
                return ResponseVO.buildFailure(NODE_DELETE_FAIL);
            }
            String deleteNodeSql = String.format("MATCH (n) where n.id=%s detach delete n", nodeId);
            driver.executeCypher(deleteNodeSql);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO findNodes(NodeFindVO node) {
        String label = node.getLabel();
        String name = node.getName();
        Double lowerBound = node.getLowerBound();
        Double upperBound = node.getUpperBound();
        String pic_name = node.getPic_name();
        Long uid = node.getUid();
        List<HashMap<String, Object>> result;
        try {
            if (lowerBound == null) lowerBound = 0D;
            if (upperBound == null) upperBound = 100D;
            StringBuilder cypher = new StringBuilder(String.format("MATCH (n) where n.symbolSize>=%s and n.symbolSize<=%s and n.pic_name='%s' and n.uid=%s ", lowerBound, upperBound, pic_name, uid));
            String labelCypher = String.format("MATCH (n:`%s`) ", label);
            if (label != null) cypher.append(labelCypher);
            String nameCypher = String.format("MATCH (n) where n.name=~'.*%s.*' ", name);
            if (name != null) cypher.append(nameCypher);
            cypher.append("return n");
            result = driver.getGraphNode(cypher.toString());
            return ResponseVO.buildSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public void addLayoutInfo(List<HashMap<String, Object>> nodes){
        NodeLayout nodeLayout;
        for (HashMap<String, Object> node : nodes) {
            long id = (Long) node.get("id");
            nodeLayout = mapper.getById(id);
                node.put("x", nodeLayout.getX());
                node.put("y", nodeLayout.getY());
                node.put("symbol", nodeLayout.getSymbol());
                HashMap<String, Object> style = new HashMap<>();
                style.put("color", nodeLayout.getColor());
                node.put("itemStyle", style);
                HashMap<String, Object> label = new HashMap<>();
                label.put("show", nodeLayout.getLabel_show());
                label.put("fontSize", nodeLayout.getLabel_fontsize());
                node.put("label", label);
                HashMap<String, Object> tooltip = new HashMap<>();
                tooltip.put("show", nodeLayout.getTooltip_show());
                node.put("tooltip", tooltip);
        }
    }

//    public ResponseVO findNodeById(Long id){
//        List<HashMap<String, Object>> result;
//        try{
//            String cypher = String.format("match (n) where n.id=%s return n ", id);
//            result = driver.getGraphNode(cypher);
//            return ResponseVO.buildSuccess(result);
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
//        }
//    }

}
