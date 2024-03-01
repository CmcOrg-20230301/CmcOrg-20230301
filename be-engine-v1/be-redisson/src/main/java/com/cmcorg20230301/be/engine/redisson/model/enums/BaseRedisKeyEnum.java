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

    // im 相关 ↓

    PRE_SYS_IM_SESSION_APPLY_PRIVATE_CHAT, // 即时通讯会话申请锁，锁：【用户主键 id + 目标用户主键 id】

    PRE_SYS_IM_SESSION_REF_USER, // 即时通讯会话关联用户锁，目的：往会话里面添加用户时，防止重复添加，锁：【sessionId + 用户主键 id】

    PRE_SYS_IM_SESSION_CUSTOMER, // 即时通讯客服会话锁，目的：创建会话时，防止重复添加，锁：【用户主键 id】

    // im 相关 ↑

    PRE_SYS_WX_WORK_SYNC_MSG, // 企业微信，获取消息锁，锁：【租户主键 id】

    // 统一登录相关 ↓

    PRE_SYS_SINGLE_SIGN_IN_SET_WX, // 统一登录：设置微信扫码登录时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_SINGLE_SIGN_IN_SET_PHONE, // 统一登录：设置手机验证码登录

    PRE_SYS_SINGLE_SIGN_IN_SET_EMAIL, // 统一登录：设置邮箱验证码登录

    PRE_SYS_SINGLE_SIGN_IN, // 统一登录锁：锁：【userId】

    // 统一登录相关 ↑

    // 微信操作相关 ↓

    PRE_SYS_WX_QR_CODE_WX_SIGN_DELETE, // 微信扫码账户注销时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SET_SINGLE_SIGN_IN, // 微信扫码设置统一登录时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SET_PHONE, // 微信扫码设置手机时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_UPDATE_WX, // 微信扫码修改微信时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_UPDATE_EMAIL, // 微信扫码修改邮箱时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SET_EMAIL, // 微信扫码设置邮箱时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_UPDATE_SIGN_IN_NAME, // 微信扫码修改登录名时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SET_SIGN_IN_NAME, // 微信扫码设置登录名时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_UPDATE_PASSWORD, // 微信扫码修改密码时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SET_PASSWORD, // 微信扫码设置密码时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_BIND, // 微信扫码绑定时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SIGN, // 微信扫码登录注册时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    PRE_SYS_WX_QR_CODE_SIGN_IN_SINGLE, // 微信扫码登录注册时，生成的，二维码 id，备注：只有扫描了二维码之后，才会放数据到 redis里面

    // 微信操作相关 ↑

    PRE_SYS_OTHER_APP_WX_WORK_RECEIVE_MESSAGE_ID, // 企业微信消息 id锁，目的：不重复处理消息，锁：【msgId】

    PRE_SYS_OTHER_APP_WX_OFFICIAL_ACCOUNT_RECEIVE_MESSAGE_ID, // 微信公众号消息 id锁，目的：不重复处理消息，锁：【msgId】

    PRE_OTHER_APP_TYPE_AND_APP_ID, // 第三方应用，类型code 和 appid锁，目的：同一个类型下的 appId不能重复，锁：【类型code 和 appid】

    PRE_USER_WALLET_WITHDRAW_LOG, // 用户钱包-提现记录锁，锁：【提现记录主键 id】

    PRE_USER_WALLET, // 用户钱包锁，锁：【用户钱包主键 id】

    PRE_SIGN_CONFIGURATION, // 用户登录注册相关配置锁，锁：【租户主键 id】

    PRE_PAY, // 支付锁，锁：【支付主键 id】

    PRE_TCP_PROTOBUF_CODE, // tcp-protobuf连接锁，锁：【随机码】

    PRE_WEB_SOCKET_CODE, // webSocket连接锁，锁：【随机码】

    PRE_IP_BLACK, // ip黑名单前端，后面跟 ip

    PRE_JWT_HASH, // jwtHash 前缀

    PRE_WX_APP_ID, // 微信 appId（应用）：锁：【微信 appId】
    PRE_WX_OPEN_ID, // 微信 openId（用户）：锁：【微信 openId】，备注：一般锁：微信 openId
    PRE_WX_UNION_ID, // 微信 unionId（平台）：锁：【微信 unionId】

    PRE_PHONE, // 手机号码：锁：【手机号码】
    PRE_EMAIL, // 邮箱：锁：【邮箱】
    PRE_SIGN_IN_NAME, // 登录名：锁：【登录名】

    PRE_TOO_MANY_PASSWORD_ERROR, // 密码错误次数太多：锁【用户主键 id】
    PRE_PASSWORD_ERROR_COUNT, // 密码错误总数：锁【用户主键 id】

    // 【_CACHE】结尾 ↓
    SYS_WX_WORK_KF_RETURN_ASSISTANT_CHECK_CACHE, // 企业微信-微信客服：用户输入转人工之后，5分钟之后如果人工未接待，则又转回智能客服，第二个 key：用户 id

    SYS_WX_WORK_KF_AUTO_ASSISTANT_FLAG_CACHE, // 企业微信-微信客服：当会话状态为：0 未处理时，是否自动交给智能助手接待，默认：true，如果为 false，则放进待接入池等待，用户 id

    SYS_USER_MANAGE_SIGN_IN_FLAG_CACHE, // 用户是否允许登录后台，用户 id

    SYS_USER_DISABLE_CACHE, // 用户是否被冻结，如果存在，则表示，用户被冻结了

    USER_ID_REF_TENANT_ID_SET_CACHE, // 用户 id关联的 tenantIdSet

    SYS_TENANT_DEEP_ID_SET_CACHE, // key：租户 id，value：关联的所有的 子租户（包含本租户）

    SYS_TENANT_CACHE, // 租户缓存，key：租户 id，value：租户信息

    GOOGLE_ACCESS_TOKEN_CACHE, // google接口调用凭据

    XXL_JOB_COOKIE_CACHE, // xxl-job cookie缓存

    WX_WORK_ACCESS_TOKEN_CACHE, // 企业微信全局唯一后台接口调用凭据

    WX_ACCESS_TOKEN_CACHE, // 微信公众号全局唯一后台接口调用凭据

    BAI_DU_ACCESS_TOKEN_CACHE, // 百度全局唯一后台接口调用凭据

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
