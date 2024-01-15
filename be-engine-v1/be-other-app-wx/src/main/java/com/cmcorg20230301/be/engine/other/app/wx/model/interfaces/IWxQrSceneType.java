package com.cmcorg20230301.be.engine.other.app.wx.model.interfaces;

import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.util.util.VoidFunc3;
import org.redisson.api.RedissonClient;

public interface IWxQrSceneType {

    int getCode(); // 建议从：10001（包含）开始

    int getExpireSecond(); // 二维码过期时间，单位：秒，小于等于 0，表示永久

    String getSceneStr();

    VoidFunc3<String, RedissonClient, SysUserDO> getQrSceneValueConsumer(); // 处理：二维码扫码之后的值

}
