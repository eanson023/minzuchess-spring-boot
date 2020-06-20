package work.eanson.pojo.security;

public class SecurityInfo {
    private String securityKey;
    private String sessionKey;
    private String info;

    public SecurityInfo(String securityKey, String sessionKey, String info) {
        this.securityKey = securityKey;
        this.sessionKey = sessionKey;
        this.info = info;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
