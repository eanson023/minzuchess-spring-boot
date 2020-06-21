package work.eanson.service.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.dao.TeamDao;
import work.eanson.dao.TeamUserDao;
import work.eanson.dao.TrickDao;
import work.eanson.dao.UserLogDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.TeamUser;
import work.eanson.pojo.back.TeamMain;
import work.eanson.service.base.GlobalService;
import work.eanson.util.Context;

import java.io.File;
import java.util.*;

/**
 * 定时任务
 * 1.总时长
 * 找到队伍所有(已加入)成员
 * 从每个成员加入的天数开始算
 * 计算每个成员的时间总时间(用户日志表，棋谱日志表)
 * <p>
 * 汇总
 * 2.创建多少天了
 * 3.昨日活跃度排行
 * <p>
 * http://blog.csdn.net/sd4000784/article/details/7745947
 * <p>
 * 实现InitializingBean接口 应用启动时就调用该方法
 *
 * @author eanson
 */
@Component
public class Schedule implements InitializingBean {

    public static final Logger logger = LoggerFactory.getLogger(Schedule.class);

    @Value("${spring.profiles.active}")
    private String profiles;


    @Value("${dev-environment}")
    private String devEnvironment;


    @Autowired
    private TeamDao teamDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private TrickDao trickDao;
    @Autowired
    private UserLogDao userLogDao;
    @Autowired
    private TeamUserDao teamUserDao;

    @Autowired
    @Qualifier("reset_chess")
    private GlobalService resetChessService;

    @Value("${my.public-code}")
    private String publicCode;

    @Value("${my.tmp-cheep-path}")
    private String tempCheepPath;

