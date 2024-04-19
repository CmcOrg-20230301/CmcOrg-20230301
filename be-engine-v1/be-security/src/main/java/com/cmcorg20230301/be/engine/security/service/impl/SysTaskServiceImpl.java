package com.cmcorg20230301.be.engine.security.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.mapper.SysTaskMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.ISysTaskConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.SysTaskDO;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysTaskType;
import com.cmcorg20230301.be.engine.security.service.SysTaskService;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.TryUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    @Scheduled(fixedDelay = 3000)
    public void scheduledHandleNotCompleteTask() {

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

                // 处理：任务，备注：如果需要更新任务数据，请添加到：SYS_TASK_DO_UPDATE_LIST
                iSysTaskConfiguration.handle(item);

            });

        }

    }

    public static CopyOnWriteArrayList<SysTaskDO> SYS_TASK_DO_UPDATE_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务，更新数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledUpdate() {

        CopyOnWriteArrayList<SysTaskDO> tempSysTaskDoList;

        synchronized (SYS_TASK_DO_UPDATE_LIST) {

            if (CollUtil.isEmpty(SYS_TASK_DO_UPDATE_LIST)) {
                return;
            }

            tempSysTaskDoList = SYS_TASK_DO_UPDATE_LIST;
            SYS_TASK_DO_UPDATE_LIST = new CopyOnWriteArrayList<>();

        }

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("修改任务数据，长度：{}", tempSysTaskDoList.size());

            // 批量修改数据
            updateBatchById(tempSysTaskDoList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

    private static CopyOnWriteArrayList<SysTaskDO> SYS_TASK_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        CopyOnWriteArrayList<SysTaskDO> tempSysTaskDoList;

        synchronized (SYS_TASK_DO_LIST) {

            if (CollUtil.isEmpty(SYS_TASK_DO_LIST)) {
                return;
            }

            tempSysTaskDoList = SYS_TASK_DO_LIST;
            SYS_TASK_DO_LIST = new CopyOnWriteArrayList<>();

        }

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("新增任务数据，长度：{}", tempSysTaskDoList.size());

            // 批量保存数据
            saveBatch(tempSysTaskDoList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

    /**
     * 添加一个任务
     * 
     * @param type {@link ISysTaskType}
     * 
     * @param expireTs -1 永不过期
     */
    @SneakyThrows
    public static Long addTask(Long userId, Long tenantId, Integer type, String mainId, String businessId,
        Long expireTs, @Nullable VoidFunc1<SysTaskDO> voidFunc1) {

        Long id = IdGeneratorUtil.nextId();

        SysTaskDO sysTaskDO = new SysTaskDO();

        sysTaskDO.setId(id);

        sysTaskDO.setUserId(userId);

        sysTaskDO.setTenantId(tenantId);

        sysTaskDO.setType(type);

        sysTaskDO.setMainId(mainId);

        sysTaskDO.setBusinessId(businessId);

        sysTaskDO.setCompleteFlag(false);

        sysTaskDO.setExpireTs(expireTs);

        if (voidFunc1 != null) {

            voidFunc1.call(sysTaskDO);

        }

        SYS_TASK_DO_LIST.add(sysTaskDO);

        return id;

    }

}
