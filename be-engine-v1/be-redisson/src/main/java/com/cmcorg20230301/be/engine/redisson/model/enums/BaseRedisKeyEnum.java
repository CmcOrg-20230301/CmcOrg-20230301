package com.cmcorg20230301.be.engine.redisson.model.enums;

import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;

/**
 * redis中 key的枚举类
 * 备注：如果是 redisson的锁 key，一定要备注：锁什么，例如：锁【用户主键 id】
 * 备注：【PRE_】开头，表示 key后面还要跟字符串
 * 备注：【_CACHE】结尾，表示 key后面不用跟字符串
 */
public enum BaseRedisKeyEnum implements IRedisKey {

    // 【PRE_】开头 ↓
    PRE_USER_WALLET_WITHDRAW_LOG, // 用户钱包-提现记录锁，锁：【提现记录主键 id】

    PRE_USER_WALLET, // 用户钱包锁，锁：【用户钱包主键 id】

    PRE_SIGN_CONFIGURATION, // 用户登录注册相关配置锁，锁：【租户主键 id】

    PRE_PAY, // 支付锁，锁：【支付主键 id】

    PRE_TCP_PROTOBUF_CODE, // tcp-protobuf连接锁，锁：【随机码】

    PRE_WEB_SOCKET_CODE, // webSocket连接锁，锁：【随机码】

    PRE_IP_BLACK, // ip黑名单前端，后面跟 ip

    PRE_JWT_HASH, // jwtHash 前缀

    PRE_WX_APP_ID, // 微信 appId：锁【微信 appId】
    PRE_WX_OPEN_ID, // 微信 openId：锁【微信 openId】
    PRE_PHONE, // 手机号码：锁【手机号码】
    PRE_EMAIL, // 邮箱：锁【邮箱】
    PRE_SIGN_IN_NAME, // 登录名：锁【登录名】

    PRE_TOO_MANY_PASSWORD_ERROR, // 密码错误次数太多：锁【用户主键 id】
    PRE_PASSWORD_ERROR_COUNT, // 密码错误总数：锁【用户主键 id】

    // 【_CACHE】结尾 ↓
    SYS_USER_INFO_CACHE, // 用户信息缓存

    SYS_USER_DISABLE_CACHE, // 用户是否被冻结，如果存在，则表示，用户被冻结了

    USER_ID_REF_TENANT_ID_SET_CACHE, // 用户 id关联的 tenantIdSet

    SYS_TENANT_DEEP_ID_SET_CACHE, // key：租户 id，value：关联的所有的 子租户（包含本租户）

    SYS_TENANT_CACHE, // 租户缓存，key：租户 id，value：租户信息

    GOOGLE_ACCESS_TOKEN_CACHE, // google接口调用凭据

    XXL_JOB_COOKIE_CACHE, // xxl-job cookie缓存

    WX_ACCESS_TOKEN_CACHE, // 微信小程序全局唯一后台接口调用凭据

    USER_ID_AND_JWT_SECRET_SUF_CACHE, // 用户 id和 jwt私钥后缀

    USER_ID_REF_ROLE_ID_SET_CACHE, // 用户 id关联的 roleIdSet
    TENANT_DEFAULT_ROLE_ID_CACHE, // 每个租户的，默认角色 id，map
    ROLE_ID_REF_MENU_ID_SET_CACHE, // 角色 id关联的 menuIdSet
    ROLE_ID_SET_CACHE, // 角色 idSet

    SYS_MENU_CACHE, // 菜单缓存，key：菜单 id，value：菜单信息
    ALL_MENU_ID_AND_AUTHS_LIST_CACHE, // 菜单集合，包含所有菜单：id和 auths
    ROLE_ID_REF_FULL_MENU_SET_CACHE, // 角色 id关联的 menuSet，1 完整的菜单信息
    ROLE_ID_REF_SECURITY_MENU_SET_CACHE, // 角色 id关联的 menuSet，2 给 security获取权限时使用

    SYS_PARAM_CACHE, // 系统参数缓存

    SYS_DICT_CACHE, // 字典缓存

    // 其他 ↓
    ATOMIC_LONG_ID_GENERATOR, // 获取主键 id，自增值

    ;

}
