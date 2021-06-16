package kg666.service;

import kg666.data.MyNeo4jDriver;
import kg666.data.QuestionMapper;
import kg666.util.NLP;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MovieService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";

    @Autowired
    MyNeo4jDriver driver;

    @Autowired
    NLP nlp;

    @Autowired
    QuestionMapper mapper;

    public ResponseVO getPersonInfo(String name){
        String play = String.format("match (p:Person{name:'%s'})-[:play]->(m:Movie) return m", name);
        String direct = String.format("match (p:Person{name:'%s'})-[:direct]->(m:Movie) return m", name);
        String write = String.format("match (p:Person{name:'%s'})-[:write]->(m:Movie) return m", name);
        String info = String.format("match (p:Person{name:'%s'}) return p", name);
        String genre = String.format("match (p:Person{name:'%s'})-[r]->(m:Movie)-[:is]->(g:Genre) return g.name as genre,count(*) as num", name);
        HashMap<String, Object> res = new HashMap<>();
        List<HashMap<String, Object>> playMovies = driver.getGraphNode(play);
        List<HashMap<String, Object>> directMovies = driver.getGraphNode(direct);
        List<HashMap<String, Object>> writeMovies = driver.getGraphNode(write);
        List<HashMap<String, Object>> personInfo = driver.getGraphNode(info);
        List<HashMap<String, Object>> genreData = driver.getResult(genre);
        res.put("info", personInfo);
        res.put("play", playMovies);
        res.put("direct", directMovies);
        res.put("write", writeMovies);
        res.put("genre", genreData);
        return ResponseVO.buildSuccess(res);
    }

    public ResponseVO getMovieInfo(long id){
        HashMap<String, Object> res = new HashMap<>();
        String info = String.format("match (m:Movie{id:%s}) return m", id);
        List<HashMap<String, Object>> movieInfo = driver.getGraphNode(info);
        res.put("info", movieInfo);
        String play = String.format("match (p:Person)-[:play]->(m:Movie{id:%s}) return p", id);
        List<String> players = new ArrayList<>();
        driver.getGraphNode(play).forEach((x)->players.add((String) x.get("name")));
        res.put("actor", players);
        String compose = String.format("match (p:Person)-[:write]->(m:Movie{id:%s}) return p", id);
        List<String> composers = new ArrayList<>();
        driver.getGraphNode(compose).forEach((x)->composers.add((String) x.get("name")));
        res.put("composer", composers);
        String direct = String.format("match (p:Person)-[:direct]->(m:Movie{id:%s}) return p", id);
        List<String> directors = new ArrayList<>();
        driver.getGraphNode(direct).forEach((x)->directors.add((String) x.get("name")));
        res.put("director", directors);
        String genre = String.format("match (m:Movie{id:%s})-[:is]->(g:Genre) return g", id);
        List<String> genres = new ArrayList<>();
        driver.getGraphNode(genre).forEach((x)->genres.add((String) x.get("name")));
        res.put("genre", genres);
        return ResponseVO.buildSuccess(res);
    }

    public ResponseVO getUserMovieData(long uid){
        String movie = String.format("match (:User{uid:%s})-[:like]->(m:Movie) return m", uid);
        List<HashMap<String, Object>> movies = driver.getGraphNode(movie);
        String genre = String.format("match (:User{uid:%s})-[:like]->(:Movie)-[:is]->(g:Genre) return g.name as genre,count(*) as num", uid);
        List<HashMap<String, Object>> genres = driver.getResult(genre);
        HashMap<String, Object> res = new HashMap<>();
        res.put("movie", movies);
        res.put("genre", genres);
        return ResponseVO.buildSuccess(res);
    }

    public ResponseVO likeMovie(long id, long uid){
        //这里的id是从公共电影图查出来的id
        String info = String.format("match (m:Movie{id:%s}) return m", id);
        List<HashMap<String, Object>> movieInfo = driver.getGraphNode(info);
        String name = (String) movieInfo.get(0).get("name");
        String play = String.format("match (p:Person)-[:play]->(m:Movie{id:%s}) return p", id);
        List<String> players = new ArrayList<>();
        driver.getGraphNode(play).forEach((x)->players.add((String) x.get("name")));
        String compose = String.format("match (p:Person)-[:write]->(m:Movie{id:%s}) return p", id);
        List<String> composers = new ArrayList<>();
        driver.getGraphNode(compose).forEach((x)->composers.add((String) x.get("name")));
        String direct = String.format("match (p:Person)-[:direct]->(m:Movie{id:%s}) return p", id);
        List<String> directors = new ArrayList<>();
        driver.getGraphNode(direct).forEach((x)->directors.add((String) x.get("name")));
        String genre = String.format("match (m:Movie{id:%s})-[:is]->(g:Genre) return g", id);
        List<String> genres = new ArrayList<>();
        driver.getGraphNode(genre).forEach((x)->genres.add((String) x.get("name")));
        StringBuilder cypher = new StringBuilder(String.format("merge (m:movie{name:'%s', uid:%s, pic_name:'movie', symbolSize:40, mid:%s}) ", name, uid, id));
        players.forEach((x)->cypher.append(String.format("merge (pp%s:person{name:'%s', uid:%s, pic_name:'movie', symbolSize:30}) merge (pp%s)-[:play{name:'play'}]->(m) ",players.indexOf(x), x, uid, players.indexOf(x))));
        directors.forEach((x)->cypher.append(String.format("merge (pd%s:person{name:'%s', uid:%s, pic_name:'movie', symbolSize:30}) merge (pd%s)-[:direct{name:'direct'}]->(m) ",directors.indexOf(x), x, uid, directors.indexOf(x))));
        composers.forEach((x)->cypher.append(String.format("merge (pc%s:person{name:'%s', uid:%s, pic_name:'movie', symbolSize:30}) merge (pc%s)-[:write{name:'write'}]->(m) ",composers.indexOf(x), x, uid, composers.indexOf(x))));
        genres.forEach((x)->cypher.append(String.format("merge(g%s:genre{name:'%s', uid:%s, pic_name:'movie', symbolSize:30}) merge (m)-[:is{name:'is'}]->(g%s) ",genres.indexOf(x), x, uid, genres.indexOf(x))));
        cypher.append(String.format("merge (a:Movie{id:%s}) merge (u:User{uid:%s}) merge(u)-[:like]->(a) ", id, uid));
        try {
            driver.executeCypher(cypher.toString());
            driver.executeCypher("match (a)-[r]->(b) where a.pic_name='movie' set a.id=id(a) set b.id=id(b) set r.source=id(startNode(r)) set r.target=id(endNode(r)) set r.id=id(r) ");
            return ResponseVO.buildSuccess();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO unlikeMovie(long id, long uid){
        //这里的id是用户个人电影图里的id
        try{
            driver.executeCypher(String.format("match (m:movie{id:%s, pic_name:'movie'}) match (u:User{uid:%s})-[r:like]->(n:Movie{id:m.mid}) detach delete m delete r",id, uid));
            driver.executeCypher("match (n) where not(n)--() and n.pic_name='movie' delete n");
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getRecommendedMovieByMovie(long id){
        //电影之间的相似度
        String cypher = "MATCH (m:Movie {id:%s})-[:is|play|write|direct]-(g)-[:is|play|write|direct]-(other:Movie) WITH m, other, COUNT(g) AS intersection, COLLECT(g.name) AS i MATCH (m)-[:is|play|write|direct]-(mg) WITH m,other, intersection,i, COLLECT(mg.name) AS s1 MATCH (other)-[:is|play|write|direct]-(og) WITH m,other,intersection,i, s1, COLLECT(og.name) AS s2 WITH m,other,intersection,s1,s2 WITH m,other,intersection,s1+[x IN s2 WHERE NOT x IN s1] AS union, s1, s2 WITH other, s1,s2,((1.0*intersection)/SIZE(union)) AS jaccard RETURN other ORDER BY jaccard DESC LIMIT 10";
        try{
            List<HashMap<String, Object>> movie = driver.getGraphNode(String.format(cypher, id));
            HashMap<String, Object> res = new HashMap<>();
            res.put("rec", movie);
            return ResponseVO.buildSuccess(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getRecommendedMovieByUser(long uid){
        //看得最多的题材中，从没看过里推荐
        String cypher = "MATCH (u:User{uid:%s})-[:like]->(m:Movie)-[:is]->(g:Genre) WITH u, g, COUNT(*) AS score, avg(m.rate) AS mean MATCH (g)<-[:is]-(rec:Movie) WHERE NOT EXISTS((u)-[:like]->(rec)) AND rec.rate>mean WITH rec AS recommendation,  SUM(score) AS sscore RETURN recommendation ORDER BY sscore DESC LIMIT 10";
        try{
            List<HashMap<String, Object>> movie = driver.getGraphNode(String.format(cypher, uid));
            HashMap<String, Object> res = new HashMap<>();
            res.put("rec", movie);
            return ResponseVO.buildSuccess(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getRecommendedMovieByRandom(){
        String cypher = "WITH toInteger(ceil(rand()*4587)) as r MATCH (m:Movie) where m.id<=r+9 and m.id>=r return m ";
        try{
            List<HashMap<String, Object>> movie = driver.getGraphNode(cypher);
            HashMap<String, Object> res = new HashMap<>();
            res.put("rec", movie);
            return ResponseVO.buildSuccess(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getRecommendedMovieByOther(long uid){
        String cypher = "match (u:User{uid:%s})-[:like]->(:Movie)<-[:like]-(o:User) where o.uid<>%s  match (o)-[:like]->(m:Movie) where not exists((u)-[:like]->(m)) with  distinct m as movies, count (m) as frequency return movies order by frequency desc limit 10";
        try{
            List<HashMap<String, Object>> movie = driver.getGraphNode(String.format(cypher, uid, uid));
            HashMap<String, Object> res = new HashMap<>();
            res.put("rec", movie);
            return ResponseVO.buildSuccess(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO getAnswerForQuestion(String question){
        String cypher = nlp.analysisQuery(question);
        try{
            List<HashMap<String, Object>> res = driver.getResult(cypher);
            return ResponseVO.buildSuccess(res);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO feedBack(String question){
        mapper.insertQuestion(question);
        return ResponseVO.buildSuccess();
    }
}
