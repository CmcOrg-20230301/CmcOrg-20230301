package com.cmcorg20230301.engine.be.log.configuration;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.cmcorg20230301.engine.be.log.properties.LogProperties;
import com.cmcorg20230301.engine.be.log.util.LogToFlowChartUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import org.apache.commons.lang3.BooleanUtils;

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

            if (logProperties.getLogTopicSet().contains(LogTopicConstant.NORMAL) && BooleanUtils
                .isFalse(iLoggingEvent.getLoggerName().startsWith(LogTopicConstant.PRE_BE))) {
                return FilterReply.NEUTRAL; // 打印
            }

            return FilterReply.DENY; // 不打印

        }

        if (iLoggingEvent.getLoggerName().startsWith(LogTopicConstant.PRE_BE)) {
            return FilterReply.DENY; // 不打印
        }

        return FilterReply.NEUTRAL; // 中立

    }

}
