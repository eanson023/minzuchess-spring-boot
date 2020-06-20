package work.eanson.pojo.back;

import work.eanson.pojo.TeamUser;
import work.eanson.pojo.extend.UserInfoExtend;

import java.util.List;

public class UserMain {
    private UserInfoExtend userInfoExtend;
    //平均多少天来一次
    private String AvgComeDay;
    //平均每天使用多久云棋盘
    private String AvgUseTime;
    //近31天最长来的时候
    private String MostComePeriod;

    private List<TeamUser> myTeams;
    private List<TeamUser> joinTeams;


    public String getAvgComeDay() {
        return AvgComeDay;
    }

    public void setAvgComeDay(String avgComeDay) {
        AvgComeDay = avgComeDay;
    }

    public String getAvgUseTime() {
        return AvgUseTime;
    }

    public void setAvgUseTime(String avgUseTime) {
        AvgUseTime = avgUseTime;
    }

    public List<TeamUser> getMyTeams() {
        return myTeams;
    }

    public void setMyTeams(List<TeamUser> myTeams) {
        this.myTeams = myTeams;
    }

    public List<TeamUser> getJoinTeams() {
        return joinTeams;
    }

    public void setJoinTeams(List<TeamUser> joinTeams) {
        this.joinTeams = joinTeams;
    }

    public String getMostComePeriod() {
        return MostComePeriod;
    }

    public void setMostComePeriod(String mostComePeriod) {
        MostComePeriod = mostComePeriod;
    }

    public UserInfoExtend getUserInfoExtend() {
        return userInfoExtend;
    }

    public void setUserInfoExtend(UserInfoExtend userInfoExtend) {
        this.userInfoExtend = userInfoExtend;
    }


    @Override
    public String toString() {
        return "UserMain{" +
                "userInfoExtend=" + userInfoExtend +
                ", AvgComeDay='" + AvgComeDay + '\'' +
                ", AvgUseTime='" + AvgUseTime + '\'' +
                ", MostComePeriod='" + MostComePeriod + '\'' +
                ", myTeams=" + myTeams +
                ", joinTeams=" + joinTeams +
                '}';
    }
}
