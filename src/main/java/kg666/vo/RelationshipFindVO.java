package kg666.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RelationshipFindVO {
    String pic_name;
    Long uid;
    String name;
    String source;
    String target;
}
