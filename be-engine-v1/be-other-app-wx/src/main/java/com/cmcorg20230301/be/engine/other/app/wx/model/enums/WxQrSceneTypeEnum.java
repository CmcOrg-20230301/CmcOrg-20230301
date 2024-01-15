package com.cmcorg20230301.be.engine.other.app.wx.model.enums;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.interfaces.IWxQrSceneType;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.util.util.SeparatorUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;

/**
 * 微信二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum WxQrSceneTypeEnum implements IWxQrSceneType {

    // 登录
    SIGN_IN(101, BaseConstant.MINUTE_10_EXPIRE_TIME / 1000, (qrSceneValue, redissonClient, sysUserDO) -> {

        RBucket<SignInVO> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN.name() + qrSceneValue);

        SignInVO signInVO = SignUtil.signInGetJwt(sysUserDO);

        bucket.set(signInVO, Duration.ofMillis(BaseConstant.SECOND_10_EXPIRE_TIME));

    }),

    ;

    @EnumValue
    @JsonValue
    private final int code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    private final VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer; // 处理：二维码扫码之后的值

    public static final String SEPARATOR = SeparatorUtil.POUND_SIGN_SEPARATOR; // 分隔符

    /**
     * 获取：sceneStr
     */
    public String getSceneStr() {
        return SEPARATOR + getCode();
    }

    public static final Map<String, IWxQrSceneType> MAP = MapUtil.newConcurrentHashMap(WxQrSceneTypeEnum.values().length);

    static {

        for (WxQrSceneTypeEnum item : WxQrSceneTypeEnum.values()) {

            MAP.put(item.getSceneStr(), item);

        }

    }

}
