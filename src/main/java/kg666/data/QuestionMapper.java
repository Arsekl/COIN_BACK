package kg666.data;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (question) values(#{question})")
    void insertQuestion(String question);

    @Select("select * from question")
    List<String> getAllQuestion();
}
