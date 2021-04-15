package kg666.data;

import kg666.po.NodeLayout;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NodeLayoutMapper {
    @Select("select * from node_layout")
    List<NodeLayout>  getAll();

    @Select("select * from node_layout where id = #{id}")
    NodeLayout getById(long id);

    @Insert("insert into node_layout(id,x,y,color,symbol,label_show,label_fontsize,tooltip_show) " +
            "values(#{id},#{x},#{y},#{color},#{symbol},#{label_show},#{label_fontsize},#{tooltip_show})")
    void insert(NodeLayout nodeLayout);

    @Update("update node_layout set x=#{x}, y=#{y}, color=#{color}, symbol=#{symbol}, label_show=#{label_show}, label_fontsize=#{label_fontsize}, tooltip_show=#{tooltip_show} " +
            "where id=#{id}")
    void update(NodeLayout nodeLayout);

    @Delete("delete from node_layout")
    void deleteAll();
}
