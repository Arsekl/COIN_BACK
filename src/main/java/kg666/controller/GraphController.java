package kg666.controller;

import kg666.service.GraphService;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

    @Autowired
    GraphService graphService;

    @GetMapping("/")
    public ResponseVO getGraph(){
        return graphService.getGraph();
    }
}
