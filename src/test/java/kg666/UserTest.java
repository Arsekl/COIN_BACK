package kg666;

import kg666.data.UserMapper;
import kg666.po.User;
import kg666.service.UserService;
import kg666.vo.UserVO;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class UserTest {

    @ClassRule
    public static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0")
            .withInitScript("db/kg666.sql");

    @Autowired
    UserMapper mapper;

    @Autowired
    UserService service;


    @BeforeAll
    static void initializeMySQL() {
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
    public void getTest(){
        User test = mapper.getUser("admin");
        System.out.println(test);
        test = mapper.getUser("123");
        if (test==null) System.out.println("NULL");
        else System.out.println(test);
    }

    @Test
    public void insertTest(){
        UserVO userVO = new UserVO(2, "hjm", "123");
        mapper.insertUser(userVO.getName(), userVO.getPassword());
        System.out.println(mapper.getUser("hjm"));
    }

    @Test
    public void loginRegisterTest(){
        UserVO userVO1 = new UserVO(0, "Bob", "123");
        System.out.println(service.register(userVO1));
        UserVO userVO2 = new UserVO(0, "Bob", "1234");
        System.out.println(service.register(userVO2));
        System.out.println(service.login(userVO1));
        UserVO userVO3 = new UserVO(0, "Amy", "1234");
        System.out.println(service.login(userVO3));
    }
}
