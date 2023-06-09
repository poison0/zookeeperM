package com.azure.nzook.constant;

import org.apache.zookeeper.CreateMode;

/**
 * @author niu
 * @since 1.0
 */
public class Constant {

    /**
     * 工具窗口id
     */
    public static final String TOOL_WINDOW_ID = "nzook";

    public static final String ROOT = "/";

    public static final String NODE_SPLIT = "/";

    /**
     * 默认时间格式
     */
    public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    /**
     * 默认端口
     */
    public static final String DEFAULT_PORT = "2181";
    /**
     * 默认ip
     */
    public static final String DEFAULT_HOST = "127.0.0.1";
    /**
     * 默认超时时间
     */
    public static final Integer DEFAULT_TIMEOUT = 10000;
    /**
     * 存储ip
     */
    @Deprecated
    public static final String PROPERTIES_COMPONENT_IP = "zookeeper.m.ip";
    /**
     * 存储端口
     */
    @Deprecated
    public static final String PROPERTIES_COMPONENT_PORT = "zookeeper.m.port";
    /**
     * 用于存储当前是否登录
     */
    public static final String PROPERTIES_COMPONENT_LOGIN = "zookeeper.m.login";
    /**
     * 用于存储当前信息
     */
    public static final String PROPERTIES_COMPONENT_LOGIN_DATA = "nzook.m.login.data";

    /**
     * 加载中标题
     */
    public static final String LOGIN_LOADING_TITLE = "nzook loading...";

    public static final String TIMER_KEY = "nzook loading...";

    public static final String[] CREATE_MODE_OPTIONS = new String[]{
            CreateMode.PERSISTENT.name(),
            CreateMode.PERSISTENT_SEQUENTIAL.name(),
            CreateMode.EPHEMERAL.name(),
            CreateMode.EPHEMERAL_SEQUENTIAL.name(),
            CreateMode.CONTAINER.name(),
            CreateMode.PERSISTENT_WITH_TTL.name(),
            CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL.name()
    };
    public static final String[] SCHEME_OPTIONS = new String[]{
            "world",
            "auth",
            "digest",
            "ip"
    };
}
