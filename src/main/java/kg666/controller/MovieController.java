package kg666.controller;

import kg666.service.MovieService;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    MovieService service;
    @PostMapping("/person")
    public ResponseVO getPersonInfo(@RequestParam String name){
        return service.getPersonInfo(name);
    }

    @PostMapping("/info")
    public ResponseVO getMovieInfo(@RequestParam long id) {return service.getMovieInfo(id); }

    @PostMapping("/like")
    public ResponseVO likeMovie(@RequestParam long id, @RequestParam long uid) {return service.likeMovie(id, uid);}

    @PostMapping("/unlike")
    public ResponseVO unlikeMovie(@RequestParam long id, @RequestParam long uid) {return  service.unlikeMovie(id, uid);}

    @PostMapping("/recommend/u")
    public ResponseVO recommendMovieByUser(@RequestParam long uid){return service.getRecommendedMovieByUser(uid);}

    @PostMapping("/recommend/m")
    public ResponseVO recommendMovieByMovie(@RequestParam long id){return service.getRecommendedMovieByMovie(id);}
}
