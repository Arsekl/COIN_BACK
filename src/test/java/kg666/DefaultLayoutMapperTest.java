package kg666;

import kg666.data.DefaultLayoutMapper;
import kg666.data.NodeLayoutMapper;
import kg666.po.DefaultLayout;
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

@Testcontainers
@SpringBootTest
class DefaultLayoutMapperTest {
    @ClassRule
    public static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0").withInitScript("db/kg666.sql");

    @Autowired
    DefaultLayoutMapper mapper;


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
        List<DefaultLayout> result = mapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getByName() {
        DefaultLayout result = mapper.getByName("temp", 1L);
        assertThat(result.getUid()).isEqualTo(1);
    }

    @Test
    void insert() {
        DefaultLayout defaultLayout = new DefaultLayout("hjm", 1L ,10.0, 19.78, 0.8, "yellow", "red", 2.0, "dotted", 0.2, true, 14.0, true);
        mapper.insert(defaultLayout);
        List<DefaultLayout> result = mapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update() {
        DefaultLayout defaultLayout = new DefaultLayout("temp", 1L ,11.0, 20.23, 0.8, "yellow", "red", 2.0, "dotted", 0.2, true, 14.0, true);
        mapper.update(defaultLayout);
        DefaultLayout result = mapper.getByName("temp",1L);
        System.out.println(result);
    }
}