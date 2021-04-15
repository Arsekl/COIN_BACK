package kg666.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;


@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NodeVO {
    private Double x;
    private Double y;
    private String symbol;
    private ItemStyleVO itemStyle;
    private LabelVO label;
    private TooltipVO tooltip;
    private String category;
    private String name;
    private Long id;
    private Double symbolSize;


//    public static void main(String[] args) {
//        System.out.println(JSON.toJSONString(new NodeVO("movie", "hjm", 0L)));
//    }


}


