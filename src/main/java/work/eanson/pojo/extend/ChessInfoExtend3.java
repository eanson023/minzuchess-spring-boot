package work.eanson.pojo.extend;

import work.eanson.pojo.ChessInfo;

/**
 * @author eanson
 * @create 2020-03-29 14:11
 */
public class ChessInfoExtend3 extends ChessInfo {
    private String isPublicStr;
    private String value;
    private String realName;

    public String getIsPublicStr() {
        return isPublicStr;
    }

    public void setIsPublicStr(String isPublicStr) {
        this.isPublicStr = isPublicStr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
