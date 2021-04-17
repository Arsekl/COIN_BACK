package kg666;

import com.mysql.cj.jdbc.Driver;
import kg666.data.NodeLayoutMapper;
import kg666.po.NodeLayout;
import kg666.service.NodeService;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class NodeLayoutMapperTest {

    @ClassRule
    public static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0").withInitScript("db/layout.sql");

    @Autowired
    NodeLayoutMapper mapper;


    @BeforeAll
    static void initializeMySQL(){
        mySQLContainer.start();
    }

    @DynamicPropertySource
    static void mySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }

    @Test
    public void getAll(){
        List<NodeLayout> result = mapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getById() {
        NodeLayout result = mapper.getById(0, 0, "test");
        assertThat(result.getId()).isEqualTo(0);
    }

    @Test
    void insert() {
        NodeLayout nodeLayout = new NodeLayout(1, "test", 0, 0.0, 2.0, "blue", "circle",false, 7.0, false);
        mapper.insert(nodeLayout);
        List<NodeLayout> result = mapper.getAll();
        System.out.println(result.get(1));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update() {
        NodeLayout nodeLayout = new NodeLayout(0, "test", 0,0.0, 2.0, "blue", "circle",false, 7.0, false);
        mapper.update(nodeLayout);
        NodeLayout result = mapper.getById(0, 0, "test");
        System.out.println(result);
        assertThat(result.getColor().equals("blue")).isEqualTo(true);
    }


}