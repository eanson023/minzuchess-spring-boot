package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.School;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface SchoolDao {
    int deleteByPrimaryKey(Integer schId);

    int insert(School record);

    int insertSelective(School record);

    School selectByPrimaryKey(Integer schId);

    int updateByPrimaryKeySelective(School record);

    int updateByPrimaryKey(School record);

    List<School> selectByForeignKey(Integer proId);
}