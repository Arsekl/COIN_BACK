package kg666.controller;

import kg666.service.NodeService;
import kg666.vo.NodeVO;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    NodeService nodeService;

    @PostMapping("/add")
    public ResponseVO addNode(@RequestBody NodeVO nodeVO) {
        return nodeService.createNode(nodeVO.getLabel(), nodeVO.getName());
    }

    @PutMapping("/update")
    public ResponseVO updateNode(@RequestBody NodeVO nodeVO) {
        return nodeService.updateNodeNameById(nodeVO.getLabel(), nodeVO.getId(), nodeVO.getName());
    }

    @DeleteMapping("/delete")
    public ResponseVO deleteNode(@RequestBody NodeVO nodeVO) {
        return nodeService.deleteNode(nodeVO.getLabel(), nodeVO.getId());
    }
}
