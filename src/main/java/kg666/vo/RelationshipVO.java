package kg666.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RelationshipVO {
    private LineStyleVO lineStyle;
    private LabelVO label;
    private TooltipVO tooltip;
    private Long source;
    private Long target;
    private Long id;
    private String name;
}
