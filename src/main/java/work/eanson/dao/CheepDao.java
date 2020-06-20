package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.Cheep;
import work.eanson.pojo.extend.CheepExtend;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface CheepDao {
    int deleteByPrimaryKey(String cheepId);

    int insert(Cheep record);

    int insertSelective(Cheep record);

    Cheep selectByPrimaryKey(String cheepId);

    int updateByPrimaryKeySelective(Cheep record);

    int updateByPrimaryKey(Cheep record);

    List<CheepExtend> selectAllAboutInfo();

    String selectHtmlByPrimaryKey(String cheepId);
}