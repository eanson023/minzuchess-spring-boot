package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.Province;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface ProvinceDao {
    int deleteByPrimaryKey(Integer proId);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer proId);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);

    List<Province> selectAll();
}