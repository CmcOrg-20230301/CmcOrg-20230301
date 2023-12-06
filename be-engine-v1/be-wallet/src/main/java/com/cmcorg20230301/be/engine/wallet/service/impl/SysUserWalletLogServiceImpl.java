package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletLogMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogUserSelfPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j(topic = LogTopicConstant.USER_WALLET)
public class SysUserWalletLogServiceImpl extends ServiceImpl<SysUserWalletLogMapper, SysUserWalletLogDO>
    implements SysUserWalletLogService {

    private static CopyOnWriteArrayList<SysUserWalletLogDO> SYS_USER_WALLET_LOG_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        CopyOnWriteArrayList<SysUserWalletLogDO> tempSysUserWalletLogDoList;

        synchronized (SYS_USER_WALLET_LOG_DO_LIST) {

            if (CollUtil.isEmpty(SYS_USER_WALLET_LOG_DO_LIST)) {
                return;
            }

            tempSysUserWalletLogDoList = SYS_USER_WALLET_LOG_DO_LIST;
            SYS_USER_WALLET_LOG_DO_LIST = new CopyOnWriteArrayList<>();

        }

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("保存用户钱包操作日志，长度：{}", tempSysUserWalletLogDoList.size());

            // 批量保存数据
            saveBatch(tempSysUserWalletLogDoList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

    /**
     * 添加：用户钱包操作日志
     */
    public static void add(SysUserWalletLogDO sysUserWalletLogDO) {

        SYS_USER_WALLET_LOG_DO_LIST.add(sysUserWalletLogDO);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletLogDO> myPage(SysUserWalletLogPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().eq(dto.getUserId() != null, SysUserWalletLogDO::getUserId, dto.getUserId())
            .eq(dto.getType() != null, SysUserWalletLogDO::getType, dto.getType())

            .ne(SysUserWalletLogDO::getWithdrawableMoneyChange, BigDecimal.ZERO)

            .like(StrUtil.isNotBlank(dto.getName()), SysUserWalletLogDO::getName, dto.getName())

            .le(dto.getCtEndTime() != null, SysUserWalletLogDO::getCreateTime, dto.getCtEndTime())

            .ge(dto.getCtBeginTime() != null, SysUserWalletLogDO::getCreateTime, dto.getCtBeginTime())

            .like(StrUtil.isNotBlank(dto.getRemark()), SysUserWalletLogDO::getRemark, dto.getRemark())

            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //

            .orderByDesc(BaseEntityNoIdSuper::getUpdateTime).page(dto.page(true));

    }

    /**
     * 分页排序查询-租户
     */
    @Override
    public Page<SysUserWalletLogDO> myPageTenant(SysUserWalletLogUserSelfPageDTO dto) {

        // 检查：租户 id是否属于自己
        SysTenantUtil.checkAndGetTenantIdSet(true, dto.getTenantIdSet());

        SysUserWalletLogPageDTO sysUserWalletLogPageDTO = BeanUtil.copyProperties(dto, SysUserWalletLogPageDTO.class);

        sysUserWalletLogPageDTO.setUserId(BaseConstant.TENANT_USER_ID);

        return myPage(sysUserWalletLogPageDTO);

    }

    /**
     * 分页排序查询-用户
     */
    @Override
    public Page<SysUserWalletLogDO> myPageUserSelf(SysUserWalletLogUserSelfPageDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserWalletLogPageDTO sysUserWalletLogPageDTO = BeanUtil.copyProperties(dto, SysUserWalletLogPageDTO.class);

        sysUserWalletLogPageDTO.setUserId(currentUserId);

        return myPage(sysUserWalletLogPageDTO);

    }

}
