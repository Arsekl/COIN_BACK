package kg666.data;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
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
     * @param cypher
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
            throw e;
        }
        return records;
    }

    /**
     * Extract nodes from query's result and map into a list
     *
     * @param cypher
     * @return List with node's attributes inside
     */
    public List<HashMap<String, Object>> getGraphNode(String cypher) {
        List<HashMap<String, Object>> nodes = new ArrayList<>();
        try {
            List<Record> records = executeCypher(cypher);
            if (records.size() > 0) {
                for (Record record : records) {
                    /**
                     * 这里用不到record的key值，待测试
                     */
                    List<Pair<String, Value>> fields = record.fields();
                    for (Pair<String, Value> pair : fields) {
                        HashMap<String, Object> node = new HashMap<>();
                        String typeName = pair.value().type().name();
                        if (typeName.equals("NODE")) {
                            Node noe4jNode = pair.value().asNode();
                            String id = String.valueOf(noe4jNode.id());
                            String label = noe4jNode.labels().iterator().next();
                            Map<String, Object> map = noe4jNode.asMap();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                node.put(key, entry.getValue());
                            }
                            node.put("id", id);
                            node.put("category", label);
                            node.put("symbolSize", 20);
                            System.out.println(node);
                            nodes.add(node);
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
     * @param cypher
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
                            String id = String.valueOf(relationship.id());
                            String sourceId = String.valueOf(relationship.startNodeId());
                            String targetId = String.valueOf(relationship.endNodeId());
                            Map<String, Object> map = relationship.asMap();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                String key = entry.getKey();
                                res.put(key, entry.getValue());
                            }
                            res.put("id", id);
                            res.put("source", sourceId);
                            res.put("target", targetId);
                            System.out.println(res);
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

    public Integer getCount(String cypher) {
        Integer count = 0;
        try {
            List<Record> records = executeCypher(cypher);
            if (records.size()>0) {
                Record record = records.get(0);
                for (Value value : record.values()) {
                    String t = value.type().name();
                    if (t.equals("INTEGER")) {
                        count = Integer.valueOf(value.toString());
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Remove double quotation mark in string which is transformed by JSON
     *
     * @param jsonStr
     * @return result
     */
    public String getFilterPropertiesJson(String jsonStr) {
        String propertiesString = jsonStr.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        return propertiesString;
    }

//    /**
//     * Use reflect to transform Object to Sql of String type
//     * @param obj
//     * @param <T>
//     * @return Sql list
//     */
//    public <T> String getKeyAndValueCyphersql(T obj) {
//        Map<String, Object> map = new HashMap<>();
//        List<String> sqlList = new ArrayList<>();
//        Class objClass = obj.getClass();
//        Field[] fields = objClass.getDeclaredFields();
//        for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];
//            Class type = field.getType();
//            field.setAccessible(true);
//            Object val;
//            try {
//                val = field.get(obj);
//                if (val == null) {
//                    val = "";
//                }
//                String sql = "";
//                String key = field.getName();
//                System.out.println("key:" + key + "type:" + type);
//                if (val instanceof Integer) {
//                    map.put(key, val);
//                    sql = "n." + key + "=" + val;
//                } else if (val instanceof String[]) {
//                    String[] arr = (String[]) val;
//                    String v = "";
//                    for (int j = 0; j < arr.length; j++) {
//                        arr[j] = "'" + arr[j] + "'";
//                    }
//                    v = String.join(",", arr);
//                    sql = "n." + key + "=[" + v + "]";
//                } else if (val instanceof List) {
//                    List<String> arr = (ArrayList<String>) val;
//                    List<String> temp = new ArrayList<>();
//                    String v = "";
//                    for (String s : arr) {
//                        s = "'" + s + "'";
//                        temp.add(s);
//                    }
//                    v = String.join(",", temp);
//                    sql = "n." + key + "=[" + v + "]";
//                } else {
//                    map.put(key, val);
//                    sql = "n." + key + "='" + val + "'";
//                }
//
//                sqlList.add(sql);
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        String finalsql = String.join(",", sqlList);
//        System.out.println(finalsql);
//        System.out.println("单个对象的所有键值==反射==" + map.toString());
//        return finalsql;
//    }

//    public static void main(String[] args) {
//        EntityVO entity = new EntityVO();
//        entity.setUuid(1223L);
//        entity.setName("hjm");
//        System.out.println(new Neo4jDriver().getFilterPropertiesJson(JSON.toJSONString(entity)));
//    }

}
