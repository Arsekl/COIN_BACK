package kg666.service;

import kg666.data.MyNeo4jDriver;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RelationshipService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";
    private final static String RELATIONSHIP_CREATE_FAIL = "Fail to create new relationship";
    private final static String RELATIONSHIP_UPDATE_FAIL = "Fail to update the relationship. There is not a relationship match the given id";
    private final static String RELATIONSHIP_DELETE_FAIL = "Fail to delete the relationship. There is not a relationship match the given id";

    @Autowired
    private MyNeo4jDriver driver;

    public ResponseVO createRelationship( long sourceId, long targetId, String name) {
        try {
            String cypher = String.format("MATCH (n),(m) WHERE id(n)=%s AND id(m) = %s "
                    + "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", sourceId, targetId, name);
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip(cypher);
            if (relationships.size() > 0) {
                return ResponseVO.buildSuccess();
            }
            else return ResponseVO.buildFailure(RELATIONSHIP_CREATE_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO updateRelationship(long id, String name) {
        try {
            String cypher = String.format("MATCH (n) -[r]->(m) where id(r)=%s set r.name='%s' return r",
                    id, name);
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip(cypher);
            if (relationships.size() > 0) {
                return ResponseVO.buildSuccess();
            }
            else{
                return ResponseVO.buildFailure(RELATIONSHIP_UPDATE_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO deleteRelationship(long id) {
        try {
            String cypher = String.format("MATCH (n) -[r]->(m) where id(r)=%s return r", id);
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip(cypher);
            if (relationships.size() == 0){
                return ResponseVO.buildFailure(RELATIONSHIP_DELETE_FAIL);
            }
            cypher = String.format("MATCH (n) -[r]->(m) where id(r)=%s delete r", id);
            driver.executeCypher(cypher);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }
}
