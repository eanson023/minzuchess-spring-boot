package work.eanson.pojo.extend;

import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;

import java.util.List;

public class UserInfoExtend extends UserInfo {
    private UserLogin userLogin;
    //加入有多久了
    private Integer dateDiff;

    //加入那天的日期
    private String joinDay;

    //头像
    private String avatar;

    private List<ChessInfoExtend2> chessInfoExtend2s;

    private String isFinishReg;

    private String isAdminAdd;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsFinishReg() {
        return isFinishReg;
    }

    public void setIsFinishReg(String isFinishReg) {
        this.isFinishReg = isFinishReg;
    }

    public String getIsAdminAdd() {
        return isAdminAdd;
    }

    public void setIsAdminAdd(String isAdminAdd) {
        this.isAdminAdd = isAdminAdd;
    }

    public List<ChessInfoExtend2> getChessInfoExtend2s() {
        return chessInfoExtend2s;
    }

    public void setChessInfoExtend2s(List<ChessInfoExtend2> chessInfoExtend2s) {
        this.chessInfoExtend2s = chessInfoExtend2s;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getDateDiff() {
        return dateDiff;
    }

    public void setDateDiff(Integer dateDiff) {
        this.dateDiff = dateDiff;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public String getJoinDay() {
        return joinDay;
    }

    public void setJoinDay(String joinDay) {
        this.joinDay = joinDay;
    }

    @Override
    public String toString() {
        return "UserInfoExtend{" +
                "userLogin=" + userLogin +
                ", dateDiff=" + dateDiff +
                ", joinDay='" + joinDay + '\'' +
                ", avatar='" + avatar + '\'' +
                ", chessInfoExtend2s=" + chessInfoExtend2s +
                '}';
    }
}
