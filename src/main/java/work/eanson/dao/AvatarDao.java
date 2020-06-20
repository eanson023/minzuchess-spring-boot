package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.Avatar;

/**
 * @author eanson
 */
@Repository
public interface AvatarDao {
    int deleteByPrimaryKey(Integer avatarId);

    int insert(Avatar record);

    int insertSelective(Avatar record);

    Avatar selectByPrimaryKey(Integer avatarId);

    int updateByPrimaryKeySelective(Avatar record);

    int updateByPrimaryKey(Avatar record);

    Avatar selectByForeignKey(String telephone);
}