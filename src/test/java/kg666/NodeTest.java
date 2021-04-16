package kg666;



import kg666.data.MyNeo4jDriver;
import kg666.service.NodeService;
import kg666.vo.NodeFindVO;
import kg666.vo.NodeVO;
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
    public void NodeSaveTest() {

        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, label, "hjm", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null, label, "cpk", 1L, 20D);
        NodeVO node2 = new NodeVO(null,null,null,null,null,null, label, "lpy", 2L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        nodeService.createNode(node2);
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
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, label, "hjm", 0L, 20D);
        nodeService.createNode(node0);
//        long id = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) return n").get(0).get("id")));
        long id = 0L;
        nodeService.updateNodeById(id, "cpk", 20D);
        nodeService.updateNodeById(111, "cpk", 20D);
        Map<String, Object> node;
        try (Session session = driver.session()) {
            node = session.writeTransaction(tx -> {
                Result result = tx.run("match (n:movie)  return n");
                return result.list().get(0).values().get(0).asMap();
            });
        }
        assertThat(node.get("name")).isEqualTo("cpk");
    }

    @Test
    public void DeleteNodeTest(){
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, label, "hjm", 0L, 20D);
        nodeService.createNode(node0);
        long id = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) return n").get(0).get("id")));
        nodeService.deleteNode(id);
        nodeService.deleteNode(111);
        int num;
        try(Session session = driver.session()){
            num = session.readTransaction(tx -> tx.run("match (n) return n").list().size());
        }
        assertThat(num).isEqualTo(0);
    }

    @Test
    public void findNodesTest(){
        NodeFindVO nodeF0 = new NodeFindVO("temp", 0L, "movie", "hjm", null, null);
        NodeFindVO nodeF1 = new NodeFindVO("temp", 0L,"movie", null, null, null);
        NodeFindVO nodeF2 = new NodeFindVO("temp", 0L,null, "hjm", 15D, 25D);
        NodeFindVO nodeF3 = new NodeFindVO("temp", 0L,null, null, 15D, 25D);
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, "movie", "hjm0", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null, "movie", "hjm1", 1L, 40D);
        NodeVO node3 = new NodeVO(null,null,null,null,null,null, "drama", "hj2", 2L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        nodeService.createNode(node3);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "temp", 0));
        NodeVO nodeF = new NodeVO(null,null,null,null,null,null, "movie", "hjm0", 0L, 20D);
        nodeService.createNode(nodeF);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "temp", 1));
        System.out.println("----------------------------------------");
        List<HashMap<String,Object>> result = (List<HashMap<String,Object>>)nodeService.findNodes(nodeF0).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)nodeService.findNodes(nodeF1).getContent();
        assertThat(result.size()).isEqualTo(2);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)nodeService.findNodes(nodeF2).getContent();
        assertThat(result.size()).isEqualTo(1);
        System.out.println("----------------------------------------");
        result = (List<HashMap<String,Object>>)nodeService.findNodes(nodeF3).getContent();
        assertThat(result.size()).isEqualTo(2);
    }

}
