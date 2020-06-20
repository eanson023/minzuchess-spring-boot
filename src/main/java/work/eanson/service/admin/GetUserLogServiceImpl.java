package work.eanson.service.admin;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.UserLogDao;
import work.eanson.pojo.back.page.*;
import work.eanson.pojo.back.page.dao.Find;
import work.eanson.pojo.extend.UserLogExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author eanson
 * @create 2020-03-28 15:28
 * <p>
 * 用户操作日志 分页 模糊查询
 */
@Service("get_user_log")
public class GetUserLogServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private UserLogDao userLogDao;

    @Input(required = {"data_tables_in"})
    @Override
    public void service(Context context) throws Exception {
        DataTablesIn dti = (DataTablesIn) context.get("data_tables_in");
        int start = dti.getStart();
        int length = dti.getLength();
        int page = start / length + 1;
        //找排序
        Set<work.eanson.pojo.back.page.dao.Order> orders = null;
        if (!dti.getOrder().isEmpty()) {
            orders = new HashSet<>(8);
            for (Order order : dti.getOrder()) {
                int index = order.getColumn();
                Column column = dti.getColumns().get(index);
                String columnName = column.getData();
                //驼峰转换
                if ("createTime".equals(columnName)) {
                    //这是真正字段名
                    columnName = "execute_time";
                } else {
                    columnName = toLowerCaseAddUnderline(columnName);
                }
                String orderBy = order.getDir();
                //数据库 mybatis动态sql
                work.eanson.pojo.back.page.dao.Order orderD = new work.eanson.pojo.back.page.dao.Order(columnName, orderBy);
                orders.add(orderD);
            }
        }
        //找搜索
        work.eanson.pojo.back.page.dao.Search searchD = null;
        //数据库 mybatis动态sql
        if (!"".equals(dti.getSearch().getValue())) {
            searchD = new work.eanson.pojo.back.page.dao.Search();
            Set<String> columnNames = new HashSet<>(8);
            Search search = dti.getSearch();
            String value = search.getValue();
            searchD.setValue(value);
            List<Column> columns = dti.getColumns();
            for (Column column : columns) {
                if (column.isSearchable()) {
                    //转小写
                    columnNames.add(toLowerCaseAddUnderline(column.getData()));
                }
            }
            searchD.setColumnNames(columnNames);
        }
        //组装条件
        Find find = new Find(orders, searchD);
        PageHelper.startPage(page, length);
        List<UserLogExtend> userLogExtends = userLogDao.selectAll(find);
        context.clear();
        context.setResult(Result.success(new DataTablesBack<>(userLogExtends)));
        context.put("msg", "管理员查看用户日志");
    }
}
