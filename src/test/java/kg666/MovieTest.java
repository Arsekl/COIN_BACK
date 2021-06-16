package kg666;

import kg666.data.MyNeo4jDriver;
import kg666.service.MovieService;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class MovieTest {
    @Autowired
    MovieService service;

    @Autowired
    MyNeo4jDriver driver;

    @Test
    public void getPersonInfo(){
        HashMap<String, Object> result = (HashMap<String, Object>) service.getPersonInfo("尔冬升").getContent();
        assertThat(result.size()).isEqualTo(5);
        System.out.println(JSON.toString(result));
    }

    @Test
    public void getMovieInfo(){
        HashMap<String, Object> result = (HashMap<String, Object>) service.getMovieInfo(1).getContent();
        System.out.println(JSON.toString(result));
    }

    @Test
    public void getRandom(){
        System.out.println(JSON.toString(service.getRecommendedMovieByRandom().getContent()));
    }

    @Test
    void getA(){
        System.out.println(JSON.toString(service.getAnswerForQuestion("尔冬升出演的老片")));
        System.out.println(JSON.toString(service.getAnswerForQuestion("那他导演的有哪些")));
        System.out.println(JSON.toString(service.getAnswerForQuestion("英雄的上映时间")));
    }
}
