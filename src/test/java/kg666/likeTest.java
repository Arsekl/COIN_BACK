package kg666;

import kg666.data.MyNeo4jDriver;
import kg666.service.GraphService;
import kg666.service.MovieService;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class likeTest {
    private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.2");

    @Autowired
    MovieService service;

    @Autowired
    Driver driver;

    @Autowired
    MyNeo4jDriver myNeo4jDriver;

    @Autowired
    GraphService graphService;

    @BeforeAll
    static void initializeNeo4j() {

        neo4jContainer.start();
    }

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
    public void likeTest() {
        myNeo4jDriver.executeCypher("create (p:Person{name:'hjm'}) create((p0:Person{name:'cpk'})) create (m:Movie{name:'123', showtime:2015, length:135, rate: 7.4}) create((m0:Movie{name:'321', showtime:1998, length:100, rate: 4})) create (g:Genre{name:'abc'}) create ((g0:Genre{name:'cba'})) create (m)-[:is]->(g) create (m0)-[:is]->(g0) create (p)-[:play]->(m) create (p)-[:write]->(m) create(p0)-[:direct]->(m0) create(p0)-[:write]->(m) ");
        myNeo4jDriver.executeCypher("match (n) where n.id is null set n.id=id(n)");
        service.likeMovie(2, 1);
        int num;
        num = myNeo4jDriver.getCount("match (n) where n.pic_name='movie' return count(n)");
        assertThat(num).isEqualTo(4);
        HashMap<String, Object> res = new HashMap<>();
//        res.put("nodes", myNeo4jDriver.getGraphNode("match (n) where n.pic_name='movie' return n"));
//        System.out.println(JSON.toString(res));
        num = myNeo4jDriver.getCount("match (n)-[r]->() where n.pic_name='movie' return count(r)");
        assertThat(num).isEqualTo(4);
        service.likeMovie(3, 1);
        num = myNeo4jDriver.getCount("match (n) where n.pic_name='movie' return count(n)");
        assertThat(num).isEqualTo(6);
        num = myNeo4jDriver.getCount("match (n:User) where n.uid=1 return count(n)");
        assertThat(num).isEqualTo(1);
        num = myNeo4jDriver.getCount("match (n:User)-[r:like]->(:Movie) where n.uid=1 return count(r)");
        assertThat(num).isEqualTo(2);
        num = myNeo4jDriver.getCount("match (n)-[r]->() where n.pic_name='movie' return count(r)");
        assertThat(num).isEqualTo(6);
        service.likeMovie(2, 2);
        service.unlikeMovie(6, 1);
        num = myNeo4jDriver.getCount("match (n:User)-[r:like]->(:Movie) where n.uid=1 return count(r)");
        assertThat(num).isEqualTo(1);
        num = myNeo4jDriver.getCount("match (n:User)-[r:like]->(:Movie) where n.uid=2 return count(r)");
        assertThat(num).isEqualTo(1);
        num = myNeo4jDriver.getCount("match (n) where n.pic_name='movie' and n.uid=1 return count(n)");
        assertThat(num).isEqualTo(3);
        num = myNeo4jDriver.getCount("match (n:Movie) return count(n)");
        assertThat(num).isEqualTo(2);
        graphService.getGraph("movie",2L);
        service.likeMovie(2, 1);
        System.out.println(JSON.toString(service.getUserMovieData(1).getContent()));
        System.out.println(JSON.toString(service.getRecommendedMovieByMovie(2).getContent()));
        System.out.println(JSON.toString(service.getRecommendedMovieByUser(2).getContent()));
        System.out.println(JSON.toString(service.getRecommendedMovieByOther(2).getContent()));
    }

}
