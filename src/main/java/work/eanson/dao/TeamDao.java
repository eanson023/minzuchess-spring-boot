package work.eanson.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import work.eanson.pojo.Team;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface TeamDao {
    int deleteByPrimaryKey(String teamId);

    int insert(Team record);

    int insertSelective(Team record);

    Team selectByPrimaryKey(String teamId);

    int updateByPrimaryKeySelective(Team record);

    int updateByPrimaryKey(Team record);

    Team selectByPrimaryKeyOrTeamName(String param);

    int selectTeamIsExistsByTeamName(String teamName);

    int selectTeamIsExistsByTeamId(String teamId);

    int checkIsLeaderJoinTeamUserByPrimaryKey(@Param("teamId") String teamId, @Param("telephone") String telephone);

    List<Team> selectTeamLike(String teamIdOrTeamName);

    int selectUserIsOneTeam(@Param("teamId") String teamId, @Param("userId") String userId);

    List<Team> selectTeamIdCreateTimeAll();

    /**
     * 查询加入的天数
     *
     * @param teamId
     * @return
     */
    int selectCreateTimeDiffByPrimaryKey(String teamId);

    Team selectTeamInfoLeftJoinTeamAvatarLeftJoinSchoolLeftJoinProvince(String teamId);

}