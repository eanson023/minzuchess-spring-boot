package work.eanson.service.base;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import work.eanson.dao.CategoryDao;
import work.eanson.dao.ChessInfoDao;
import work.eanson.dao.UserLoginDao;
import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.back.UserSession;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 抽取一些通用 方法
 *
 * @author eanson
 */
@Service
public class BaseService {
    @Autowired
    protected CategoryDao categoryDao;
    @Autowired
    protected ChessInfoDao chessInfoDao;
    @Autowired
    protected UserLoginDao userLoginDao;

    private static Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    /**
     * 最大的号码+1映射为用户名
     * 棋盘别名的第一张 映射用户名....
     * 同时留间隔当前棋盘数量
     *
     * @return
     * @throws IllegalArgumentException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized long generateAliasCode() throws IllegalArgumentException {
        int min = 99;
        int max = 600;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Integer uInt = userLoginDao.selectMaxUserName();
        //生成最初别名
        long initLongStr = Long.parseLong(year + String.valueOf(1100));
        if (uInt == null) {
            return initLongStr;
        }
        Integer cInt = chessInfoDao.selectMaxAlias();
        int maxUsername = Math.max(uInt, cInt);
        //更换年份
        if (maxUsername < initLongStr) {
            return initLongStr;
        }
        for (int i = 1; i < 10; i++) {
            int suffix = i * 1000 + min;
            String query = year + String.valueOf(suffix);
            //最大最小区间
            int intervalMin = Integer.parseInt(query);
            int intervalMax = intervalMin + max - min;
            if (maxUsername + 1 > intervalMin && maxUsername + 1 < intervalMax) {
                return maxUsername + 1;
            }
            //不在则加1000进入下一次判断
            maxUsername = intervalMin + i * 1000;
        }
        throw new IllegalArgumentException("别名满了");
    }

    /**
     * 生成4位随机棋盘码
     *
     * @return
     */
    protected String getRandomCode() {
        String str = "QWERTYUIOPLKJHGFSAZXCVBNMqwertyuioplkjhgfdsazxcvb";
        Random random = new Random();
        StringBuilder proCode = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int i1 = random.nextInt(str.length());
            proCode.append(str.charAt(i1));
        }
        ChessInfo chess = chessInfoDao.selectByPrimaryKey(proCode.toString());
        if (chess != null) {
            proCode = new StringBuilder(this.getRandomCode());
        }
        return proCode.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param para
     * @return
     */
    protected String toLowerCaseAddUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        //定位
        int temp = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toLowerCase();
    }

    protected String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected boolean isNumber(String string) {
        return string != null && NUMBER_PATTERN.matcher(string).matches();
    }


    /**
     * 更新用户session
     *
     * @param userSession
     */
    public static void updateNewSession(UserSession userSession) {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection previousPrincipals = subject.getPrincipals();
        String next = previousPrincipals.getRealmNames().iterator().next();
        PrincipalCollection principalCollection = new SimplePrincipalCollection(userSession, next);
        subject.runAs(principalCollection);
    }
}
