package kg666.controller;

import kg666.service.RelationshipService;
import kg666.vo.RelationshipFindVO;
import kg666.vo.RelationshipVO;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/relationship")
@CrossOrigin
public class RelationshipController {
    @Autowired
    RelationshipService relationshipService;

//    @PostMapping("/add")
//    public ResponseVO addRelationship(@RequestBody RelationshipVO RelationshipVO) {
//        return relationshipService.createRelationship(RelationshipVO);
//    }
//
//    @PutMapping("/update")
//    public ResponseVO updateRelationship(@RequestBody RelationshipVO RelationshipVO) {
//        return relationshipService.updateRelationship(RelationshipVO.getId(), RelationshipVO.getName());
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseVO deleteRelationship(@RequestBody RelationshipVO RelationshipVO) {
//        return relationshipService.deleteRelationship(RelationshipVO.getId());
//    }

    @GetMapping("/find")
    public ResponseVO findRelationships(@RequestBody RelationshipFindVO relationshipFindVO){
        return relationshipService.findRelationships(relationshipFindVO);
    }
}
