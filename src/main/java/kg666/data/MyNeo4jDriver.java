package kg666.data;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyNeo4jDriver {
    @Autowired
    private Driver driver;

    /**
     * Test if there is a open session
     *
     * @return Test result
     */
    public boolean isDriverOpen() {
        try {
            Session session = driver.session();
            return session.isOpen();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Execute given cypher statement
     *
     * @param cypher clause
     * @return a stream of result values
     */
    public List<Record> executeCypher(String cypher) {
        List<Record> records;
        try (Session session = driver.session()) {
            records = session.writeTransaction(tx -> {
                Result result = tx.run(cypher);
                if (result.hasNext()) return result.list();
                else return List.of();
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return records;
    }

    /**
     * Extract nodes from query's result and map into a list
     *
     * @param cypher clause
     * @return List with node's attributes inside
     */
    public List<HashMap<String, Object>> getGraphNode(String cypher) {
        List<HashMap<String, Object>> nodes = new ArrayList<>();
        try {
            List<Record> records = executeCypher(cypher);
            if (records.size() > 0) {
                for (Record record : records) {
                    List<Pair<String, Value>> fields = record.fields();
                    for (Pair<String, Value> pair : fields) {
                        HashMap<String, Object> res = new HashMap<>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NODE")) {
                            Node node = pair.value().asNode();
                            String label = node.labels().iterator().next();
                            Map<String, Object> map = node.asMap();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                res.put(key, entry.getValue());
                            }
                            res.put("category", label);
//                            System.out.println(res);
                            nodes.add(res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;
    }

    /**
     * Extract relationships from query's result and map into a list
     * There are redundant codes exist, may be able to be fix by design pattern
     *
     * @param cypher clause
     * @return List with relationship's attributes inside
     */
    public List<HashMap<String, Object>> getGraphRelationShip(String cypher) {
        List<HashMap<String, Object>> relationships = new ArrayList<>();
        try {
            List<Record> records = executeCypher(cypher);
            if (records.size() > 0) {
                for (Record recordItem : records) {
                    List<Pair<String, Value>> fields = recordItem.fields();
                    for (Pair<String, Value> pair : fields) {
                        HashMap<String, Object> res = new HashMap<>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("RELATIONSHIP")) {
                            Relationship relationship = pair.value().asRelationship();
//                            String label = relationship.type();
                            Map<String, Object> map = relationship.asMap();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                res.put(key, entry.getValue());
                            }
//                            res.put("type", label);
//                            System.out.println(res);
                            relationships.add(res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relationships;
    }


    public Integer getCount(String cypher){
        Integer result = null;
        try {
            List<Record> records = executeCypher(cypher);
            Record record = records.get(0);
            Value value = record.values().get(0);
            result = value.asInt();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public List<HashMap<String, Object>> getResult(String cypher){
        List<HashMap<String, Object>> result = new ArrayList<>();
        try {
            List<Record> records = executeCypher(cypher);
            if (records.size() > 0) {
                for (Record recordItem : records) {
                    List<Pair<String, Value>> fields = recordItem.fields();
                    HashMap<String, Object> res = new HashMap<>();
                    for (Pair<String, Value> pair : fields) {
                        Object value = null;
                        switch (pair.value().type().name()) {
                            case "INTEGER":
                                value = pair.value().asInt();
                                break;
                            case "STRING":
                                value = pair.value().asString();
                                break;
                            case "FLOAT":
                                value = pair.value().asFloat();
                                break;
                            case "DOUBLE":
                                value = pair.value().asDouble();
                                break;
                            case "NODE":
                                value = pair.value().asNode().asMap();
                                break;
                            case "LONG":
                                value = pair.value().asLong();
                                break;
                        }
                        res.put(pair.key(), value);
                    }
                    result.add(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

}
