package kg666.data;


import kg666.po.DefaultLayout;
import kg666.po.NodeLayout;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DefaultLayoutMapper {
    @Select("select * from default_layout")
    List<DefaultLayout> getAll();

    @Select("select * from default_layout where pic_name = #{name} and uid=#{uid}")
    DefaultLayout getByName(String name, Long uid);

    @Insert("insert into default_layout(pic_name,uid,x,y,zoom,item_color,line_color,line_width,line_type,line_cur,label_show,label_fontsize,tooltip_show) " +
            "values(#{pic_name},#{uid},#{x},#{y},#{zoom},#{item_color},#{line_color},#{line_width},#{line_type},#{line_cur},#{label_show},#{label_fontsize},#{tooltip_show})")
    void insert(DefaultLayout defaultLayout);

    @Update("update default_layout set x=#{x}, y=#{y}, zoom=#{zoom}, item_color=#{item_color}, line_color=#{line_color}, line_width=#{line_width}, line_type=#{line_type}, line_cur=#{line_cur}, label_show=#{label_show}, label_fontsize=#{label_fontsize}, tooltip_show=#{tooltip_show} " +
            "where pic_name=#{pic_name} and uid=#{uid}")
    void update(DefaultLayout defaultLayout);

    @Delete("delete from default_layout")
    void deleteAll();
}
