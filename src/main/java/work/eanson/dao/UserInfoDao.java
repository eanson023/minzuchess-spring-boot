package work.eanson.dao;

import org.springframework.stereotype.Repository;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.back.page.dao.Find;
import work.eanson.pojo.extend.UserInfoExtend;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface UserInfoDao {
    int deleteByPrimaryKey(String telephone);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(String telephone);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    String selectPrimaryKeyByUUID(String UUID);

    int selectDateDiffToNow(String telephone);

    List<UserInfoExtend> selectAllJoinTimeDiffNow();

    UserInfoExtend selectUserInfo(String telephone);

    UserInfoExtend selectUserInfoAnno(String telephone);

    List<UserInfoExtend> selectAll(Find find);
}