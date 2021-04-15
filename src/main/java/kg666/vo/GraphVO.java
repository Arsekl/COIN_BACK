package kg666.vo;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GraphVO {
    String pic_name;
    Long uid;
    List<Double> center;
    Double zoom;
    ItemStyleVO itemStyle;
    LineStyleVO lineStyle;
    LabelVO label;
    TooltipVO tooltip;
    List<NodeVO> nodes;
    List<RelationshipVO> links;
    List<Map<String, String>> categories;
}
