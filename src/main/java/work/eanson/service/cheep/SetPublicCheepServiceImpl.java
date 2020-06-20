package work.eanson.service.cheep;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.CheepDao;
import work.eanson.dao.TrickDao;
import work.eanson.pojo.Cheep;
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
 * @author eanson
 * @create 2020-03-26 10:56
 * <p>
 * 设置公共棋谱
 * 生成具体文件:采用绝对路径
 */
@Service("set_public")
public class SetPublicCheepServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TrickDao trickDao;
    @Autowired
    private CheepDao cheepDao;

    @Value("${my.public-cheep-path}")
    private String publicCheepPath;

    @Input(required = {"from", "to", "cheep"})
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void service(Context context) throws Exception {
        // code name
        Cheep cheep = (Cheep) context.get("cheep");
        String code = cheep.getCode();
        if (code == null) {
            context.clear();
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
        if (cheep.getName() == null) {
            context.clear();
            context.setResult(Result.fail(MsgCenter.ERROR_PARAMS));
            return;
        }
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
        //如果比到大 交换
        if (from.getTime() > to.getTime()) {
            Date tmp;
            tmp = from;
            from = to;
            to = tmp;
        }
        int count = trickDao.selectCountLimitTime(from, to, code);
        if (count == 0) {
            context.setResult(Result.fail(MsgCenter.ERROR_INTERNAL_NOTRICK));
            return;
        }
        List<TrickExtend> trickExtends = trickDao.selectTrickInfoOrderTimeLimitTime(from, to, code);
        String name = categoryDao.selectValueByCode(code);
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String format1 = sf.format(from);
        String format2 = sf.format(to);
        //拿到绝对路径
        String directoryPath = SetPublicCheepServiceImpl.class.getResource(publicCheepPath).getPath();
        String fileName = getUUID();
        cheep.setCheepId(fileName);
        try (FileWriter fw = new FileWriter(new File(directoryPath, fileName));
             BufferedWriter bw = new BufferedWriter(fw)) {
            int w = 1;
            int b = 1;
            StringBuilder sb;
            //组装棋谱信息
            for (TrickExtend trickExtend : trickExtends) {
//                if (trickExtend.getStatus() > (byte) 3) {
//                    continue;
//                }
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
        String path = directoryPath + "/" + fileName;
        String realName = name + "_" + format1 + "__" + format2 + ".txt";
        cheep.setPath(path);
        cheep.setRealName(realName);
        cheep.setCreateDate(new Date());
        cheep.setFrom(from);
        cheep.setTo(to);
        cheep.setCheepId(getUUID());
        cheepDao.insert(cheep);
        context.setResult(Result.success());
        context.put("msg", "设置公共棋谱");
    }
}
