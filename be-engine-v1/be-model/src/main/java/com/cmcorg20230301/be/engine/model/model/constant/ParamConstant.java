package com.cmcorg20230301.be.engine.model.model.constant;

/**
 * 参数配置相关的常量类
 */
public interface ParamConstant {

    // 参数配置相关 ↓

    String RSA_PRIVATE_KEY_UUID = "1"; // 非对称加密，密钥 uuid

    String IP_REQUESTS_PER_SECOND_UUID = "2"; // ip请求速率 uuid

    String TENANT_REF_CHILDREN_FLAG_UUID = "3"; // 是否关联子级租户 uuid：0 表示不关联 1 表示关联

    String DEFAULT_MANAGE_SIGN_IN_FLAG = "4"; // 默认后台登录：0 不允许 1 允许

    // 参数配置相关 ↑

}
