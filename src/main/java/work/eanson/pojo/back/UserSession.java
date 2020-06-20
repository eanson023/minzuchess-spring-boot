package work.eanson.pojo.back;

import java.io.Serializable;
import java.util.List;

/**
 * @author yans
 */
public class UserSession implements Serializable {
    private String telephone;
    private String username;
    private String realName;
    private String token;
    private int msgCount;
    private String avatar;
    private String ip;
    private List<Message> messages;
    private boolean rememberMe;

    private static final long serialVersionUID = 183912476329470234L;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public int getMsgCount() {
        return msgCount;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
