package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.List;

import lombok.Data;

@Data
public class SysActivitiParamItemBO {

    /**
     * 输出
     */
    private List<SysActivitiParamSubItemBO> outList;

    /**
     * 是否被执行过，默认：false
     */
    private Boolean execFlag;

    /**
     * 执行错误的错误信息
     */
    private String execErrorMessage;

}
