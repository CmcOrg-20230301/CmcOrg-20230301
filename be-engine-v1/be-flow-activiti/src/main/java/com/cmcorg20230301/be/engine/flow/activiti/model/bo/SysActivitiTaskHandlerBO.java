package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SysActivitiTaskHandlerBO {

    private Map<String, SysActivitiNodeBO> nodeBoMap;

    private Task task;

    private String description;

    private TaskService taskService;

}
