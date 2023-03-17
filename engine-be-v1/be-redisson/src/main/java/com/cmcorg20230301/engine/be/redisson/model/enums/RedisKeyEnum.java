package com.cmcorg20230301.engine.be.redisson.model.enums;

import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;

/**
 * redis中 key的枚举类
 * 备注：如果是 redisson的锁 key，一定要备注：锁什么，例如：锁【用户主键 id】
 * 备注：【PRE_】开头，表示 key后面还要跟字符串
 * 备注：【_CACHE】结尾，表示 key后面不用跟字符串
 */
public enum RedisKeyEnum implements IRedisKey {

    // 【PRE_】开头 ↓
    PRE_IP_BLACK, // ip黑名单前端，后面跟 ip
    PRE_IP_TOTAL_CHECK, // ip 请求总数，key前缀，后面跟 ip

    PRE_JWT_HASH, // jwtHash 前缀

    PRE_WX_OPEN_ID, // 微信 openId：锁【微信 openId】
    PRE_PHONE, // 手机号码：锁【手机号码】
    PRE_EMAIL, // 邮箱：锁【邮箱】
    PRE_SIGN_IN_NAME, // 登录名：锁【登录名】

    PRE_TOO_MANY_PASSWORD_ERROR, // 密码错误次数太多：锁【用户主键 id】
    PRE_PASSWORD_ERROR_COUNT, // 密码错误总数：锁【用户主键 id】

    // 【_CACHE】结尾 ↓
    XXL_JOB_COOKIE_CACHE, // xxl-job cookie缓存

    WX_ACCESS_TOKEN_CACHE, // 微信小程序全局唯一后台接口调用凭据

    USER_ID_AND_JWT_SECRET_SUF_CACHE, // 用户 id和 jwt私钥后缀

    USER_ID_REF_ROLE_ID_SET_CACHE, // 用户 id关联的 roleIdSet
    DEFAULT_ROLE_ID_CACHE, // 默认角色 id
    ROLE_ID_REF_MENU_ID_SET_CACHE, // 角色 id关联的 menuIdSet
    ALL_MENU_ID_AND_AUTHS_LIST_CACHE, // menu集合，包含所有菜单：id和 auths
    ROLE_ID_REF_MENU_SET_ONE_CACHE, // 角色 id关联的 menuSet，1 完整的菜单信息
    ROLE_ID_REF_MENU_SET_TWO_CACHE, // 角色 id关联的 menuSet，2 给 security获取权限时使用
    ROLE_ID_SET_CACHE, // 角色 idSet

    SYS_PARAM_CACHE, // 系统参数缓存

    // 其他 ↓
    ATOMIC_LONG_ID_GENERATOR, // 获取主键 id，自增值

    ;

}
