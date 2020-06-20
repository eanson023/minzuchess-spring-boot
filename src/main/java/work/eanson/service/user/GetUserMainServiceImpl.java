package work.eanson.service.user;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.eanson.dao.*;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.UserMain;
import work.eanson.pojo.back.UserSession;
import work.eanson.pojo.extend.UserInfoExtend;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.text.DecimalFormat;
import java.util.*;


/**
 * 查询用户页详情信息
 * <p>
 * 用户名 加入时间 加入的队伍 创建的队伍
 * <p>
 * <p>
 * * 思路：
 * * 1.查询所有用户加入到现在的时间 如果<31?最大时间:31
 * * 2.循环每一天 查询出某一天的棋谱时间
 * * 3.如果某一天size==0 记0
 * * 4.如果某一天size==1 记5分钟
 * * 5.如果某一天size>1:
 * * 遍历时间列表
 * * 筛选出比上一次时间间隔>30的 对象
 * * <p>
 * * 最后计算时间 第一----筛选出来的时间1+筛选出的时间2----时间3+时间n----最后
 *
 * @author eanson
 */
@Service("get_user_main")
public class GetUserMainServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private TrickDao trickDao;

    @Autowired
    private UserLogDao userLogDao;

    @Autowired
    private TeamUserDao teamUserDao;

    @Autowired
    private UserLoginDao userLoginDao;


    public static final Logger logger = LoggerFactory.getLogger(GetUserMainServiceImpl.class);

    /**
     * 我就要超过80行
     *
     * @param context 装载数据的工具
     * @throws Exception
     */
    @Input(required = "is_me", optional = {"uuid", "username"})
    @Override
    public void service(Context context) throws Exception {
        Object uuidObj = context.get("uuid");
        Object usernameObj = context.get("username");
        boolean isMe = (boolean) context.get("is_me");
        context.clear();
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        if (uuidObj != null) {
            String uuid = String.valueOf(uuidObj);
            telephone = userInfoDao.selectPrimaryKeyByUUID(uuid);
        } else if (usernameObj != null) {
            String username = String.valueOf(usernameObj);
            telephone = userLoginDao.selectTelephoneByUserName(username);
        }
        //没有参数 直接跑异常
        if (telephone == null) {
            throw new IllegalArgumentException(MsgCenter.ERROR_PARAMS);
        }

        //计算用户数据  查询用户相关信息

        int diff = userInfoDao.selectDateDiffToNow(telephone);
        diff = diff > 31 ? 31 : diff;
        UserMain userMain = new UserMain();
        int sumSeconds = 0;
        //记录下标
        //第一天到之后
        for (int i = 1; i < diff; i++) {
            //棋谱日志
            List<Date> dates1 = trickDao.selectOneDayTime(telephone, i);
            //用户日志
            List<Date> dates2 = userLogDao.selectOneDayTime(telephone, i);
            List<List<Date>> allData = Arrays.asList(dates1, dates2);
            for (List<Date> dates : allData) {
                if (dates.size() == 1) {
                    sumSeconds += 5 * 60;
                } else if (dates.size() > 1) {
                    //核心
                    int lastDifference = 0;
                    int last = dates.size() - 1;
                    for (int j = 1; j < dates.size(); j++) {
                        long preDate = dates.get(j - 1).getTime();
                        long curDate = dates.get(j).getTime();
                        //如果大于半小时
                        if (curDate - preDate > 30 * 60 * 1000) {
                            //去除临界值
                            if (j - 1 == 0) {
                                sumSeconds += 5;
                            } else {
                                //必须减一
                                long t = dates.get(j - 1).getTime() - dates.get(lastDifference).getTime();
                                sumSeconds += t / (1000);
                            }
                            lastDifference = j;
                        }
                    }
                    //如果lastDifference没有被改变过 说明 没有差距过大的时间 说明 从始至终的时间就是一天的总时间
                    if (lastDifference == 0) {
                        long t = dates.get(last).getTime() - dates.get(0).getTime();
                        sumSeconds += t / 1000;
                    }
                    //被改变了 所以加上最后一段时间差
                    else {
                        //去除临界值
                        if (last == lastDifference) {
                            sumSeconds += 5;
                        } else {
                            long t = dates.get(last).getTime() - dates.get(lastDifference).getTime();
                            sumSeconds += t / 1000;
                        }
                    }
                }
            }
        }
        //平均每天使用云棋盘时间
        String caclTime;
        if (diff == 0) {
            caclTime = caclTime(0);
        } else {
            caclTime = caclTime(sumSeconds / diff);
        }
        userMain.setAvgUseTime(caclTime);
        //计算近一个月我最喜欢在那个时间段使用云棋盘 清晨 白天 傍晚 深夜
        //0-6深夜 6-9清晨 9-18 白天 18-22傍晚 22-24深夜
        int[] interval = {0, 6, 9, 18, 22, 24};
        int[] num = new int[5];
        //查两张表 找出数据
        for (int i = 0; i < 5; i++) {
            num[i] = trickDao.selectCountByHour(telephone, interval[i], interval[i + 1]);
            num[i] += userLogDao.selectIntervalTimeCountByForeignKey(telephone, interval[i], interval[i + 1]);
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("深夜", num[0] + num[4]);
        map.put("清晨", num[1]);
        map.put("白天", num[2]);
        map.put("傍晚", num[3]);
        String mostUseTime = getMostUseTime(map);
        userMain.setMostComePeriod(mostUseTime);
        //计算 平均多少天来一次
        int count1 = userLogDao.selectUseCountInOneMonthByTelephone(telephone);
        int count2 = trickDao.selectUseCountInOneMonthByTelephone(telephone);
        double final_ = count1 > count2 ? count1 : count2;
        double res = diff / final_;
        //获取多久来一次数据 单位天
        String comeDay = new DecimalFormat("0.0").format(res);
        if (final_ == 0) {
            comeDay = "近期没有使用民族棋";
        }
        userMain.setAvgComeDay(comeDay);

        //查询我的队伍
        List<TeamUser> notLeader = teamUserDao.selectInfoByUserIdIsLeader(false, telephone);
        for (TeamUser teamUser : notLeader) {
            int i = teamUserDao.selectJoinedCountByTeamId(teamUser.getTeamId());
            teamUser.setCount(i);
        }
        List<TeamUser> isLeader = teamUserDao.selectInfoByUserIdIsLeader(true, telephone);
        for (TeamUser teamUser : isLeader) {
            int i = teamUserDao.selectJoinedCountByTeamId(teamUser.getTeamId());
            teamUser.setCount(i);
        }
        userMain.setJoinTeams(notLeader);
        userMain.setMyTeams(isLeader);

        //查询我或者其它人的信息
        UserInfoExtend userInfoExtend1;
        if (isMe) {
            userInfoExtend1 = userInfoDao.selectUserInfo(telephone);
        } else {
            userInfoExtend1 = userInfoDao.selectUserInfoAnno(telephone);
        }
        //加入到现在的时间
        userInfoExtend1.setDateDiff(diff);
        userMain.setUserInfoExtend(userInfoExtend1);
        context.setResult(Result.success(userMain));
        context.put("msg", "获取用户页");
    }

    private String getMostUseTime(Map<String, Integer> map) {
        int max = 0;
        String name = "";
        Set<String> strings = map.keySet();
        for (String string : strings) {
            Integer integer = map.get(string);
            int tmp = max;
            max = max > integer ? max : integer;
            //防止没有记录
            if (tmp != max) {
                name = string;
            }
        }
        if (max == 0) {
            name = "近期没有使用民族棋";
        }
        return name;
    }

    private String caclTime(int seconds) {
        StringBuilder sb = new StringBuilder();
        int min = seconds / 60;
        if (min > 0) {
            seconds = seconds % 60;
        }
        int hour = min / 60;
        if (hour > 0) {
            min = hour % 60;
        }
        if (hour > 24) {
            hour = 24;
            min = 0;
            seconds = 0;
        }
        return sb.append(hour).append("时").append(min).append("分").append(seconds).append("秒").toString();
    }
}
