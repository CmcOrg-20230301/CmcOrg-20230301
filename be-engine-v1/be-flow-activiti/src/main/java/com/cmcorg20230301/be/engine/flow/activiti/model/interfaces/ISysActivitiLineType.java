package com.cmcorg20230301.be.engine.flow.activiti.model.interfaces;

public interface ISysActivitiLineType {

    int getCode(); // 建议从：10001（包含）开始

    Boolean getFunctionCallFlag(); // 是否是函数调用

}
