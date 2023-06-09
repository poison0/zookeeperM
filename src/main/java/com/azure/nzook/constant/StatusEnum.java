package com.azure.nzook.constant;

/**
 * @author niu
 * @since 1.0
 */
public enum StatusEnum {
    NOT_CONNECT("未连接"),
    CONNECTED("已连接"),

    CONNECTING("连接中");

    private String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
