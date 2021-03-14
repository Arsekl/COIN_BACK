package kg666;

import kg666.service.NodeService;
import kg666.service.RelationshipService;
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
        nodeService.createNode(label,"hjm");
        nodeService.createNode(label,"cpk");
        relationshipService.createRelationship(0L, 1L, "kg666");
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
        nodeService.createNode(label,"hjm");
        nodeService.createNode(label,"cpk");
        relationshipService.createRelationship(0L, 1L, "kg666");
        relationshipService.updateRelationship(0, "kg777");
        Map<String, Object> node;
        try (Session session = driver.session()) {
            node = session.writeTransaction(tx -> {
                Result result = tx.run("match (n)-[r]->(m) where id(r)=0 return r");
                return result.list().get(0).values().get(0).asMap();
            });
        }
        assertThat(node.get("name")).isEqualTo("kg777");
    }

    @Test
    public void DeleteRelationshipTest(){
        String label = "movie";
        nodeService.createNode(label,"hjm");
        nodeService.createNode(label,"cpk");
        relationshipService.createRelationship(0L, 1L, "kg666");
        relationshipService.deleteRelationship(0);
        int num;
        try(Session session = driver.session()){
            num = session.readTransaction(tx -> tx.run("match (n)-[r]->(m) return r").list().size());
        }
        assertThat(num).isEqualTo(0);
    }
}
