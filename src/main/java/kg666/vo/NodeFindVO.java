package kg666.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NodeFindVO {
    String pic_name;
    Long uid;
    String label;
    String name;
    Double lowerBound;
    Double upperBound;
}
