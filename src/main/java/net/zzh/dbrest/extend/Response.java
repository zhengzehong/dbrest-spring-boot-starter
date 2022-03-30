package net.zzh.dbrest.extend;

/**
 * @Description: 默认的结果包装类，可通过自定义ResultHandler修改
 * @author Zeo Zheng
 * @date 2022/1/23 16:58
 * @version 1.0
 */
public class Response {

    boolean success;

    String msg;

    Object data;

    public Response(boolean success, String msg, Object body) {
        this.success = success;
        this.msg = msg;
        this.data = body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
