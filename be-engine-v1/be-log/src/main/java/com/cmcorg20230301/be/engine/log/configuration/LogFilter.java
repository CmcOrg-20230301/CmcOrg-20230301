package com.cmcorg20230301.be.engine.log.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cmcorg20230301.be.engine.log.properties.LogProperties;
import com.cmcorg20230301.be.engine.log.util.LogToFlowChartUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;

public class LogFilter extends Filter<ILoggingEvent> {

    public static LogProperties logProperties;

    /**
     * 备注：打印日志会影响 tps：2600 -> 2000
     */
    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {

        if (logProperties != null && CollectionUtils.isNotEmpty(logProperties.getLogTopicSet())) {

            if (logProperties.getLogTopicSet().contains(iLoggingEvent.getLoggerName())) {

                // 处理：日志变成流程图
                LogToFlowChartUtil.handle(iLoggingEvent);

                return FilterReply.NEUTRAL; // 打印

            }

            if (logProperties.getLogTopicSet().contains(LogTopicConstant.NORMAL) && !iLoggingEvent.getLoggerName()
                .startsWith(LogTopicConstant.PRE_BE)) {

                if (logProperties.getNotLogTopicSet().contains(iLoggingEvent.getLoggerName())) {

                    return FilterReply.DENY; // 不打印

                }

                return FilterReply.NEUTRAL; // 打印

            }

            return FilterReply.DENY; // 不打印

        }

        if (iLoggingEvent.getLoggerName().startsWith(LogTopicConstant.PRE_BE)) {

            return FilterReply.DENY; // 不打印

        }

        return FilterReply.NEUTRAL; // 中立（一般都会打印）

    }

}
