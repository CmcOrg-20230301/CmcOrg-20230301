package com.cmcorg20230301.engine.be.dict.handler;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.engine.be.cache.properties.MyCacheProperties;
import com.cmcorg20230301.engine.be.cache.util.CanalKafkaListenerHelper;
import com.cmcorg20230301.engine.be.model.model.enums.TableNameEnum;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import org.redisson.api.RBatch;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 字典：canal-kafka监听器的帮助类
 */
@Component
public class SysDictCanalKafkaHandler implements CanalKafkaListenerHelper.ICanalKafkaHandler {

    public static final RedisKeyEnum SYS_DICT_CACHE = RedisKeyEnum.SYS_DICT_CACHE;

    @Resource
    MyCacheProperties myCacheProperties;

    @Override
    public Set<String> getFullTableNameSet() {

        return CollUtil
            .newHashSet(myCacheProperties.getDatabaseName() + "." + TableNameEnum.SYS_DICT.name().toLowerCase());

    }

    @Override
    public void handler(CanalKafkaDTO dto, RBatch batch, CanalKafkaListenerHelper.CanalKafkaResult result) {

        String name = SYS_DICT_CACHE.name();

        batch.getBucket(name).deleteAsync();

        result.getRemoveLocalCacheKeySet().add(name);

    }

}
