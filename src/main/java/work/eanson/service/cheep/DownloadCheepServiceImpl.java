package work.eanson.service.cheep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.CheepDao;
import work.eanson.pojo.Cheep;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;


/**
 * @author eanson
 * @create 2020-03-26 15:36
 */
@Service("download_cheep")
public class DownloadCheepServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private CheepDao cheepDao;

    @Input(required = "cheep_id")
    @Override
    public void service(Context context) throws Exception {
        String cheepId = String.valueOf(context.get("cheep_id"));
        Cheep cheep = cheepDao.selectByPrimaryKey(cheepId);
        if (cheep == null) {
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
        String path = cheep.getPath();
        String realName = cheep.getRealName();
        context.clear();
        context.put("path", path);
        context.put("real_name", realName);
        context.setResult(Result.success());
        context.put("msg", "下载棋谱");
    }
}
