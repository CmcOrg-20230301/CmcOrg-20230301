package com.cmcorg20230301.be.engine.flow.activiti.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.flow.activiti.model.bo.SysActivitiParamBO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_activiti_process_instance")
@Data
@Schema(description = "主表：流程实例全局参数")
public class SysActivitiProcessInstanceDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "流程实例 id")
    private String processInstanceId;

    /**
     * {@link SysActivitiParamBO}
     */
    @Schema(description = "流程实例全局参数：SysActivitiParamBO对象")
    private String processInstanceJsonStr;

}