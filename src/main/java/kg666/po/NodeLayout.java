package kg666.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Immutable;

@Getter
@AllArgsConstructor
@ToString
public class NodeLayout {
    private long id;
    private double x;
    private double y;
    private String color;
    private String symbol;
    private Boolean label_show;
    private Double label_fontsize;
    private Boolean tooltip_show;
}
