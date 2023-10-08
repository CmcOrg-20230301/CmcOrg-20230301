package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletLogMapper;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
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

        log.info("保存用户钱包操作日志，长度：{}", tempSysUserWalletLogDoList.size());

        // 批量保存数据
        saveBatch(tempSysUserWalletLogDoList);

    }

    /**
     * 添加：用户钱包操作日志
     */
    public static void add(SysUserWalletLogDO sysUserWalletLogDO) {

        SYS_USER_WALLET_LOG_DO_LIST.add(sysUserWalletLogDO);

    }

}
