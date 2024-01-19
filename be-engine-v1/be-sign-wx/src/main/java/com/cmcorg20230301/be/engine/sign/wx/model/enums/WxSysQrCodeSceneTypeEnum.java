package com.cmcorg20230301.be.engine.sign.wx.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;

/**
 * 微信二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum WxSysQrCodeSceneTypeEnum implements ISysQrCodeSceneType {

    // 登录
    WX_SIGN_IN("WX_SIGN_IN", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000, (qrCodeSceneValue, redissonClient, sysUserDO) -> {

        RBucket<SignInVO> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN.name() + qrCodeSceneValue);

        SignInVO signInVO = SignUtil.signInGetJwt(sysUserDO);

        bucket.set(signInVO, Duration.ofMillis(BaseConstant.SECOND_10_EXPIRE_TIME));

    }, false),

    // 设置密码
    WX_SET_PASSWORD("WX_SET_PASSWORD", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000, (qrCodeSceneValue, redissonClient, sysUserDO) -> {

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_PASSWORD.name() + qrCodeSceneValue);

        bucket.set("", Duration.ofMillis(BaseConstant.SECOND_10_EXPIRE_TIME));

    }, false),

    ;

    @EnumValue
    @JsonValue
    private final String code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    private final VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer; // 处理：二维码扫码之后的值

    private final Boolean bindFlag; // 是否是：绑定操作

    static {

        for (WxSysQrCodeSceneTypeEnum item : WxSysQrCodeSceneTypeEnum.values()) {

            SysQrCodeSceneTypeEnum.MAP.put(item.getSceneStr(), item);

        }

    }

}
