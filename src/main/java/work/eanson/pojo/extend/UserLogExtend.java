package work.eanson.pojo.extend;

import work.eanson.pojo.UserLog;

import java.io.Serializable;

/**
 * @author eanson
 * @create 2020-03-28 15:33
 */
public class UserLogExtend extends UserLog implements Serializable {
    private static final long serialVersionUID = 2234235345612316546L;
    private String createTime;
    private String realName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
