package com.cmcorg20230301.be.engine.flow.activiti.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.flow.activiti.mapper.SysActivitiProcessInstanceMapper;
import com.cmcorg20230301.be.engine.flow.activiti.model.entity.SysActivitiProcessInstanceDO;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiProcessInstanceService;

@Service
public class SysActivitiProcessInstanceServiceImpl
    extends ServiceImpl<SysActivitiProcessInstanceMapper, SysActivitiProcessInstanceDO>
    implements SysActivitiProcessInstanceService {

}
