package kg666;

import com.alibaba.fastjson.JSON;
import kg666.data.DefaultLayoutMapper;
import kg666.data.LinkLayoutMapper;
import kg666.data.MyNeo4jDriver;
import kg666.data.NodeLayoutMapper;
import kg666.service.GraphService;
import kg666.service.NodeService;
import kg666.service.RelationshipService;
import kg666.vo.GraphVO;
import kg666.vo.NodeVO;
import kg666.vo.RelationshipVO;
import kg666.vo.ResponseVO;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class GraphTest {
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

    @Autowired
    NodeLayoutMapper nodeLayoutMapper;

    @Autowired
    LinkLayoutMapper linkLayoutMapper;

    @Autowired
    DefaultLayoutMapper defaultLayoutMapper;

    @BeforeEach
    @AfterEach
    void deleteAll(){
        nodeLayoutMapper.deleteAll();
        linkLayoutMapper.deleteAll();
        defaultLayoutMapper.deleteAll();
    }

    @AfterEach
    void cleanUP() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH(n) DETACH DELETE n"));
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }


    @Test
    void getNumTest(){
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, label, "hjm", 0L, 20D);
        NodeVO node1 = new NodeVO(null,null,null,null,null,null, label, "cpk", 1L, 20D);
        nodeService.createNode(node0);
        nodeService.createNode(node1);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "temp", 0));
        assertThat(graphService.getNodeNum("temp", 0L).getContent()).isEqualTo(2);
        RelationshipVO relationship0 = new RelationshipVO(null,null,null,0L , 1L, 1L, "kg666");
        relationshipService.createRelationship(relationship0);
        assertThat(graphService.getLinkNum("temp", 0L).getContent()).isEqualTo(1);
    }

    @Test
    void DeleteAllTest(){
        String label = "movie";
        NodeVO node0 = new NodeVO(null,null,null,null,null,null, label, "hjm", 0L, 20D);
        nodeService.createNode(node0);
        label = "drama";
        NodeVO node1 = new NodeVO(null,null,null,null,null,null, label, "cpk", 1L, 20D);
        nodeService.createNode(node1);
        myNeo4jDriver.executeCypher(String.format("match (n) where n.pic_name is null and n.uid is null set n.pic_name='%s' set n.uid=%s", "temp", 0));
        long sid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='hjm' return n").get(0).get("id")));
        long tid = Long.parseLong(String.valueOf(myNeo4jDriver.getGraphNode("match (n) where n.name='cpk' return n").get(0).get("id")));
        RelationshipVO relationship0 = new RelationshipVO(null,null,null,sid, tid, 1L, "kg666");
        relationshipService.createRelationship(relationship0);
        graphService.deleteAll("temp", 0L);
        int num;
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
            ResponseVO responseVO = graphService.importGraphByCypher(file);
            assertThat(responseVO.getSuccess()).isEqualTo(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void saveAndGetGraphTest(){
        StringBuilder json = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream ("src/main/resources/test.json")));
            String temp = reader.readLine();
            while (temp!=null){
                json.append(temp);
                temp = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphVO graphVO = JSON.parseObject(json.toString(), GraphVO.class);
        graphService.saveGraph(graphVO);
        graphService.saveLayout(graphVO);
        graphService.getGraph("temp", 0L);
        graphService.getGraph("tmp", 0L);
    }

    @Test
    void saveLayoutTest(){
        StringBuilder json = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream ("src/main/resources/test.json")));
            String temp = reader.readLine();
            while (temp!=null){
                json.append(temp);
                temp = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphVO graphVO = JSON.parseObject(json.toString(), GraphVO.class);
        graphService.saveLayout(graphVO);
        graphService.saveLayout(graphVO);
        assertThat(nodeLayoutMapper.getAll().size()).isEqualTo(9);
        assertThat(linkLayoutMapper.getAll().size()).isEqualTo(18);
    }
}
