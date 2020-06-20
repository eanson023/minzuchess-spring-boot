package work.eanson.pojo.extend;

import work.eanson.pojo.Category;
import work.eanson.pojo.Cheep;
import work.eanson.pojo.UserInfo;

import java.io.Serializable;

/**
 * @author eanson
 * @create 2020-03-26 14:30
 */
public class CheepExtend extends Cheep implements Serializable {
    private String createTime;
    private Category category;
    private UserInfo userInfo;

    public static final long serialVersionUID = 127892618927998L;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
