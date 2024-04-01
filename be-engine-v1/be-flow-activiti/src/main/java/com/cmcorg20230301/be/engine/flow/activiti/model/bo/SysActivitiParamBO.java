package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SysActivitiParamBO {

    /**
     * 输入，key，流程图里面的 id，value：历史和当前的参数集合
     */
    private Map<String, List<SysActivitiParamItemBO>> inMap;

}
