package work.eanson.service.cheep;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import work.eanson.dao.CategoryDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.back.UserSession;
import work.eanson.pojo.extend.TrickExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 导出棋谱
 * 以绝对路径存储文件 相对路径存储路径
 *
 * @author eanson
 */
@Service("output_cheep")
public class OutputCheepServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;

    @Autowired
    private CategoryDao categoryDao;

    @Value("${my.tmp-cheep-path}")
    private String reactivePath;

    @Input(required = {"from", "to", "code"})
    @Override
    public void service(Context context) throws Exception {
        String code = String.valueOf(context.get("code"));
        Date from = (Date) context.get("from");
        Date to = (Date) context.get("to");
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        int i1 = chessInfoDao.selectChessInfoIsExistByPrimaryKey(code);
        if (i1 == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_404));
            return;
        }
        int i = chessInfoDao.selectCbCodeIsBeLongToOneUser(code, telephone);
        if (i == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_CODE_NOT_BELONG));
            return;
        }
        //如果比到大 交换  防止用户胡乱输入
        if (from.getTime() > to.getTime()) {
            Date tmp = from;
            from = to;
            to = tmp;
        }
        List<TrickExtend> trickExtends = trickDao.selectTrickInfoOrderTimeLimitTime(from, to, code);
        String name = categoryDao.selectValueByCode(code);
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String format1 = sf.format(from);
        String format2 = sf.format(to);
        //获取到绝对路径 绝对路径
        String absolutePath = OutputCheepServiceImpl.class.getResource(reactivePath).getPath();
        String fileName = getUUID();
        String realName = name + "_" + format1 + "__" + format2 + ".txt";
        File file = new File(absolutePath, fileName);
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            int w = 1;
            int b = 1;
            StringBuilder sb;
            //组装棋谱信息
            for (TrickExtend trickExtend : trickExtends) {
                sb = new StringBuilder();
                if ("z".equals(trickExtend.getColor())) {
                    sb.append('W').append(w++).append(':');
                } else if ("Z".equals(trickExtend.getColor())) {
                    sb.append('B').append(b++).append(':');
                } else {
                    sb.append("--").append(':');
                }
                sb.append(trickExtend.getTrick()).append('|');
                String createTime = trickExtend.getCreateTime();
                sb.append(createTime.substring(createTime.lastIndexOf(" ") + 1));
                if (trickExtend.getIsFalse()) {
                    sb.append('|').append(trickExtend.getMessage());
                }
                bw.write(sb.toString(), 0, sb.length());
                bw.newLine();
            }
        }
        //传回绝对路径
        context.setResult(Result.success(file.getAbsolutePath()));
        context.put("real_name", realName);
        context.put("msg", "导出棋谱");
    }
}
