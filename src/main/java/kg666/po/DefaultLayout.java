package kg666.po;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class DefaultLayout {
    private String pic_name;
    private Long uid;
    private Double x;
    private Double y;
    private Double zoom;
    private String item_color;
    private String line_color;
    private Double line_width;
    private String line_type;
    private Double line_cur;
    private Boolean label_show;
    private Double label_fontsize;
    private Boolean tooltip_show;
}
