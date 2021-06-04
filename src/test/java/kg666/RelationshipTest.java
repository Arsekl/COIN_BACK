package kg666;

import kg666.data.MyNeo4jDriver;
import kg666.service.NodeService;
import kg666.service.RelationshipService;
import kg666.vo.NodeVO;
import kg666.vo.RelationshipFindVO;
import kg666.vo.RelationshipVO;
import org.assertj.core.util.diff.myers.MyersDiff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class RelationshipTest {
    private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.2");

    @BeforeAll
    static void initializeNeo4j() {

        neo4jContainer.start();
    }
    @Autowired
    RelationshipService relationshipService;

    @Autowired
    Driver driver;

    @Autowired
    Neo4jTemplate neo4jTemplate;

    @Autowired
    NodeService nodeService;

    @Autowired
    MyNeo4jDriver myNeo4jDriver;

    @AfterEach
    void cleanUP(){
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH(n) DETACH DELETE n"));
        }
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry){

        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }

    @Test
    public void RelationshipSaveTest() {
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null,label, "hjm", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null,label, "cpk", 1L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        RelationshipVO relationship0 = new RelationshipVO(null,null,null,0L, 1L, 0L, "kg666");
        relationshipService.createRelationship(relationship0);
        int num;
        try(Session session = driver.session()){
            num = session.writeTransaction(tx -> {
                Result result = tx.run("match (n)-[r]->(m) return r");
                return result.list().size();                    });
        }
        assertThat(num).isEqualTo(1);

    }

    @Test
    public void UpdateRelationshipTest(){
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null,label, "hjm", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null,label, "cpk", 1L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        long sid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='hjm' return n").get(0).get("id")));
        long tid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='cpk' return n").get(0).get("id")));
        RelationshipVO relationship0 = new RelationshipVO(null,null,null,sid, tid, 1L, "kg666");
        relationshipService.createRelationship(relationship0);
        RelationshipVO relationship1 = new RelationshipVO(null,null,null,100L, 101L, 2L, "kg666");
        relationshipService.createRelationship(relationship1);
        long id = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphRelationShip("match (n)-[r]->(m) return r").get(0).get("id")));
        relationshipService.updateRelationship(id, "kg777");
        relationshipService.updateRelationship(100, "kg777");
        Map<String, Object> node;
        try (Session session = driver.session()) {
            node = session.writeTransaction(tx -> {
                Result result = tx.run("match (n)-[r]->(m) return r");
                return result.list().get(0).values().get(0).asMap();
            });
        }
        assertThat(node.get("name")).isEqualTo("kg777");
    }

    @Test
    public void DeleteRelationshipTest(){
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null,label, "hjm", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null,label, "cpk", 1L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        long sid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='hjm' return n").get(0).get("id")));
        long tid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='cpk' return n").get(0).get("id")));
        RelationshipVO relationship0 = new RelationshipVO(null,null,null,sid, tid, 1L, "kg666");
        relationshipService.createRelationship(relationship0);
        long id = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphRelationShip("match (n)-[r]->(m) return r").get(0).get("id")));
        System.out.println(id);
        relationshipService.deleteRelationship(id);
        relationshipService.deleteRelationship(100);
        int num;
        try(Session session = driver.session()){
            num = session.readTransaction(tx -> tx.run("match (n)-[r]->(m) return r").list().size());
        }
        assertThat(num).isEqualTo(0);
    }

    @Test
    public void FindRelationshipsTest(){
        NodeVO node0 = new NodeVO(null,null,null,null,null,null,"movie", "Hjm0", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null,"movie", "hjm1", 1L, 40D);
        NodeVO node3 = new NodeVO(null,null,null,null,null,null,"drama", "hj2", 2L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        nodeService.createNode(node3);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "temp", 1));
        NodeVO node0F = new NodeVO(null,null,null,null,null,null,"movie", "hjm0", 3L, 20D);
        NodeVO node1F = new NodeVO(null,null,null,null,null,null,"movie", "hjm1", 4L, 40D);
        nodeService.createNode(node0F);
        nodeService.createNode(node1F);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "tempp", 2));
        RelationshipVO link0F = new RelationshipVO(null,null,null,4L, 3L, 3L, "kg666");
        relationshipService.createRelationship(link0F);
        RelationshipVO link0 = new RelationshipVO(null,null,null,0L, 1L, 0L, "kg666");
        RelationshipVO link1 = new RelationshipVO(null,null,null,0L, 2L, 1L, "test66");
        RelationshipVO link2 = new RelationshipVO(null,null,null,1L, 2L, 2L, "test77");
        relationshipService.createRelationship(link0);
        relationshipService.createRelationship(link1);
        relationshipService.createRelationship(link2);
        System.out.println("----------------------------------------");
        RelationshipFindVO linkF0 = new RelationshipFindVO("temp", 1L,"66", null, null);
        RelationshipFindVO linkF1 = new RelationshipFindVO("temp", 1L,null, "0", null);
        RelationshipFindVO linkF2 = new RelationshipFindVO("temp", 1L,null, null, "hj2");
        RelationshipFindVO linkF3 = new RelationshipFindVO("temp", 1L,null, "hjm", "hj2");
        RelationshipFindVO linkF4 = new RelationshipFindVO("temp", 1L,"77", "hjm", "hj2");
        List<HashMap<String,Object>> result;
        result = (List<HashMap<String,Object>>)relationshipService.findRelationships(linkF0).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)relationshipService.findRelationships(linkF1).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)relationshipService.findRelationships(linkF2).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)relationshipService.findRelationships(linkF3).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)relationshipService.findRelationships(linkF4).getContent();
        assertThat(result.size()).isEqualTo(1);
    }
}
