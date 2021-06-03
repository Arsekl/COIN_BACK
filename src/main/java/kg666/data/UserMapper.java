package kg666.data;

import kg666.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where name=#{username}")
    User getUser(String username);

    @Insert("insert into user (name, password) values (#{username}, #{password})")
    void  insertUser(String username, String password);
}
