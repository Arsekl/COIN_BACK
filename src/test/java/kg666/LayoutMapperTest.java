package kg666;

import kg666.data.DefaultLayoutMapper;
import kg666.data.LinkLayoutMapper;
import kg666.data.NodeLayoutMapper;
import kg666.data.UserMapper;
import kg666.po.DefaultLayout;
import kg666.po.LinkLayout;
import kg666.po.NodeLayout;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class LayoutMapperTest {
    @ClassRule
    public static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0").withInitScript("db/kg666.sql");

    @Autowired
    DefaultLayoutMapper defaultLayoutMapper;

    @Autowired
    NodeLayoutMapper nodeLayoutMapper;

    @Autowired
    LinkLayoutMapper linkLayoutMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService service;


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
    void getAll0() {
        List<DefaultLayout> result = defaultLayoutMapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getByName() {
        DefaultLayout result = defaultLayoutMapper.getByName("temp", 1L);
        assertThat(result.getUid()).isEqualTo(1);
    }

    @Test
    void insert0() {
        DefaultLayout defaultLayout = new DefaultLayout("hjm", 1L ,10.0, 19.78, 0.8, "yellow", "red", 2.0, "dotted", 0.2, true, 14.0, true);
        defaultLayoutMapper.insert(defaultLayout);
        List<DefaultLayout> result = defaultLayoutMapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update0() {
        DefaultLayout defaultLayout = new DefaultLayout("temp", 1L ,11.0, 20.23, 0.8, "yellow", "red", 2.0, "dotted", 0.2, true, 14.0, true);
        defaultLayoutMapper.update(defaultLayout);
        DefaultLayout result = defaultLayoutMapper.getByName("temp",1L);
        System.out.println(result);
    }

    @Test
    public void getAll(){
        List<NodeLayout> result = nodeLayoutMapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getById() {
        NodeLayout result = nodeLayoutMapper.getById(0, 1, "test");
        assertThat(result.getId()).isEqualTo(0);
    }

    @Test
    void insert() {
        NodeLayout nodeLayout = new NodeLayout(1, "test", 1, 0.0, 2.0, "blue", "circle",false, 7.0, false);
        nodeLayoutMapper.insert(nodeLayout);
        List<NodeLayout> result = nodeLayoutMapper.getAll();
        System.out.println(result.get(1));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update() {
        NodeLayout nodeLayout = new NodeLayout(0, "test", 1,0.0, 2.0, "blue", "circle",false, 7.0, false);
        nodeLayoutMapper.update(nodeLayout);
        NodeLayout result = nodeLayoutMapper.getById(0, 1, "test");
        System.out.println(result);
        assertThat(result.getColor().equals("blue")).isEqualTo(true);
    }

    @Test
    void getAll1() {
        List<LinkLayout> result = linkLayoutMapper.getAll();
        System.out.println(result.get(0));
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getById1() {
        LinkLayout result = linkLayoutMapper.getById(0,1, "test");
        assertThat(result.getId()).isEqualTo(0);
    }

    @Test
    void insert1() {
        LinkLayout nodeLayout = new LinkLayout(1, "test", 1,"blue", 2.0, "dotted", 0.5,false, 7, false);
        linkLayoutMapper.insert(nodeLayout);
        List<LinkLayout> result = linkLayoutMapper.getAll();
        System.out.println(result.get(1));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update1() {
        LinkLayout nodeLayout = new LinkLayout(0, "test", 1, "yellow", 2.0, "dotted", 0.5,false, 7, false);
        linkLayoutMapper.update(nodeLayout);
        LinkLayout result = linkLayoutMapper.getById(0,1, "test");
        System.out.println(result);
        assertThat(result.getColor().equals("yellow")).isEqualTo(true);
    }
    @Test
    public void getTest(){
        User test = userMapper.getUser("admin");
        System.out.println(test);
        test = userMapper.getUser("123");
        if (test==null) System.out.println("NULL");
        else System.out.println(test);
    }

    @Test
    public void insertTest(){
        UserVO userVO = new UserVO(2, "hjm", "123");
        userMapper.insertUser(userVO.getName(), userVO.getPassword());
        System.out.println(userMapper.getUser("hjm"));
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