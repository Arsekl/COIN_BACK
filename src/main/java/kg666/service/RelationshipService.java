package kg666.service;

import kg666.data.LinkLayoutMapper;
import kg666.data.MyNeo4jDriver;
import kg666.po.LinkLayout;
import kg666.vo.RelationshipFindVO;
import kg666.vo.RelationshipVO;
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

    @Autowired
    private LinkLayoutMapper mapper;

    public ResponseVO createRelationship(RelationshipVO relationshipVO) {
        String name = relationshipVO.getName();
        Long sourceId = relationshipVO.getSource();
        Long targetId = relationshipVO.getTarget();
        Long id = relationshipVO.getId();
        String property = "{name:'"+name+"', id:" + id + ", source:" + sourceId + ", target:" + targetId + "}";
        try {
            String cypher = String.format("MATCH (n),(m) WHERE n.id=%s AND m.id= %s "
                    + "CREATE (n)-[r:RE %s ]->(m)" + "RETURN r", sourceId, targetId, property);
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
            String cypher = String.format("MATCH (n) -[r]->(m) where r.id=%s set r.name='%s' return r",
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
            String cypher = String.format("MATCH (n)-[r]->(m) where r.id=%s return r", id);
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip(cypher);
            if (relationships.size() == 0){
                return ResponseVO.buildFailure(RELATIONSHIP_DELETE_FAIL);
            }
            cypher = String.format("MATCH (n) -[r]->(m) where r.id=%s delete r", id);
            driver.executeCypher(cypher);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO findRelationships(RelationshipFindVO relationship) {
        String name = relationship.getName();
        String source = relationship.getSource();
        String target = relationship.getTarget();
        String pic_name = relationship.getPic_name();
        Long uid = relationship.getUid();
        List<HashMap<String, Object>> result;
        StringBuilder cypher = new StringBuilder(String.format("match (n)-[r:RE]->(m) where n.pic_name='%s' and n.uid=%s ", pic_name, uid));
        String nameCypher = String.format("match (n)-[r:RE]->(m) where r.name=~'.*%s.*' ", name);
        String sourceCypher = String.format("match (n)-[r]->(m) where n.name=~'.*%s.*' ", source);
        String targetCypher = String.format("match (n)-[r]->(m) where m.name=~'.*%s.*' ", target);
        if (name != null) cypher.append(nameCypher);
        if (source != null) cypher.append(sourceCypher);
        if (target != null) cypher.append(targetCypher);
        cypher.append("return r");
        try{
            result = driver.getGraphRelationShip(cypher.toString());
            return ResponseVO.buildSuccess(result);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public void addLayoutInfo(List<HashMap<String, Object>> relationships) {
        LinkLayout linkLayout;
        for (HashMap<String, Object> relationship : relationships){
            long id = (Long) relationship.get("id");
            linkLayout = mapper.getById(id);
                HashMap<String, Object> style = new HashMap<>();
                style.put("color", linkLayout.getColor());
                style.put("width", linkLayout.getWidth());
                style.put("type", linkLayout.getType());
                style.put("curveness", linkLayout.getCurveness());
                relationship.put("lineStyle", style);
                HashMap<String, Object> label = new HashMap<>();
                label.put("show", linkLayout.getLabel_show());
                label.put("fontSize", linkLayout.getLabel_fontsize());
                relationship.put("label", label);
                HashMap<String, Object> tooltip = new HashMap<>();
                tooltip.put("show", linkLayout.getTooltip_show());
                relationship.put("tooltip", tooltip);
        }
    }
}
