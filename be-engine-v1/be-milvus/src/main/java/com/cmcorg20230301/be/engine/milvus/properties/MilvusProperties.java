package com.cmcorg20230301.be.engine.milvus.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.MILVUS)
public class MilvusProperties {

    /**
     * 主机：例如：ip，域名，等
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 登录名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
