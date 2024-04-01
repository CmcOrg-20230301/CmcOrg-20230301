package com.cmcorg20230301.be.engine.flow.activiti.model.interfaces;

import com.cmcorg20230301.be.engine.flow.activiti.model.bo.SysActivitiTaskHandlerBO;

import cn.hutool.core.lang.func.Func1;

public interface ISysActivitiTaskCategory {

    int getCode(); // 建议从：10001（包含）开始

    String getName(); // 分类名称

    Func1<SysActivitiTaskHandlerBO, Boolean> getHandler(); // 处理器

}
