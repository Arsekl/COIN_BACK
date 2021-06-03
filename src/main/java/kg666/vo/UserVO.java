package kg666.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class UserVO {
    private long uid;
    private String name;
    private String password;
}
