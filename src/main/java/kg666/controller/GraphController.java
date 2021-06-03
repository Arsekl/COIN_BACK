package kg666.controller;

import kg666.service.GraphService;
import kg666.vo.GraphVO;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class GraphController {

    @Autowired
    GraphService graphService;

    @GetMapping("/")
    public ResponseVO getGraph(@RequestParam String pic_name, @RequestParam Long uid) {
        return graphService.getGraph(pic_name, uid);
    }

    @GetMapping("/nodeNum")
    public ResponseVO getNodeNum(@RequestParam String pic_name, @RequestParam Long uid){
        return graphService.getNodeNum(pic_name, uid);
    }

    @GetMapping("/linkNum")
    public ResponseVO getLinkNum(@RequestParam String pic_name, @RequestParam Long uid){
        return graphService.getLinkNum(pic_name, uid);
    }

    @PostMapping("/save")
    public ResponseVO saveGraph(@RequestBody GraphVO graphVO) {
        graphService.deleteAll(graphVO.getPic_name(),graphVO.getUid());
        return graphService.saveGraph(graphVO);
    }

    @PostMapping("/saveLayout")
    public ResponseVO saveLayout(@RequestBody GraphVO graphVO) {
        return graphService.saveLayout(graphVO);
    }

    @DeleteMapping("/deleteAll")
    public ResponseVO deleteGraph(@RequestParam String pic_name, @RequestParam Long uid) {
        return graphService.deleteAll(pic_name, uid);
    }
}
