package work.eanson.pojo.extend;

import work.eanson.pojo.Trick;

import java.io.Serializable;

public class TrickExtend extends Trick implements Serializable {
    private static final long serialVersionUID = 1L;
    private String createTime;
    private String realName;
    private String value;
    private String statusStr;
    private String typeStr;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TrickExtend{" +
                "createTime='" + createTime + '\'' +
                '}' + super.toString();
    }
}
