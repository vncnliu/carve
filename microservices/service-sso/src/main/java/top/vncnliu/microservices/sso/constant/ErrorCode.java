package top.vncnliu.microservices.sso.constant;

public enum ErrorCode {

    UserNotExit(20001,"用户不存在"),
    ErrorPassword(20002,"密码不正确"),
    ErrorClientUser(20003,"用户APP不匹配");

    private Integer code;
    private String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
