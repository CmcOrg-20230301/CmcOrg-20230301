package com.cmcorg20230301.be.engine.model.model.constant;

import java.math.BigDecimal;

/**
 * 通用的常量类
 */
public interface BaseConstant {

    String NEGATIVE_ONE_STR = "-1";

    String ASTERISK = "*";

    // 过期时间相关 ↓

    long ZERO = 0; // 0
    long NEGATIVE_ONE = -1L; // -1
    Long NEGATIVE_ONE_LONG = NEGATIVE_ONE; // -1
    long NEGATIVE_TWO = -2L; // -2

    long DAY_1_EXPIRE_TIME = 60 * 60 * 1000 * 24; // 1天过期
    long DAY_7_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 7L; // 7天过期
    long DAY_15_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 15L; // 15天过期
    long DAY_30_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 30L; // 30天过期
    long YEAR_30_EXPIRE_TIME = 60 * 60 * 1000 * 24 * 365L * 30; // 30年过期
    int HOUR_3_EXPIRE_TIME = 60 * 60 * 1000 * 3; // 3小时过期
    int HOUR_1_EXPIRE_TIME = 60 * 60 * 1000; // 1小时过期
    int MINUTE_30_EXPIRE_TIME = 30 * 60 * 1000; // 30分钟过期
    int MINUTE_20_EXPIRE_TIME = 20 * 60 * 1000; // 20分钟过期
    int MINUTE_10_EXPIRE_TIME = 10 * 60 * 1000; // 10分钟过期，这个一般用于长一点的验证码的过期时间
    int MINUTE_6_EXPIRE_TIME = 6 * 60 * 1000; // 6分钟过期
    int MINUTE_5_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟过期
    int MINUTE_3_EXPIRE_TIME = 3 * 60 * 1000; // 3分钟过期
    int MINUTE_1_EXPIRE_TIME = 60 * 1000; // 1分钟过期
    int SECOND_1_EXPIRE_TIME = 1000; // 1秒钟过期
    int SECOND_2_EXPIRE_TIME = 2000; // 2秒钟过期
    int SECOND_3_EXPIRE_TIME = 3 * 1000; // 3秒钟过期
    int SECOND_5_EXPIRE_TIME = 5 * 1000; // 5秒钟过期
    int SECOND_6_EXPIRE_TIME = 6 * 1000; // 6秒钟过期
    int SECOND_10_EXPIRE_TIME = 10 * 1000; // 10秒钟过期
    int SECOND_20_EXPIRE_TIME = 20 * 1000; // 20秒钟过期，这个一般用于短暂验证码的过期时间
    int SECOND_30_EXPIRE_TIME = 30 * 1000; // 30秒钟过期

    long JWT_EXPIRE_TIME = DAY_1_EXPIRE_TIME; // jwt 过期时间

    long LONG_CODE_EXPIRE_TIME = MINUTE_10_EXPIRE_TIME; // 长一点的验证码的过期时间
    long LONG_CODE_EXPIRE_MINUTE = LONG_CODE_EXPIRE_TIME / 60 / 1000; // 长一点的验证码的过期时间，单位：分钟

    long SHORT_CODE_EXPIRE_TIME = SECOND_20_EXPIRE_TIME; // 短暂验证码的过期时间

    // 过期时间相关 ↑

    // 计算相关 ↓

    BigDecimal BIG_DECIMAL_ONE_HUNDRED = BigDecimal.valueOf(100);

    // 计算相关 ↑

    // id 相关 ↓

    Long ADMIN_ID = ZERO; // 管理员 id
    String ADMIN_ACCOUNT = "admin"; // 管理员 登录名，如果修改，请注意【登录名】注册，是否会有影响
    Long SYS_ID = NEGATIVE_ONE; // 系统/缺省 id，或者表示不存在

    Long TOP_TENANT_ID = ZERO; // 默认租户 id
    String TOP_TENANT_NAME = "默认"; // 默认租户名

    Long TENANT_USER_ID = NEGATIVE_TWO; // 租户的用户 id

    String TENANT_MANAGE_NAME = "后台管理系统"; // 租户管理后台的名称

    Long TOP_PARENT_ID = ZERO; // 顶层 parentId：0

    // id 相关 ↑

    // 字符串长度限制 ↓

    int STR_MAX_LENGTH_500 = 500 - 3;
    int STR_MAX_LENGTH_1000 = 1000 - 3;

    // 字符串长度限制 ↑

}
