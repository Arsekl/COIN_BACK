package kg666;

import com.alibaba.fastjson.JSON;

import kg666.controller.GraphController;
import kg666.controller.NodeController;
import kg666.controller.RelationshipController;
import kg666.service.GraphService;
import kg666.vo.NodeVO;
import kg666.vo.RelationshipVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@WebMvcTest
@AutoConfigureMockMvc
public class AppApiTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GraphController graphController;

    @MockBean
    NodeController nodeController;

    @MockBean
    RelationshipController relationshipController;

    @Test
    void getGraphApi() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/")).andDo(print());
    }

    @Test
    void createNodeApi() throws Exception{
        NodeVO nodeVO = new NodeVO("movie", "hjm", 0L);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/node/add")
                    .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                            .content(JSON.toJSONBytes(nodeVO))).andDo(print()).andExpect(status().isOk());
    }
}
