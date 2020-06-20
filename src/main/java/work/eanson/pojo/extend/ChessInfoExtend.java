package work.eanson.pojo.extend;

import work.eanson.pojo.ChessInfo;
import work.eanson.pojo.UserLogin;

public class ChessInfoExtend extends ChessInfo {
    private UserLogin userLogin;

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }
}
