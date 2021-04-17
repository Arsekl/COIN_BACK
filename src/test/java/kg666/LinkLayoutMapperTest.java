package kg666;

import kg666.data.DefaultLayoutMapper;
import kg666.data.LinkLayoutMapper;
import kg666.po.DefaultLayout;
import kg666.po.LinkLayout;
import kg666.po.LinkLayout;
import kg666.po.NodeLayout;
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

@SpringBootTest
@Testcontainers
class LinkLayoutMapperTest {
    @ClassRule
    public static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0").withInitScript("db/layout.sql");

    @Autowired
    LinkLayoutMapper mapper;


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
    void getAll() {
        List<LinkLayout> result = mapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getById() {
        LinkLayout result = mapper.getById(0,0, "test");
        assertThat(result.getId()).isEqualTo(0);
    }

    @Test
    void insert() {
        LinkLayout nodeLayout = new LinkLayout(1, "test", 0,"blue", 2.0, "dotted", 0.5,false, 7, false);
        mapper.insert(nodeLayout);
        List<LinkLayout> result = mapper.getAll();
        System.out.println(result.get(1));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update() {
        LinkLayout nodeLayout = new LinkLayout(0, "test", 0, "yellow", 2.0, "dotted", 0.5,false, 7, false);
        mapper.update(nodeLayout);
        LinkLayout result = mapper.getById(0,0, "test");
        System.out.println(result);
        assertThat(result.getColor().equals("yellow")).isEqualTo(true);
    }
}