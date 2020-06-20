package work.eanson.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.Message;
import work.eanson.pojo.extend.TeamUserExtend;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface TeamUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TeamUser record);

    int insertSelective(TeamUser record);

    TeamUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeamUser record);

    int updateByPrimaryKey(TeamUser record);

    int updateUserIdAndTeamIdSelective(TeamUser teamUser);


    /**
     * 查询所有队伍信息+队长+省份或学校
     *
     * @return
     */
    List<TeamUser> selectAllTeamInfo();

    /**
     * 查找已加入的数量
     *
     * @return
     */
    int selectJoinedCountByTeamId(String team_id);

    /**
     * 查询队伍信息
     *
     * @param isLeader
     * @param userId
     * @return
     */
    List<TeamUser> selectInfoByUserIdIsLeader(@Param("isLeader") Boolean isLeader, @Param("userId") String userId);

    /**
     * 根据手机号查询 想加入的队伍
     *
     * @param userId
     * @return
     */
    List<Message> selectWantJoinTeam(@Param("userId") String userId);

    /**
     * 检查是否是队长
     */
    Boolean checkIsLeader(@Param("teamId") String teamId, @Param("telephone") String telephone);

    int deleteTeamUserByTeamId(String teamId);

    int selectWantJoinMegCount(String user_id);

    String selectLeaderByTeamId(String teamId);

    int deleteByTeamIdAndUserId(@Param("teamId") String teamId, @Param("userId") String userId);

    /**
     * 查询已加入的该队伍的所有成员 手机号和加入到此天数
     *
     * @param teamId
     * @return
     */
    List<TeamUser> selectOneTeamUserInfoByTeamId(String teamId);

    /**
     * 查询某队伍队员信息
     */
    List<TeamUserExtend> selectOneTeamUserInfo2ByTeamId(String teamId);
}