    /**
     * 每天凌晨0点执行
     *
     * @throws JsonProcessingException
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void generateTeamMainData() throws JsonProcessingException {
        logger.info("开始计算当天队伍数据");
        try (Jedis redis = jedisPool.getResource()) {
            List<Team> teams = teamDao.selectTeamIdCreateTimeAll();
            List<TeamMain> teamMains = new ArrayList<>();
            for (Team team : teams) {
                String teamId = team.getTeamId();
                TeamMain teamMain = new TeamMain();
                teamMain.setTeam(team);
                //1.总时长
                long totalSeconds = 0;
                //昨日总时长
                long yesterdaySeconds = 0;
                //查询该队所有已加入用户
                List<TeamUser> teamUsers = teamUserDao.selectOneTeamUserInfoByTeamId(teamId);
                //遍历每个用户
                for (TeamUser teamUser : teamUsers) {
                    int diff = teamUser.getDateDiff();
                    String telephone = teamUser.getUserId();
                    for (int i = 1; i < diff; i++) {
                        //棋谱日志
                        List<Date> dates1 = trickDao.selectOneDayTime(telephone, i);
                        //用户日志
                        List<Date> dates2 = userLogDao.selectOneDayTime(telephone, i);
                        List<List<Date>> allData = Arrays.asList(dates1, dates2);
                        for (List<Date> dates : allData) {
                            if (dates.size() == 1) {
                                totalSeconds += 5 * 60;
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
                                            totalSeconds += 5;
                                        } else {
                                            //必须减一
                                            long t = dates.get(j - 1).getTime() - dates.get(lastDifference).getTime();
                                            totalSeconds += t / (1000);
                                        }
                                        lastDifference = j;
                                    }
                                }
                                //如果lastDifference没有被改变过 说明 没有差距过大的时间 说明 从始至终的时间就是一天的总时间
                                if (lastDifference == 0) {
                                    long t = dates.get(last).getTime() - dates.get(0).getTime();
                                    totalSeconds += t / 1000;
                                }
                                //被改变了 所以加上最后一段时间差
                                else {
                                    //去除临界值
                                    if (last == lastDifference) {
                                        totalSeconds += 5;
                                    } else {
                                        long t = dates.get(last).getTime() - dates.get(lastDifference).getTime();
                                        totalSeconds += t / 1000;
                                    }
                                }
                            }
                        }
                        //昨日
                        if (i == 1) {
                            yesterdaySeconds += totalSeconds;
                        }
                    }
                }
                String timeString = caclTime(totalSeconds);
                teamMain.setUseTotalTime(timeString);
                teamMain.setYesterdayTotalSecond(yesterdaySeconds);
                //2.创建多少天了
                int day = teamDao.selectCreateTimeDiffByPrimaryKey(teamId);
                teamMain.setCreateDayInterval(day);
                teamMains.add(teamMain);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            //3.活跃度排行 +存储
            Collections.sort(teamMains);
            for (int i = 0; i < teamMains.size(); i++) {
                TeamMain teamMain = teamMains.get(i);
                String teamId = teamMain.getTeam().getTeamId();
                //置空 暂不需要了
                teamMain.setTeam(null);
                String key = "team_main_" + teamId;
                //设置排名
                teamMain.setYesterdayActiveRanking(i + 1);
                String json = objectMapper.writeValueAsString(teamMain);
                redis.set(key, json);
            }
        }
    }

    /**
     * 每天凌晨4点清除棋盘码Test的信息
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void resetChessBoard() {
        Context context = new Context("");
        context.put("code", publicCode);
        try {
            resetChessService.service(context);
        } catch (Exception ignored) {
        }
    }

    /**
     * 每天凌晨四点清除tempCheepPath里的文件
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void clearFile() {
// 判断是开发环境 还是生产环境 不同环境 路径不同
        File file = profiles.equals(devEnvironment) ? new File(Schedule.class.getResource(tempCheepPath).getPath()) : new File(tempCheepPath);
        //递归删除所有文件
        delete(file);
        //重建该目录
        file.mkdirs();
    }

    public void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                delete(file1);
            }
            //删除目录
            file.delete();
        } else {
            //删除文件
            file.delete();
        }
    }

    private String caclTime(long seconds) {
        StringBuilder sb = new StringBuilder();
        long min = seconds / 60;
        if (min > 0) {
            seconds = seconds % 60;
        }
        long hour = min / 60;
        if (hour > 0) {
            min = hour % 60;
        }
        return sb.append(hour).append("时").append(min).append("分").append(seconds).append("秒").toString();
    }

    private void bootstrap() {
        String s = "\n" +
                "         _               _                   _                  _                 _                _          \n" +
                "        /\\ \\            / /\\                /\\ \\     _         / /\\              /\\ \\             /\\ \\     _  \n" +
                "       /  \\ \\          / /  \\              /  \\ \\   /\\_\\      / /  \\            /  \\ \\           /  \\ \\   /\\_\\\n" +
                "      / /\\ \\ \\        / / /\\ \\            / /\\ \\ \\_/ / /     / / /\\ \\__        / /\\ \\ \\         / /\\ \\ \\_/ / /\n" +
                "     / / /\\ \\_\\      / / /\\ \\ \\          / / /\\ \\___/ /     / / /\\ \\___\\      / / /\\ \\ \\       / / /\\ \\___/ / \n" +
                "    / /_/_ \\/_/     / / /  \\ \\ \\        / / /  \\/____/      \\ \\ \\ \\/___/     / / /  \\ \\_\\     / / /  \\/____/  \n" +
                "   / /____/\\       / / /___/ /\\ \\      / / /    / / /        \\ \\ \\          / / /   / / /    / / /    / / /   \n" +
                "  / /\\____\\/      / / /_____/ /\\ \\    / / /    / / /     _    \\ \\ \\        / / /   / / /    / / /    / / /    \n" +
                " / / /______     / /_________/\\ \\ \\  / / /    / / /     /_/\\__/ / /       / / /___/ / /    / / /    / / /     \n" +
                "/ / /_______\\   / / /_       __\\ \\_\\/ / /    / / /      \\ \\/___/ /       / / /____\\/ /    / / /    / / /      \n" +
                "\\/__________/   \\_\\___\\     /____/_/\\/_/     \\/_/        \\_____\\/        \\/_________/     \\/_/     \\/_/       \n" +
                "                                                                                                              \n";
        logger.info(s);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrap();
        generateTeamMainData();
    }
}
