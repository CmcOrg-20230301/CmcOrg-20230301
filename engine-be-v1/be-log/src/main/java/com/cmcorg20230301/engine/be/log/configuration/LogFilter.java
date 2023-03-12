package com.cmcorg20230301.engine.be.log.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cmcorg20230301.engine.be.log.properties.LogProperties;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;

public class LogFilter extends Filter<ILoggingEvent> {

    public static LogProperties logProperties;

    /**
     * 备注：打印日志会影响 tps：2600 -> 2000
     */
    @Override
    public FilterReply decide(ILoggingEvent loggingEvent) {

        if (logProperties != null && CollectionUtils.isNotEmpty(logProperties.getLogTopicSet())) {

            if (logProperties.getLogTopicSet().contains(loggingEvent.getLoggerName())) {
                return FilterReply.NEUTRAL; // 打印
            }

            if (logProperties.getLogTopicSet().contains(LogTopicConstant.NORMAL) && !loggingEvent.getLoggerName()
                .startsWith(LogTopicConstant.PRE_BE)) {
                return FilterReply.NEUTRAL; // 打印
            }

            return FilterReply.DENY; // 不打印

        }

        if (loggingEvent.getLoggerName().startsWith(LogTopicConstant.PRE_BE)) {
            return FilterReply.DENY; // 不打印
        }

        return FilterReply.NEUTRAL; // 中立

    }

}
