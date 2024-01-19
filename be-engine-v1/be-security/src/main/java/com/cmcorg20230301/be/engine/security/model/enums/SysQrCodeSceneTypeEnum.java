package com.cmcorg20230301.be.engine.security.model.enums;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.model.model.bo.SysQrCodeSceneBindBO;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;

/**
 * 二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum SysQrCodeSceneTypeEnum implements ISysQrCodeSceneType {

    // 微信绑定
    WX_BIND("WX_BIND", BaseConstant.MINUTE_3_EXPIRE_TIME / 1000, (qrCodeSceneValue, redissonClient, sysUserDO) -> {

        RBucket<SysQrCodeSceneBindBO> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_BIND.name() + qrCodeSceneValue);

        SysQrCodeSceneBindBO sysQrCodeSceneBindBO = new SysQrCodeSceneBindBO();

        sysQrCodeSceneBindBO.setUserId(sysUserDO.getId());
        sysQrCodeSceneBindBO.setTenantId(sysUserDO.getTenantId());

        sysQrCodeSceneBindBO.setAppId(sysUserDO.getWxAppId());
        sysQrCodeSceneBindBO.setOpenId(sysUserDO.getWxOpenId());

        bucket.set(sysQrCodeSceneBindBO, Duration.ofMillis(BaseConstant.SECOND_10_EXPIRE_TIME));

    }, true),

    ;

    @EnumValue
    @JsonValue
    private final String code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    private final VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer; // 处理：二维码扫码之后的值

    private final Boolean bindFlag; // 是否是：绑定操作

    // 不是绑定操作的 map
    public static final Map<String, ISysQrCodeSceneType> MAP = MapUtil.newConcurrentHashMap(SysQrCodeSceneTypeEnum.values().length);

    // 绑定操作的 map
    public static final Map<String, ISysQrCodeSceneType> BIND_MAP = MapUtil.newConcurrentHashMap(SysQrCodeSceneTypeEnum.values().length);

    static {

        for (SysQrCodeSceneTypeEnum item : SysQrCodeSceneTypeEnum.values()) {

            if (BooleanUtil.isTrue(item.getBindFlag())) {

                BIND_MAP.put(item.getSceneStr(), item);

            } else {

                MAP.put(item.getSceneStr(), item);

            }

        }

    }

}