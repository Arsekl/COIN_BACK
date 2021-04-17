package kg666.controller;

import kg666.service.NodeService;
import kg666.vo.NodeFindVO;
import kg666.vo.NodeVO;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/node")
@CrossOrigin
public class NodeController {

    @Autowired
    NodeService nodeService;

//    @PostMapping("/add")
//    public ResponseVO addNode(@RequestBody NodeVO nodeVO) {
//        return nodeService.createNode(nodeVO);
//    }
//
//    @PutMapping("/update")
//    public ResponseVO updateNode(@RequestBody NodeVO nodeVO) {
//        return nodeService.updateNodeById(nodeVO.getId(), nodeVO.getName(), nodeVO.getSymbolSize());
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseVO deleteNode(@RequestBody NodeVO nodeVO) {
//        return nodeService.deleteNode(nodeVO.getId());
//    }

    @GetMapping("/find")
    public ResponseVO findNodes(@RequestBody NodeFindVO nodeFindVO){
        return nodeService.findNodes(nodeFindVO);
    }
}
