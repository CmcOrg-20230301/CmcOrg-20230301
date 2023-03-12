package com.cmcorg20230301.engine.be.cache.model.enums;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.cache.model.interfaces.ILocalCacheAndCanalKafkaHelper;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.redisson.model.interfaces.IRedisKey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Schema(description = "本地缓存和 canal的消息枚举")
public enum LocalCacheAndCanalKafkaHelperEnum implements ILocalCacheAndCanalKafkaHelper {

    SYS_MENU("sys_menu", CollUtil
        .newHashSet(RedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, RedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE,
            RedisKeyEnum.ROLE_ID_REF_MENU_SET_ONE_CACHE, RedisKeyEnum.ROLE_ID_REF_MENU_SET_TWO_CACHE)), // 菜单表

    SYS_USER("sys_user", CollUtil
        .newHashSet(RedisKeyEnum.USER_ID_JWT_SECRET_SUF_CACHE, RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE)), // 用户表

    SYS_ROLE("sys_role", CollUtil
        .newHashSet(RedisKeyEnum.DEFAULT_ROLE_ID_CACHE, RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE,
            RedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE, RedisKeyEnum.ROLE_ID_REF_MENU_SET_ONE_CACHE,
            RedisKeyEnum.ROLE_ID_REF_MENU_SET_TWO_CACHE, RedisKeyEnum.ROLE_ID_SET_CACHE)), // 角色表

    SYS_PARAM("sys_param", CollUtil.newHashSet(RedisKeyEnum.SYS_PARAM_CACHE)), // 系统参数表

    ;

    private final String name;
    private final Set<Enum<? extends IRedisKey>> deleteRedisKeyEnumSet; // 会默认添加删除的 handler

}
