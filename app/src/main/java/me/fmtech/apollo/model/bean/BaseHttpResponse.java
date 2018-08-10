package me.fmtech.apollo.model.bean;

public class BaseHttpResponse {

    /**
     * errno : 0
     * msg : success
     */

    private int errno;
    private String msg;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public boolean isSuccess() {
        return 0 == errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
