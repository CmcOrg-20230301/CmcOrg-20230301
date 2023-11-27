package com.cmcorg20230301.be.engine.model.exception;

/**
 * 业务消息枚举类的父类
 */
public interface IBizCode {

    int getCode(); // 建议从：300011开始

    String getMsg();

}
