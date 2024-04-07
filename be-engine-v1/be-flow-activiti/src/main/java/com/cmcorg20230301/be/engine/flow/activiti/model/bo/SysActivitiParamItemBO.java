package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.List;

import lombok.Data;

@Data
public class SysActivitiParamItemBO {

    /**
     * 参数集合
     */
    private List<SysActivitiParamSubItemBO> paramList;

    /**
     * 是否被执行过，默认：false
     */
    private Boolean execFlag;

    /**
     * 执行错误的错误信息
     */
    private String execErrorMessage;

    /**
     * 来自：节点 id
     */
    private String fromNodeId;

}
