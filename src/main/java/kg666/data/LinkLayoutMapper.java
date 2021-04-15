package kg666.data;

import kg666.po.LinkLayout;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LinkLayoutMapper {
    @Select("select * from link_layout")
    List<LinkLayout> getAll();

    @Select("select * from link_layout where id = #{id}")
    LinkLayout getById(long id);

    @Insert("insert into link_layout(id,color,width,type,curveness,label_show,label_fontsize,tooltip_show) " +
            "values(#{id},#{color},#{width},#{type},#{curveness},#{label_show},#{label_fontsize},#{tooltip_show})")
    void insert(LinkLayout linkLayout);

    @Update("update link_layout set color=#{color}, width=#{width}, type=#{type}, curveness=#{curveness}, label_show=#{label_show}, label_fontsize=#{label_fontsize}, tooltip_show=#{tooltip_show} " +
            "where id=#{id}")
    void update(LinkLayout linkLayout);

    @Delete("delete from link_layout")
    void deleteAll();

}
