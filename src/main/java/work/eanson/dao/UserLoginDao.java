package work.eanson.dao;


import org.springframework.stereotype.Repository;
import work.eanson.pojo.UserLogin;

/**
 * @author eanson
 */
@Repository
public interface UserLoginDao {

    int deleteByPrimaryKey(String telephone);

    int insert(UserLogin record);

    int insertSelective(UserLogin record);


    UserLogin selectByPrimaryKey(String telephone);

    int updateByPrimaryKeySelective(UserLogin record);

    int updateByPrimaryKey(UserLogin record);

    UserLogin selectByUserName(String username);

    Integer selectMaxUserName();

    String selectTelephoneByUserName(String username);

    int selectIsFinishRegUser(String telephone);
}