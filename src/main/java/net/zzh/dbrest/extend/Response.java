package net.zzh.dbrest.extend;

public class Response {

    boolean success;

    String msg;

    Object body;

    public Response(boolean success, String msg, Object body) {
        this.success = success;
        this.msg = msg;
        this.body = body;
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

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
