package kg666.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RelationshipVO {
    private Long sourceId;
    private Long targetId;
    private Long id;
    private String name;
}
