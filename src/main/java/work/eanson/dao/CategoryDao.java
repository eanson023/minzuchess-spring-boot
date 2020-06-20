package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.Category;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface CategoryDao {
    int deleteByPrimaryKey(Byte key);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Byte key);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    int selectCount();

    List<Category> selectAll();

    List<Category> selectLetter();

    int selectHtmlIsExist(String html);

    String selectInitPosByHtml(String html);

    String selectInitPos(int width);

    String selectHtmlByCode(String code);

    String selectValueByCode(String code);

    String selectInitPosByKey(Byte key);
}