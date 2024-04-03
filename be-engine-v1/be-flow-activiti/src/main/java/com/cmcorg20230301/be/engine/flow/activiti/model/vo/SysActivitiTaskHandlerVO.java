package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysActivitiTaskHandlerVO {

    /**
     * 是否完成任务
     */
    private Boolean completeFlag;

    /**
     * 是否结束自动执行任务，用于：延时任务，然后由延时任务执行完成之后，调用继续执行任务
     */
    private Boolean endAutoFlag;

}
