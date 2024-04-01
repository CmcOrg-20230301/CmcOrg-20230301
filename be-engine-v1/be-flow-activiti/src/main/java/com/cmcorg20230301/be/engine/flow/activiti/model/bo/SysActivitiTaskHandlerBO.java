package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SysActivitiTaskHandlerBO {

    /**
     * 节点 map
     */
    private Map<String, SysActivitiNodeBO> nodeBoMap;

    /**
     * 本次任务
     */
    private Task task;

    /**
     * 本次任务在流程图中配置的参数
     */
    private SysActivitiTaskBO sysActivitiTaskBO;

    /**
     * taskService
     */
    private TaskService taskService;

}
