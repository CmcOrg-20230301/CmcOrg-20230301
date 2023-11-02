package com.cmcorg20230301.be.engine.log.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.LOG)
@RefreshScope
@Component
public class LogProperties {

    /**
     * 为空时，则是正常的日志状态，并且不会打印 be开头的日志，不为空时，则只打印集合里面日志
     */
    private Set<String> logTopicSet;

    /**
     * 不打印的正常日志
     */
    private Set<String> notLogTopicSet;

    /**
     * 是否开启：日志变成流程图，备注：只针对有 topic的日志
     */
    private Boolean logToFlowChartFlag = true;

}
