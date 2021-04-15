package kg666;

import com.alibaba.fastjson.JSON;

import kg666.controller.GraphController;
import kg666.controller.NodeController;
import kg666.controller.RelationshipController;
import kg666.service.GraphService;
import kg666.vo.NodeVO;
import kg666.vo.RelationshipVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@WebMvcTest
@AutoConfigureMockMvc
public class AppApiTest {


    @MockBean
    NodeController nodeController;

    @MockBean
    RelationshipController relationshipController;

    @MockBean
    GraphService graphService;

    @Test
    void importGraphApi() throws Exception {
        GraphController graphController = new GraphController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(graphController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/import").contentType(MediaType.APPLICATION_JSON)
        .content("{\"nodes\":[{\"symbolSize\":40,\"name\":\"hjm\",\"id\":\"0\",\"category\":\"movie\"},{\"symbolSize\":40,\"name\":\"cpk\",\"id\":\"1\",\"category\":\"drama\"}],\"links\":[{\"name\":\"kg666\",\"id\":\"0\",\"source\":\"0\",\"target\":\"1\"}],\"categories\":[{\"name\":\"movie\"},{\"name\":\"drama\"}]}"))
        .andDo(print());
    }

}
