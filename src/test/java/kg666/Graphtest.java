package kg666;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

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
        assertThat(responseVO).isNotNull();

    }

}
