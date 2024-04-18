package com.cmcorg20230301.be.engine.security.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysTaskMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.ISysTaskConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.SysTaskDO;
import com.cmcorg20230301.be.engine.security.service.SysTaskService;
import com.cmcorg20230301.be.engine.security.util.TryUtil;

import cn.hutool.core.collection.CollUtil;

@Service
public class SysTaskServiceImpl extends ServiceImpl<SysTaskMapper, SysTaskDO> implements SysTaskService {

    // 任务处理器
    public static final Map<Integer, ISysTaskConfiguration> I_SYS_TASK_CONFIGURATION_MAP = new HashMap<>();

    public SysTaskServiceImpl(
        @Autowired(required = false) @Nullable List<ISysTaskConfiguration> iSysTaskConfigurationList) {

        if (CollUtil.isNotEmpty(iSysTaskConfigurationList)) {

            for (ISysTaskConfiguration item : iSysTaskConfigurationList) {

                I_SYS_TASK_CONFIGURATION_MAP.put(item.getCode(), item);

            }

        }

    }

    /**
     * 定时任务，处理未完成的任务
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        if (CollUtil.isEmpty(I_SYS_TASK_CONFIGURATION_MAP)) {
            return;
        }

        List<SysTaskDO> sysTaskDOList = lambdaQuery().eq(SysTaskDO::getCompleteFlag, false).list();

        if (CollUtil.isEmpty(sysTaskDOList)) {
            return;
        }

        for (SysTaskDO item : sysTaskDOList) {

            ISysTaskConfiguration iSysTaskConfiguration = I_SYS_TASK_CONFIGURATION_MAP.get(item.getType());

            if (iSysTaskConfiguration == null) {
                continue;
            }

            TryUtil.tryCatch(() -> {

                iSysTaskConfiguration.handle(item);

            });

        }

    }

}
