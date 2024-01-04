package com.cmcorg20230301.be.engine.security.configuration.cache;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.cache.model.dto.CanalKafkaDTO;
import com.cmcorg20230301.be.engine.cache.properties.MyCacheProperties;
import com.cmcorg20230301.be.engine.cache.util.CanalKafkaListenerHelper;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.enums.TableNameEnum;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBatch;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Set;

/**
 * security的缓存配置类
 */
@Component
@Slf4j(topic = LogTopicConstant.CACHE)
public class SecurityCacheConfiguration {

    @Resource
    MyCacheProperties myCacheProperties;

    // 需要缓存的表名 set
    private static final Set<SecurityCache> TABLE_NAME_ENUM_SET = CollUtil.newHashSet();

    static {

        // 用户信息
        TABLE_NAME_ENUM_SET.add(
                new SecurityCache(TableNameEnum.SYS_USER_INFO, CollUtil.newHashSet(BaseRedisKeyEnum.SYS_USER_INFO_CACHE)));

        // 系统参数
        TABLE_NAME_ENUM_SET
                .add(new SecurityCache(TableNameEnum.SYS_PARAM, CollUtil.newHashSet(BaseRedisKeyEnum.SYS_PARAM_CACHE)));

        // 菜单
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_MENU, CollUtil
                .newHashSet(BaseRedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE,
                        BaseRedisKeyEnum.ROLE_ID_REF_FULL_MENU_SET_CACHE, BaseRedisKeyEnum.ROLE_ID_REF_SECURITY_MENU_SET_CACHE,
                        BaseRedisKeyEnum.SYS_MENU_CACHE)));

        // 角色关联用户
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_ROLE_REF_USER,
                CollUtil.newHashSet(BaseRedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE)));

        // 角色关联菜单
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_ROLE_REF_MENU, CollUtil
                .newHashSet(BaseRedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE,
                        BaseRedisKeyEnum.ROLE_ID_REF_FULL_MENU_SET_CACHE,
                        BaseRedisKeyEnum.ROLE_ID_REF_SECURITY_MENU_SET_CACHE)));

        // 角色
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_ROLE, CollUtil
                .newHashSet(BaseRedisKeyEnum.TENANT_DEFAULT_ROLE_ID_CACHE, BaseRedisKeyEnum.ROLE_ID_SET_CACHE,
                        BaseRedisKeyEnum.ROLE_ID_REF_FULL_MENU_SET_CACHE,
                        BaseRedisKeyEnum.ROLE_ID_REF_SECURITY_MENU_SET_CACHE)));

        // 字典
        TABLE_NAME_ENUM_SET
                .add(new SecurityCache(TableNameEnum.SYS_DICT, CollUtil.newHashSet(BaseRedisKeyEnum.SYS_DICT_CACHE)));

        // 租户
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_TENANT,
                CollUtil.newHashSet(BaseRedisKeyEnum.SYS_TENANT_CACHE, BaseRedisKeyEnum.SYS_TENANT_DEEP_ID_SET_CACHE)));

        // 租户关联用户
        TABLE_NAME_ENUM_SET.add(new SecurityCache(TableNameEnum.SYS_TENANT_REF_USER,
                CollUtil.newHashSet(BaseRedisKeyEnum.USER_ID_REF_TENANT_ID_SET_CACHE)));

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SecurityCache {

        // 表名
        private TableNameEnum tableNameEnum;

        // 需要移除的：redisKeyEnumSet
        private Set<Enum<? extends IRedisKey>> removeRedisKeyEnumSet;

    }

    @PostConstruct
    public void postConstruct() {

        // 添加：canal-kafka的处理器
        String databaseName = myCacheProperties.getDatabaseName();

        for (SecurityCache item : TABLE_NAME_ENUM_SET) {

            CanalKafkaListenerHelper.ICanalKafkaHandler iCanalKafkaHandler =
                    new CanalKafkaListenerHelper.ICanalKafkaHandler() {

                        @Override
                        public Set<String> getFullTableNameSet() {
                            return CollUtil.newHashSet(databaseName + "." + item.getTableNameEnum().name().toLowerCase());
                        }

                        @Override
                        public void handler(CanalKafkaDTO dto, @NotNull RBatch batch,
                                            CanalKafkaListenerHelper.CanalKafkaResult result) {

                            for (Enum<? extends IRedisKey> subItem : item.getRemoveRedisKeyEnumSet()) {

                                String name = subItem.name();

                                batch.getBucket(name).deleteAsync();

                                result.getRemoveLocalCacheKeySet().add(name);

                            }

                        }

                    };

            CanalKafkaListenerHelper.put(iCanalKafkaHandler);

            log.info("CANAL_KAFKA_HANDLER_MAP，长度：{}，name：{}", CanalKafkaListenerHelper.CANAL_KAFKA_HANDLER_MAP.size(),
                    item.getTableNameEnum().name());

        }

    }

}
