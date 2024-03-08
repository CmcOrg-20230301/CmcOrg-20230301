package com.cmcorg20230301.be.engine.log.util;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.log.properties.LogProperties;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志变成流程图，工具类
 */
@Component
@Slf4j
public class LogToFlowChartUtil {

    private static LogProperties logProperties;

    @Resource
    public void setLogProperties(LogProperties logProperties) {
        LogToFlowChartUtil.logProperties = logProperties;
    }

    /**
     * 处理
     */
    public static void handle(ILoggingEvent iLoggingEvent) {

        // if (BooleanUtils.isFalse(logProperties.getLogToFlowChartFlag())) {
        // return;
        // }

    }

}
