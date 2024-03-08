package com.cmcorg20230301.be.engine.security.model.enums;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.bo.SysQrCodeSceneBindBO;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserSingleSignInDO;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.Duration;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

/**
 * 二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum SysQrCodeSceneTypeEnum implements ISysQrCodeSceneType {

    // 微信绑定
    WX_BIND("WX_BIND", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000, (qrCodeSceneValue, redissonClient, sysUserDO) -> {

        RBucket<SysQrCodeSceneBindBO> bucket =
            redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_BIND.name() + qrCodeSceneValue);

        SysQrCodeSceneBindBO sysQrCodeSceneBindBO = new SysQrCodeSceneBindBO();

        sysQrCodeSceneBindBO.setUserId(sysUserDO.getId());
        sysQrCodeSceneBindBO.setTenantId(sysUserDO.getTenantId());

        sysQrCodeSceneBindBO.setAppId(sysUserDO.getWxAppId());
        sysQrCodeSceneBindBO.setOpenId(sysUserDO.getWxOpenId());

        bucket.set(sysQrCodeSceneBindBO, Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

    }, false),

    // 微信统一登录绑定
    WX_SINGLE_SIGN_IN_BIND("WX_SINGLE_SIGN_IN_BIND", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000,
        (qrCodeSceneValue, redissonClient, sysUserDO) -> {

            RBucket<SysQrCodeSceneBindBO> bucket =
                redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX.name() + qrCodeSceneValue);

            // 微信统一登录不能重复
            SysUserSingleSignInDO sysUserSingleSignInDO =
                ChainWrappers.lambdaQueryChain(UserUtil.sysUserSingleSignInMapper)
                    .eq(SysUserSingleSignInDO::getWxAppId, sysUserDO.getWxAppId())
                    .eq(SysUserSingleSignInDO::getWxOpenId, sysUserDO.getWxOpenId())
                    .select(SysUserSingleSignInDO::getId, SysUserSingleSignInDO::getTenantId).one();

            SysQrCodeSceneBindBO sysQrCodeSceneBindBO = new SysQrCodeSceneBindBO();

            if (sysUserSingleSignInDO != null) {

                sysQrCodeSceneBindBO.setUserId(sysUserSingleSignInDO.getId());
                sysQrCodeSceneBindBO.setTenantId(sysUserSingleSignInDO.getTenantId());

            }

            sysQrCodeSceneBindBO.setAppId(sysUserDO.getWxAppId());
            sysQrCodeSceneBindBO.setOpenId(sysUserDO.getWxOpenId());

            bucket.set(sysQrCodeSceneBindBO, Duration.ofMillis(BaseConstant.MINUTE_3_EXPIRE_TIME));

        }, false),

    ;

    @EnumValue
    @JsonValue
    private final String code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    private final VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer; // 处理：二维码扫码之后的值

    private final Boolean autoSignUpFlag; // 是否自动注册

    // 自动注册的 map
    public static final Map<String, ISysQrCodeSceneType> AUTO_SIGN_UP_MAP =
        MapUtil.newConcurrentHashMap(SysQrCodeSceneTypeEnum.values().length);

    // 不自动注册的 map
    public static final Map<String, ISysQrCodeSceneType> NOT_AUTO_SIGN_UP_MAP =
        MapUtil.newConcurrentHashMap(SysQrCodeSceneTypeEnum.values().length);

    static {

        for (SysQrCodeSceneTypeEnum item : SysQrCodeSceneTypeEnum.values()) {

            if (BooleanUtil.isTrue(item.getAutoSignUpFlag())) {

                AUTO_SIGN_UP_MAP.put(item.getSceneStr(), item);

            } else {

                NOT_AUTO_SIGN_UP_MAP.put(item.getSceneStr(), item);

            }

        }

    }

}
