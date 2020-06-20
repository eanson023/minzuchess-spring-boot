package work.eanson.util;

import java.util.HashMap;

/**
 * 类全局变量
 * @author eanson
 */
public class Context extends HashMap<String, Object> {
    private String serviceName;
    private Result result;

    public Context(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setNewServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public void clear() {
        this.result = null;
        super.clear();
    }

    public boolean isSuccess() {
        return this.result.isSuccess();
    }
}
