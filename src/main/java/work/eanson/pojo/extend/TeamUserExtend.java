package work.eanson.pojo.extend;

import work.eanson.pojo.TeamUser;

import java.io.Serializable;

public class TeamUserExtend extends TeamUser implements Serializable {
    private static final long serialVersionUID = 12193821098301L;
    private UserInfoExtend userInfoExtend;

    public UserInfoExtend getUserInfoExtend() {
        return userInfoExtend;
    }

    public void setUserInfoExtend(UserInfoExtend userInfoExtend) {
        this.userInfoExtend = userInfoExtend;
    }
}
