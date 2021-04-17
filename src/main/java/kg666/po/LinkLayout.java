package kg666.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LinkLayout {
    private long id;
    private String pic_name;
    private long uid;
    private String color;
    private double width;
    private String type;
    private double curveness;
    private Boolean label_show;
    private double label_fontsize;
    private Boolean tooltip_show;
}
