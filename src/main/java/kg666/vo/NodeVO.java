package kg666.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
public class NodeVO {
    private String label;
    private String name;
    private Long id;

//    public static void main(String[] args) {
//        System.out.println(JSON.toJSONString(new NodeVO("movie", "hjm", 0L)));
//    }


}


