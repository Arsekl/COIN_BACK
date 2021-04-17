package kg666.data;

import kg666.po.NodeLayout;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NodeLayoutMapper {
    @Select("select * from node_layout")
    List<NodeLayout>  getAll();

    @Select("select * from node_layout where id = #{id} and  uid=#{uid} and pic_name=#{pic_name}")
    NodeLayout getById(long id, long uid, String pic_name);

    @Insert("insert into node_layout(id,pic_name,uid,x,y,color,symbol,label_show,label_fontsize,tooltip_show) " +
            "values(#{id},#{pic_name},#{uid},#{x},#{y},#{color},#{symbol},#{label_show},#{label_fontsize},#{tooltip_show})")
    void insert(NodeLayout nodeLayout);

    @Update("update node_layout set x=#{x}, y=#{y}, color=#{color}, symbol=#{symbol}, label_show=#{label_show}, label_fontsize=#{label_fontsize}, tooltip_show=#{tooltip_show} " +
            "where id=#{id} and  uid=#{uid} and  pic_name=#{pic_name}")
    void update(NodeLayout nodeLayout);

    @Delete("delete from node_layout")
    void deleteAll();
}
