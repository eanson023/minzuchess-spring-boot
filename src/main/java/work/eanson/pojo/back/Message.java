package work.eanson.pojo.back;

import java.io.Serializable;
import java.util.Objects;

/**
 * 消息 申请入队
 *
 * @author yans
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 183912476319470234L;
    private String who;
    private String teamName;
    private String uuid;
    private String teamId;
    private String createTime;

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(uuid, message.uuid) &&
                Objects.equals(teamId, message.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, teamId);
    }
}
