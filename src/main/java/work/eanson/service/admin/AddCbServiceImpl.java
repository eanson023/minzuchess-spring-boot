package work.eanson.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.pojo.ChessInfo;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;

/**
 * @author eanson
 * @create 2020-03-29 19:19
 * 添加新棋盘
 * 检测是否存在
 */
@Service("admin_add_cb")
public class AddCbServiceImpl extends BaseService implements GlobalService {

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Input(required = {"code", "category_key"})
    @Override
    public void service(Context context) throws Exception {
        String code = String.valueOf(context.get("code"));
        byte categoryKey = (byte) context.get("category_key");
        int i = chessInfoDao.selectIsExistByPrimaryKey(code);
        if (i != 0) {
            context.put("msg", "添加失败,该棋盘码也存在");
        }
        long alias = generateAliasCode();
        ChessInfo chessInfo = new ChessInfo();
        chessInfo.setClock("0Z:" + System.currentTimeMillis());
        chessInfo.setAlias(alias);
        String pos = categoryDao.selectInitPosByKey(categoryKey);
        chessInfo.setPos(pos);
        chessInfo.setCategory(categoryKey);
        chessInfo.setCode(code);
        chessInfo.setIsPublic(true);
        chessInfoDao.insertSelective(chessInfo);
        context.put("msg", "添加成功!棋盘码:" + code + "编码:" + alias);
    }
}
