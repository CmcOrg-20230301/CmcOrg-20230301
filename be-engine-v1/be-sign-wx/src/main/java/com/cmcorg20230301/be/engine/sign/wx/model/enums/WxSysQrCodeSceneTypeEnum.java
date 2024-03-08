package com.cmcorg20230301.be.engine.sign.wx.model.enums;

import java.time.Duration;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserSingleSignInDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.hutool.core.util.BooleanUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum WxSysQrCodeSceneTypeEnum implements ISysQrCodeSceneType {

    // 登录
    WX_SIGN_IN("WX_SIGN_IN", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<SignInVO> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN.name() + qrCodeSceneValue);

            SignInVO signInVO = SignUtil.signInGetJwt(sysUserDO);

            bucket.set(signInVO, Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 设置密码
    WX_SET_PASSWORD("WX_SET_PASSWORD", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_PASSWORD.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 修改密码
    WX_UPDATE_PASSWORD("WX_UPDATE_PASSWORD", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_PASSWORD.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 设置登录名
    WX_SET_SIGN_IN_NAME("WX_SET_SIGN_IN_NAME", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket = redissonClient
                .getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_SIGN_IN_NAME.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 修改登录名
    WX_UPDATE_SIGN_IN_NAME("WX_UPDATE_SIGN_IN_NAME", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket = redissonClient
                .getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_SIGN_IN_NAME.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 设置邮箱
    WX_SET_EMAIL("WX_SET_EMAIL", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_EMAIL.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 修改邮箱
    WX_UPDATE_EMAIL("WX_UPDATE_EMAIL", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_EMAIL.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 修改微信
    WX_UPDATE_WX("WX_UPDATE_WX", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_WX.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 设置手机
    WX_SET_PHONE("WX_SET_PHONE", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_PHONE.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 设置统一登录
    WX_SET_SINGLE_SIGN_IN("WX_SET_SINGLE_SIGN_IN", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket = redissonClient
                .getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_SINGLE_SIGN_IN.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    // 微信统一登录
    WX_SINGLE_SIGN_IN("WX_SINGLE_SIGN_IN", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            // 获取：微信统一登录的信息
            SysUserSingleSignInDO sysUserSingleSignInDO =
                ChainWrappers.lambdaQueryChain(UserUtil.sysUserSingleSignInMapper)
                    .eq(SysUserSingleSignInDO::getWxAppId, sysUserDO.getWxAppId())
                    .eq(SysUserSingleSignInDO::getWxOpenId, sysUserDO.getWxOpenId())
                    .select(SysUserSingleSignInDO::getId, SysUserSingleSignInDO::getTenantId).one();

            SignInVO signInVO;

            if (sysUserSingleSignInDO == null) {

                signInVO = new SignInVO();

            } else {

                SysUserDO sysUserDoTemp = ChainWrappers.lambdaQueryChain(UserUtil.sysUserMapper)
                    .eq(BaseEntity::getId, sysUserSingleSignInDO.getId())
                    .eq(BaseEntityNoIdSuper::getTenantId, sysUserSingleSignInDO.getTenantId()).one();

                signInVO = SignUtil.signInGetJwt(sysUserDoTemp);

            }

            RBucket<SignInVO> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN_IN_SINGLE.name() + qrCodeSceneValue);

            bucket.set(signInVO, Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, false),

    // 账号注销
    WX_SIGN_DELETE("WX_SIGN_DELETE", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<Long> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_WX_SIGN_DELETE.name() + qrCodeSceneValue);

            bucket.set(sysUserDO.getId(), Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, true),

    ;

    @EnumValue
    @JsonValue
    private final String code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    private final VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer; // 处理：二维码扫码之后的值

    private final Boolean autoSignUpFlag; // 是否自动注册

    static {

        for (WxSysQrCodeSceneTypeEnum item : WxSysQrCodeSceneTypeEnum.values()) {

            if (BooleanUtil.isTrue(item.getAutoSignUpFlag())) {

                SysQrCodeSceneTypeEnum.AUTO_SIGN_UP_MAP.put(item.getSceneStr(), item);

            } else {

                SysQrCodeSceneTypeEnum.NOT_AUTO_SIGN_UP_MAP.put(item.getSceneStr(), item);

            }

        }

    }

}
