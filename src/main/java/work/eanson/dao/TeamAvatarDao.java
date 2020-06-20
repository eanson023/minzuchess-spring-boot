package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.TeamAvatar;

/**
 * @author eanson
 */
@Repository
public interface TeamAvatarDao {
    int deleteByPrimaryKey(Integer avatarId);

    int insert(TeamAvatar record);

    int insertSelective(TeamAvatar record);

    TeamAvatar selectByPrimaryKey(Integer avatarId);

    int updateByPrimaryKeySelective(TeamAvatar record);

    int updateByPrimaryKey(TeamAvatar record);

    TeamAvatar selectByForeignKey(String teamId);
}