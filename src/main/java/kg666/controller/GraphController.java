package kg666.controller;

import kg666.service.GraphService;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GraphController {

    @Autowired
    GraphService graphService;

    @GetMapping("/")
    public ResponseVO getGraph() {
        return graphService.getGraph();
    }

    @PostMapping("/import")
    public ResponseVO importGraph(@RequestPart("file") MultipartFile file) {
        return graphService.importGraph(file);
    }

    @DeleteMapping("deleteAll")
    public ResponseVO deleteGraph() {
        return graphService.deleteAll();
    }
}
