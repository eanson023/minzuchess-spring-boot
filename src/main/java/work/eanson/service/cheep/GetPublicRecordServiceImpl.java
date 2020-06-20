package work.eanson.service.cheep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.CheepDao;
import work.eanson.pojo.extend.CheepExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;
import work.eanson.util.Result;

import java.util.List;

/**
 * @author eanson
 * @create 2020-03-26 14:29
 */
@Service("public_record")
public class GetPublicRecordServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private CheepDao cheepDao;

    @Override
    public void service(Context context) throws Exception {
        List<CheepExtend> cheepExtends = cheepDao.selectAllAboutInfo();
        context.setResult(Result.success(cheepExtends));
    }
}
