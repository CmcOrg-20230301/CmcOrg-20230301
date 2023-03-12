package com.cmcorg20230301.engine.be.cache.configuration;

import com.cmcorg20230301.engine.be.cache.model.enums.LocalCacheAndCanalKafkaHelperEnum;
import com.cmcorg20230301.engine.be.cache.util.CanalKafkaHandlerUtil;
import org.springframework.stereotype.Component;

@Component
public class CanalKafkaRedisKeyConfiguration {

    public CanalKafkaRedisKeyConfiguration(CanalKafkaHandlerUtil canalKafkaHandlerUtil) {

        // 添加：枚举类里面的值
        canalKafkaHandlerUtil.putCanalKafkaHandlerMap(LocalCacheAndCanalKafkaHelperEnum.values());

    }

}
