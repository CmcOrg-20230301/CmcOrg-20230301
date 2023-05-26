package com.cmcorg20230301.engine.be.model.properties;

import lombok.Data;

/**
 * socket配置类，基础类
 */
@Data
public class SysSocketBaseProperties {

    /**
     * 协议：例如：ws://，wss://，http://，https://，等
     */
    private String scheme;

    /**
     * 主机：例如：ip，域名，等
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

}
