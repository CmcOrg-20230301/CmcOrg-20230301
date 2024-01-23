package com.cmcorg20230301.be.engine.security.model.interfaces;

import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.util.util.SeparatorUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import org.redisson.api.RedissonClient;

public interface ISysQrCodeSceneType {

    String getCode();

    int getExpireSecond(); // 二维码过期时间，单位：秒，小于等于 0，表示永久

    String SEPARATOR = SeparatorUtil.POUND_SIGN_SEPARATOR;

    default String getSceneStr() {

        return SEPARATOR + getCode();

    }

    VoidFunc3<String, RedissonClient, SysUserDO> getQrSceneValueConsumer(); // 处理：二维码扫码之后的值

    Boolean getAutoSignUpFlag(); // 是否自动注册

}
