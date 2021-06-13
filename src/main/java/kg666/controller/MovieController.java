package kg666.controller;

import kg666.service.MovieService;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movie")
@CrossOrigin
public class MovieController {
    @Autowired
    MovieService service;
    @GetMapping("/person")
    public ResponseVO getPersonInfo(@RequestParam String name){
        return service.getPersonInfo(name);
    }

    @GetMapping("/info")
    public ResponseVO getMovieInfo(@RequestParam long id) {return service.getMovieInfo(id); }

    @GetMapping("/like")
    public ResponseVO likeMovie(@RequestParam long id, @RequestParam long uid) {return service.likeMovie(id, uid);}

    @GetMapping("/unlike")
    public ResponseVO unlikeMovie(@RequestParam long id, @RequestParam long uid) {return  service.unlikeMovie(id, uid);}

    @GetMapping("/recommend/u")
    public ResponseVO recommendMovieByUser(@RequestParam long uid){return service.getRecommendedMovieByUser(uid);}

    @GetMapping("/recommend/m")
    public ResponseVO recommendMovieByMovie(@RequestParam long id){return service.getRecommendedMovieByMovie(id);}

    @GetMapping("recommend/r")
    public ResponseVO recommendMovieByRandom() {return service.getRecommendedMovieByRandom();}

    @GetMapping("recommend/o")
    public ResponseVO recommendMovieByOther(@RequestParam long uid) {return service.getRecommendedMovieByOther(uid);}

    @GetMapping("/userdata")
    public ResponseVO getUserData(@RequestParam long uid){return service.getUserMovieData(uid);}
}
