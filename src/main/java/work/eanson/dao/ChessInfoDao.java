package work.eanson.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.back.page.dao.Find;
import work.eanson.pojo.extend.ChessInfoExtend;
import work.eanson.pojo.extend.ChessInfoExtend2;
import work.eanson.pojo.extend.ChessInfoExtend3;

import java.util.List;

/**
 * @author eanson
 */
@Repository
public interface ChessInfoDao {
    int deleteByPrimaryKey(String code);

    int insert(ChessInfo record);

    int insertSelective(ChessInfo record);

    ChessInfo selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(ChessInfo record);

    int updateByPrimaryKeyWithBLOBs(ChessInfo record);

    int updateByPrimaryKey(ChessInfo record);

    int selectIsExistByPrimaryKey(String code);

    ChessInfoExtend selectUserLoginByPrimaryKey(String code);

    String selectPosByPrimaryKey(String code);

    String selectClockByPrimaryKey(String code);

    String selectInitPosByPrimaryKeyJoinCategory(String code);

    int selectChessInfoIsExistByPrimaryKey(String code);

    /**
     * 检查是否是公开棋盘 和棋盘是否存在
     *
     * @param code
     * @return
     */

    Boolean selectIsPublicByPrimaryKey(String code);

    int updateIsPublicByPrimaryKeyAndForeignKey(@Param("code") String code, @Param("telephone") String telephone);

    /**
     * 检查棋盘码是否属于他
     *
     * @param code
     * @param telephone
     * @return
     */
    int selectCbCodeIsBeLongToOneUser(@Param("code") String code, @Param("telephone") String telephone);

    String selectCodeByAlias(String alias);

    String selectOneCbCode(String telephone);

    List<String> selectCodeByUserId(String telephone);


    List<ChessInfoExtend2> selectCodeAndNameByUserId(String telephone);

    List<ChessInfoExtend3> selectAll(Find find);

    Integer selectMaxAlias();

    String selectPosBePrimaryKey(String code);

}