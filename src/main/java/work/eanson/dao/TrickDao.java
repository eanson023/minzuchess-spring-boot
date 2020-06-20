package work.eanson.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import work.eanson.pojo.Trick;
import work.eanson.pojo.back.page.dao.Find;
import work.eanson.pojo.extend.TrickExtend;

import java.util.Date;
import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface TrickDao {
    int deleteByPrimaryKey(String logId);

    int insert(Trick record);

    int insertSelective(Trick record);

    Trick selectByPrimaryKey(String logId);

    int updateByPrimaryKeySelective(Trick record);

    int updateByPrimaryKey(Trick record);

    List<TrickExtend> selectInfoLimit(@Param("code") String code, @Param("prefix") Integer prefix, @Param("num") Integer num);

    int selectCountByForeignKey(String code);

    String selectBeforeOderByTimeDesc(String code);

    /**
     * 业务需求 直接组装成list
     *
     * @param logId
     * @return
     */
    List<TrickExtend> selectByPrimaryKey2One(String logId);

    /**
     * 查询某一天的时间
     *
     * @param telephone
     * @param day
     * @return
     */
    List<Date> selectOneDayTime(@Param("telephone") String telephone, @Param("day") Integer day);

    int selectCountByHour(@Param("telephone") String telephone, @Param("prefix") Integer prefix, @Param("suffix") Integer suffix);

    /**
     * 查询近一个月每天是否使用棋盘的总数
     *
     * @param telephone
     * @return
     */
    int selectUseCountInOneMonthByTelephone(@Param("telephone") String telephone);

    /**
     * 分页
     */
    List<TrickExtend> selectTrickInfoOrderTime(String code);

    /**
     * 分页 时间限制
     */
    List<TrickExtend> selectTrickInfoOrderTimeLimitTime(@Param("from") Date from, @Param("to") Date to, @Param("code") String code);

    /**
     * 时间区间数量
     */
    int selectCountLimitTime(@Param("from") Date from, @Param("to") Date to, @Param("code") String code);

    /**
     * 区间内是状态
     *
     * @param from
     * @param to
     * @param code
     * @return
     */
    List<Trick> selectPosesOrderTimeLimitTime(@Param("from") Date from, @Param("to") Date to, @Param("code") String code);

    /**
     * @param find
     * @return
     */
    List<TrickExtend> selectAllOrOne(Find find);

}