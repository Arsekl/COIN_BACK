package kg666.service;

import com.alibaba.fastjson.JSON;
import kg666.data.MyNeo4jDriver;
import kg666.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class GraphService {
    private final static String DRIVER_RUNNING_ERROR = "Something wrong happened with neo4j when cypher is running";
    private final static String IMPORT_ERROR = "Please check your cypher statement in file";
    @Autowired
    private MyNeo4jDriver driver;

    public ResponseVO getGraph() {
        try {
            List<HashMap<String, Object>> nodes = driver.getGraphNode("match (n) return n");
            List<HashMap<String, Object>> relationships = driver.getGraphRelationShip("match (n)-[r]->(m) return r");
            List<String> categoryNames = new ArrayList<>();
            List<HashMap<String, Object>> categories = new ArrayList<>();
            for (HashMap<String, Object> node : nodes) {
                String name = String.valueOf(node.get("category"));
                if (!categoryNames.contains(name)) {
                    HashMap<String, Object> category = new HashMap<>();
                    categoryNames.add(name);
                    node.replace("category", categoryNames.indexOf(name));
                    category.put("name", name);
                    categories.add(category);
                }
            }
            HashMap<String, Object> result = new HashMap<>();
            result.put("nodes", nodes);
            result.put("links", relationships);
            result.put("categories", categories);
            System.out.println(JSON.toJSONString(result));
            return ResponseVO.buildSuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO deleteAll() {
        String cypher = "match (n) detach delete n";
        try {
            driver.executeCypher(cypher);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
        }
    }

    public ResponseVO importGraph(MultipartFile file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            StringBuilder cypher = new StringBuilder();
            String result = bufferedReader.readLine();
            while ( result!= null) {
                cypher.append(result).append(" ");
                result = bufferedReader.readLine();
            }
            System.out.println(cypher.toString());
            driver.executeCypher(cypher.toString());
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(IMPORT_ERROR);
        }
    }

//    public ResponseVO getAllLabelCount() {
//        try {
//            String cypher = "match (n) return count (distinct labels(n)) as count";
//            Integer num = driver.getCount(cypher);
//            return ResponseVO.buildSuccess(num);
//        } catch (Exception e) {
//            return ResponseVO.buildFailure(DRIVER_RUNNING_ERROR);
//        }
//    }
}
