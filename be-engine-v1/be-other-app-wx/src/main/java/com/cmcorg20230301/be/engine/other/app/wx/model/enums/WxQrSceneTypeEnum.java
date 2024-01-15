package com.cmcorg20230301.be.engine.other.app.wx.model.enums;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.other.app.wx.model.interfaces.IWxQrSceneType;
import com.cmcorg20230301.be.engine.util.util.SeparatorUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 微信二维码，scene的类型
 */
@AllArgsConstructor
@Getter
public enum WxQrSceneTypeEnum implements IWxQrSceneType {

    SIGN_IN(101, BaseConstant.MINUTE_30_EXPIRE_TIME), // 登录

    ;

    @EnumValue
    @JsonValue
    private final int code; // 编码

    private final int expireSecond; // 二维码过期时间，单位：秒，小于等于 0，表示永久

    /**
     * 获取：sceneStr
     */
    public String getSceneStr() {
        return SeparatorUtil.POUND_SIGN_SEPARATOR + getCode();
    }

    public static final Map<String, IWxQrSceneType> MAP = MapUtil.newHashMap(WxQrSceneTypeEnum.values().length);

    static {

        for (WxQrSceneTypeEnum item : WxQrSceneTypeEnum.values()) {

            MAP.put(item.getSceneStr(), item);

        }

    }

}
