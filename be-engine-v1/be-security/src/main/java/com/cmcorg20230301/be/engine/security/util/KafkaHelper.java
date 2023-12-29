package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.properties.CommonProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Kafka 帮助类
 */
@Component
public class KafkaHelper {

    private static CommonProperties commonProperties;

    @Resource
    public void setCommonProperties(CommonProperties commonProperties) {
        KafkaHelper.commonProperties = commonProperties;
    }

    @Resource
    BaseConfiguration baseConfiguration;

    /**
     * 根据配置和开发环境，判断是否处理该主题
     *
     * @return true 不处理该主题 false 要处理该主题
     */
    public static boolean notHandleKafkaTopCheck(List<String> topicList) {

        if (BaseConfiguration.prodFlag()) {

            if (CollUtil.containsAny(commonProperties.getProdNotHandleKafkaTopSet(), topicList)) {

                return true;

            }

        } else {

            if (CollUtil.containsAny(commonProperties.getDevNotHandleKafkaTopSet(), topicList)) {

                return true;

            }

        }

        return false;

    }

}
