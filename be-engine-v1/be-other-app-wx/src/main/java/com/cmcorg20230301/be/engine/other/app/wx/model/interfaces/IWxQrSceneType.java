package com.cmcorg20230301.be.engine.other.app.wx.model.interfaces;

public interface IWxQrSceneType {

    int getCode(); // 建议从：10001（包含）开始

    int getExpireSecond(); // 二维码过期时间，单位：秒，小于等于 0，表示永久

    String getSceneStr();

}
