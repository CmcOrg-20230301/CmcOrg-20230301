package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.List;

import lombok.Data;

@Data
public class SysActivitiParamBO {

    /**
     * 输出
     */
    private List<List<SysActivitiParamItemBO>> outList;

}
