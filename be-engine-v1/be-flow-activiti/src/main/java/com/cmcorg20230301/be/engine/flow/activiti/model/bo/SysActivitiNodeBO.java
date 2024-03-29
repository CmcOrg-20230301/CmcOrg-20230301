package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import java.util.LinkedHashSet;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;

import lombok.Data;

@Data
public class SysActivitiNodeBO {

    /**
     * 指向本节点的线
     */
    private LinkedHashSet<SequenceFlow> preLineSet = new LinkedHashSet<>();

    /**
     * 以本节点为起点的线
     */
    private LinkedHashSet<SequenceFlow> sufLineSet = new LinkedHashSet<>();

    /**
     * 指向本节点的节点
     */
    private LinkedHashSet<FlowElement> preNodeSet = new LinkedHashSet<>();

    /**
     * 本节点的下一个节点
     */
    private LinkedHashSet<FlowElement> sufNodeSet = new LinkedHashSet<>();

}
