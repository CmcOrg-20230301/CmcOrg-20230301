package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.service.BaseSysUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j(topic = LogTopicConstant.USER_INFO)
public class SysUserInfoUtil {

    private static BaseSysUserInfoService baseSysUserInfoService;

    public SysUserInfoUtil(BaseSysUserInfoService baseSysUserInfoService) {

        SysUserInfoUtil.baseSysUserInfoService = baseSysUserInfoService;

    }

    private static ConcurrentHashMap<Long, SysUserInfoDO> USER_INFO_DO_MAP = new ConcurrentHashMap<>();

    /**
     * 添加
     * 备注：如果为 null，则不会更新该字段
     */
    public static void add(Long id, @Nullable Date lastActiveTime, @Nullable String lastIp,
        @Nullable String lastRegion) {

        if (id == null) {
            return;
        }

        if (BaseConstant.NEGATIVE_ONE_LONG.equals(id)) {
            return;
        }

        if (UserUtil.getCurrentUserAdminFlag(id)) {
            return;
        }

        if (lastActiveTime == null || lastIp == null || lastRegion == null) {
            return;
        }

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

        sysUserInfoDO.setId(id);

        sysUserInfoDO.setLastActiveTime(lastActiveTime);
        sysUserInfoDO.setLastIp(lastIp);
        sysUserInfoDO.setLastRegion(lastRegion);

        // 添加
        add(sysUserInfoDO);

    }

    /**
     * 添加
     */
    public static void add(SysUserInfoDO sysUserInfoDO) {

        if (sysUserInfoDO.getId() == null) {
            return;
        }

        USER_INFO_DO_MAP.put(sysUserInfoDO.getId(), sysUserInfoDO);

    }

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        ConcurrentHashMap<Long, SysUserInfoDO> tempUserInfoDoMap;

        synchronized (USER_INFO_DO_MAP) {

            if (CollUtil.isEmpty(USER_INFO_DO_MAP)) {
                return;
            }

            tempUserInfoDoMap = USER_INFO_DO_MAP;
            USER_INFO_DO_MAP = new ConcurrentHashMap<>();

        }

        // 目的：防止还有程序往：tempUserInfoDoMap，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("保存用户信息数据，长度：{}", tempUserInfoDoMap.size());

            // 批量更新数据
            baseSysUserInfoService.updateBatchById(tempUserInfoDoMap.values());

        }, DateUtil.offsetSecond(new Date(), 2));

    }

}
