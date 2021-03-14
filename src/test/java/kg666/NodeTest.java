package kg666;



import kg666.service.NodeService;
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
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

@Testcontainers
@SpringBootTest
public class NodeTest {

    private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.2");

    @BeforeAll
    static void initializeNeo4j() {

        neo4jContainer.start();
    }
    @Autowired
    NodeService nodeService;

    @Autowired
    Driver driver;

    @Autowired
    Neo4jTemplate neo4jTemplate;

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
    public void NodeSaveTest() {

        String label = "movie";
        nodeService.createNode(label,"hjm");
        nodeService.createNode(label,"cpk");
        nodeService.createNode(label,"lpy");
        int num;
        try(Session session = driver.session()){
            num = session.writeTransaction(tx -> {
                Result result = tx.run("match (n:movie) return n");
                    return result.list().size();                    });
        }
        assertThat(num).isEqualTo(3);

    }

    @Test
    public void UpdateNodeTest(){
        String label = "movie";
        nodeService.createNode(label,"hjm");
        nodeService.updateNodeNameById(label, 0L, "cpk");
        Map<String, Object> node;
        try (Session session = driver.session()) {
            node = session.writeTransaction(tx -> {
                Result result = tx.run("match (n:movie) where id(n)=0 return n");
                return result.list().get(0).values().get(0).asMap();
            });
        }
        assertThat(node.get("name")).isEqualTo("cpk");
    }

    @Test
    public void DeleteNodeTest(){
        String label = "movie";
        nodeService.createNode(label,"hjm");
        nodeService.deleteNode(label, 0);
        int num;
        try(Session session = driver.session()){
            num = session.readTransaction(tx -> tx.run("match (n) return n").list().size());
        }
        assertThat(num).isEqualTo(0);
    }
}
