package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysSqlSlowDO;
import com.cmcorg20230301.be.engine.security.service.SysSqlSlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j(topic = LogTopicConstant.SQL)
public class SqlUtil {

    @Resource
    SysSqlSlowService sqlSlowService;

    private static CopyOnWriteArrayList<SysSqlSlowDO> SYS_SQL_SLOW_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 添加一个：慢sql日志数据
     */
    public static void add(SysSqlSlowDO sysSqlSlowDO) {

        SYS_SQL_SLOW_DO_LIST.add(sysSqlSlowDO);

    }

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        CopyOnWriteArrayList<SysSqlSlowDO> tempSysSqlSlowDOList;

        synchronized (SYS_SQL_SLOW_DO_LIST) {

            if (CollUtil.isEmpty(SYS_SQL_SLOW_DO_LIST)) {
                return;
            }

            tempSysSqlSlowDOList = SYS_SQL_SLOW_DO_LIST;
            SYS_SQL_SLOW_DO_LIST = new CopyOnWriteArrayList<>();

        }

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            log.info("保存慢 sql日志数据，长度：{}", tempSysSqlSlowDOList.size());

            // 批量保存数据
            sqlSlowService.saveBatch(tempSysSqlSlowDOList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

}
