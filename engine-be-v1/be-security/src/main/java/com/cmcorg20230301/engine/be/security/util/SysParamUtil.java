package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.engine.be.cache.properties.MyCacheProperties;
import com.cmcorg20230301.engine.be.cache.util.CacheHelper;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.cache.util.CanalKafkaListenerHelper;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.model.model.enums.TableNameEnum;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysParamMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysParamDO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统参数 工具类
 */
@Component
public class SysParamUtil {

    private static SysParamMapper sysParamMapper;

    public SysParamUtil(SysParamMapper sysParamMapper) {
        SysParamUtil.sysParamMapper = sysParamMapper;
    }

    @Resource
    MyCacheProperties myCacheProperties;

    @PostConstruct
    public void postConstruct() {

        String databaseName = myCacheProperties.getDatabaseName();

        CanalKafkaListenerHelper.ICanalKafkaHandler iCanalKafkaHandler =
            new CanalKafkaListenerHelper.ICanalKafkaHandler() {

                @Override
                public Set<String> getFullTableNameSet() {
                    return CollUtil.newHashSet(databaseName + ":" + TableNameEnum.SYS_PARAM.name().toLowerCase());
                }

                @Override
                public void handler(CanalKafkaDTO dto, @NotNull RBatch batch,
                    CanalKafkaListenerHelper.CanalKafkaResult result) {

                    CacheRedisKafkaLocalUtil.remove(RedisKeyEnum.SYS_PARAM_CACHE, null);

                }

            };

        CanalKafkaListenerHelper.put(iCanalKafkaHandler);

    }

    /**
     * 通过主键 id，获取 value，没有 value则返回 null
     */
    @Nullable
    public static String getValueById(Long id) {

        Map<Long, String> map = MyCacheUtil.get(RedisKeyEnum.SYS_PARAM_CACHE, CacheHelper.getDefaultLongMap(), () -> {

            List<SysParamDO> sysParamDOList =
                ChainWrappers.lambdaQueryChain(sysParamMapper).select(BaseEntity::getId, SysParamDO::getValue)
                    .eq(BaseEntity::getEnableFlag, true).list();

            // 注意：Collectors.toMap()方法，key不能重复，不然会报错
            // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
            return sysParamDOList.stream().collect(Collectors.toMap(BaseEntity::getId, SysParamDO::getValue));

        });

        return map.get(id);

    }

}

