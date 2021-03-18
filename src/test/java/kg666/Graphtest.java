package kg666;

import kg666.data.MyNeo4jDriver;
import kg666.service.GraphService;
import kg666.service.NodeService;
import kg666.service.RelationshipService;
import kg666.vo.ResponseVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class Graphtest {
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
    GraphService graphService;

    @Autowired
    MyNeo4jDriver myNeo4jDriver;

    @AfterEach
    void cleanUP() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH(n) DETACH DELETE n"));
        }
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }

    @Test
    public void getGraphTest() {

        String label = "movie";
        nodeService.createNode(label, "hjm");
        nodeService.createNode("drama", "cpk");
        relationshipService.createRelationship(0L, 1L, "kg666");
        ResponseVO responseVO = graphService.getGraph();
        assertThat(responseVO.getContent()).isNotNull();

    }

    @Test
    void DeleteAllTest(){
        String label = "movie";
        nodeService.createNode(label, "hjm");
        nodeService.createNode("drama", "cpk");
        long sid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='hjm' return n").get(0).get("id")));
        long tid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='cpk' return n").get(0).get("id")));
        relationshipService.createRelationship(sid, tid, "kg666");
        graphService.deleteAll();
        int num=11;
        try(Session session = driver.session()){
            num = session.writeTransaction(tx -> tx.run("match (n) return n").list().size());
        }
        assertThat(num).isEqualTo(0);
    }

    @Test
    void importGraph(){
        File f = new File("src/main/resources/cypher.txt");
        try {
            MockMultipartFile file = new MockMultipartFile("file", new FileInputStream(f));
            ResponseVO responseVO = graphService.importGraph(file);
            assertThat(responseVO.getSuccess()).isEqualTo(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
