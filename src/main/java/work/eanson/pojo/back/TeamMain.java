package work.eanson.pojo.back;

import work.eanson.pojo.Team;

import java.io.Serializable;

public class TeamMain implements Serializable, Comparable<TeamMain> {
    //队伍
    private Team team;
    //使用总时间
    private String useTotalTime;
    //昨日活跃度排行
    private Integer yesterdayActiveRanking;
    //队伍创建了多少天了
    private Integer createDayInterval;

    //昨日总时间 用于排序用
    private Long yesterdayTotalSecond;

    //总人数
    private int total;

    private static final long serialVersionUID = 183912476329470224L;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getUseTotalTime() {
        return useTotalTime;
    }

    public void setUseTotalTime(String useTotalTime) {
        this.useTotalTime = useTotalTime;
    }

    public Integer getYesterdayActiveRanking() {
        return yesterdayActiveRanking;
    }

    public void setYesterdayActiveRanking(Integer yesterdayActiveRanking) {
        this.yesterdayActiveRanking = yesterdayActiveRanking;
    }

    public Integer getCreateDayInterval() {
        return createDayInterval;
    }

    public void setCreateDayInterval(Integer createDayInterval) {
        this.createDayInterval = createDayInterval;
    }

    private Long getYesterdayTotalSecond() {
        return yesterdayTotalSecond;
    }

    public void setYesterdayTotalSecond(Long yesterdayTotalSecond) {
        this.yesterdayTotalSecond = yesterdayTotalSecond;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public int compareTo(TeamMain o) {
        //降序
        return (int) (o.getYesterdayTotalSecond() - this.getYesterdayTotalSecond());
    }
}
