package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.service.BaseSysUserInfoService;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = LogTopicConstant.USER_INFO)
public class SysUserInfoUtil {

    private static BaseSysUserInfoService baseSysUserInfoService;

    public SysUserInfoUtil(BaseSysUserInfoService baseSysUserInfoService) {

        SysUserInfoUtil.baseSysUserInfoService = baseSysUserInfoService;

    }

    private static ConcurrentHashMap<Long, SysUserInfoDO> USER_INFO_DO_MAP = new ConcurrentHashMap<>();

    /**
     * 添加 备注：如果为 null，则不会更新该字段
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

        if (lastActiveTime == null && lastIp == null && lastRegion == null) {
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

    /**
     * 通过：用户主键 idSet，获取：用户资料集合
     */
    @NotNull
    public static List<SysUserInfoDO> getUserInfoDOList(Set<Long> userIdSet, boolean addAdminFlag) {

        if (CollUtil.isEmpty(userIdSet)) {
            return new ArrayList<>();
        }

        List<SysUserInfoDO> sysUserInfoDOList =
            baseSysUserInfoService.lambdaQuery().in(SysUserInfoDO::getId, userIdSet).list();

        if (addAdminFlag) {

            sysUserInfoDOList.add(getAdminUserInfoDO());

        }

        return sysUserInfoDOList;

    }

    /**
     * 通过：用户主键 idSet，获取：用户资料 map，key：用户主键 id，value：用户资料
     */
    @NotNull
    public static Map<Long, SysUserInfoDO> getUserInfoDoMap(Set<Long> userIdSet, boolean addAdminFlag) {

        if (CollUtil.isEmpty(userIdSet)) {
            return MapUtil.newHashMap();
        }

        List<SysUserInfoDO> userInfoDOList = getUserInfoDOList(userIdSet, addAdminFlag);

        return userInfoDOList.stream().collect(Collectors.toMap(SysUserInfoDO::getId, it -> it));

    }

    private static SecurityProperties securityProperties;

    @Resource
    public void setSecurityProperties(SecurityProperties securityProperties) {
        SysUserInfoUtil.securityProperties = securityProperties;
    }

    /**
     * 获取：admin的用户信息
     */
    @NotNull
    public static SysUserInfoDO getAdminUserInfoDO() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

        sysUserInfoDO.setId(BaseConstant.ADMIN_ID);

        sysUserInfoDO.setTenantId(currentTenantIdDefault);

        sysUserInfoDO.setAvatarFileId(BaseConstant.SYS_ID);
        sysUserInfoDO.setNickname(securityProperties.getAdminNickname());
        sysUserInfoDO.setBio("");

        return sysUserInfoDO;

    }

    // 微信用户：昵称前缀
    public static final String WX_SYS_USER_INFO_NICKNAME_PRE = "微信用户";

    /**
     * 获取：带有昵称的 用户对象
     */
    @NotNull
    public static SysUserInfoDO getWxSysUserInfoDO() {

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

        sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname(WX_SYS_USER_INFO_NICKNAME_PRE));

        return sysUserInfoDO;

    }

    // 企业微信用户：昵称前缀
    public static final String WX_WORK_SYS_USER_INFO_NICKNAME_PRE = "企业微信用户";

    /**
     * 获取：带有昵称的 用户对象
     */
    @NotNull
    public static SysUserInfoDO getWxWorkSysUserInfoDO() {

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

        sysUserInfoDO.setSignUpType(SysRequestCategoryEnum.WX_WORK);

        sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname(WX_WORK_SYS_USER_INFO_NICKNAME_PRE));

        return sysUserInfoDO;

    }

}
