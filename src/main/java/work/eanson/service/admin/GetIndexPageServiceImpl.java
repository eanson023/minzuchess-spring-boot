package work.eanson.service.admin;

import org.springframework.stereotype.Service;
import work.eanson.pojo.Category;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;

import java.util.List;

/**
 * @author eanson
 * @create 2020-03-29 17:44
 *
 *
 */
@Service("get_admin_index")
public class GetIndexPageServiceImpl extends BaseService implements GlobalService {

    @Override
    public void service(Context context) throws Exception {
        String randomCode = getRandomCode();
        context.put("code", randomCode);
        List<Category> categories = categoryDao.selectLetter();
        context.put("categories", categories);
    }
}
