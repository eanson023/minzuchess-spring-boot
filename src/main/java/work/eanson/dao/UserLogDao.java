package work.eanson.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import work.eanson.pojo.UserLog;
import work.eanson.pojo.back.page.dao.Find;
import work.eanson.pojo.extend.UserLogExtend;

import java.util.Date;
import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface UserLogDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLog record);

    int insertSelective(UserLog record);

    UserLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLog record);

    int updateByPrimaryKey(UserLog record);

    int selectIntervalTimeCountByForeignKey(@Param("userId") String userId, @Param("prefix") Integer prefix, @Param("suffix") Integer suffix);

    int selectUseCountInOneMonthByTelephone(@Param("userId") String userId);

    List<Date> selectOneDayTime(@Param("telephone") String telephone, @Param("day") Integer day);

//    List<UserLogExtend> selectAll();

    /**
     * 动态sql 查询全部
     *
     *
     * 关于$和#
     * https://blog.csdn.net/bwddd/article/details/80264697
     * 这里用了#,应该是用$.    这里因为我要查询 是根据一个数据库字段名 所以应该用$
     *
     *  ${ } 变量的替换阶段是在动态 SQL 解析阶段，而 #{ }变量的替换是在 DBMS 中
     * @param find 查询条件
     * @return
     */
    List<UserLogExtend> selectAll(Find find);
}