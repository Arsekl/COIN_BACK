package kg666.service;

import kg666.data.MyNeo4jDriver;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class NodeService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";
    private final static String NODE_CREATE_FAIL = "Fail to create a new node";
    private final static String NODE_UPDATE_FAIL = "Fail to update a new node. There is not a node with given label match the given id";
    private final static String NODE_DELETE_FAIL = "Fail to delete a new node. There is not a node with given label match the given id";

    @Autowired
    private MyNeo4jDriver driver;


    public ResponseVO createNode(String label, String name) {
        List<HashMap<String, Object>> nodeList;
        String property = "{name:'"+name+"'}";
        try {
            String cypher = String.format("create (n:`%s` %s) return n", label, property);
            driver.getGraphNode(cypher);
            return ResponseVO.buildSuccess();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO updateNodeNameById(String label, long nodeId, String nodeName) {
        List<HashMap<String, Object>> nodeList;
        try {
            String cypherSql = String.format("MATCH (n:`%s`) where id(n)=%s set n.name='%s' return n", label, nodeId,
                    nodeName);
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

    public ResponseVO deleteNode(String label, long nodeId) {
        List<HashMap<String, Object>> result;
        try {
            String nodeCypher = String.format("MATCH (n:`%s`)  where id(n)=%s return n", label, nodeId);
            result = driver.getGraphNode(nodeCypher);
            if (result.size() == 0) {
                return ResponseVO.buildFailure(NODE_DELETE_FAIL);
            }
            String deleteNodeSql = String.format("MATCH (n:`%s`) where id(n)=%s detach delete n", label, nodeId);
            driver.executeCypher(deleteNodeSql);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }
}
