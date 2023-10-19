package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.model.constant.SecurityConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.service.BaseSysRequestService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j(topic = LogTopicConstant.REQUEST)
public class RequestUtil {

    public static final String[] IP_HEADER_ARR =
        {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"};

    @Resource
    BaseSysRequestService baseSysRequestService;

    private static CopyOnWriteArrayList<SysRequestDO> SYS_REQUEST_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 添加一个：请求数据
     */
    public static void add(SysRequestDO sysRequestDO) {

        SYS_REQUEST_DO_LIST.add(sysRequestDO);

    }

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        CopyOnWriteArrayList<SysRequestDO> tempSysRequestDOList;

        synchronized (SYS_REQUEST_DO_LIST) {

            if (CollUtil.isEmpty(SYS_REQUEST_DO_LIST)) {
                return;
            }

            tempSysRequestDOList = SYS_REQUEST_DO_LIST;
            SYS_REQUEST_DO_LIST = new CopyOnWriteArrayList<>();

        }

        log.info("保存请求数据，长度：{}", tempSysRequestDOList.size());

        // 目的：防止还有程序往：tempList，里面添加数据，所以这里等待一会
        MyThreadUtil.schedule(() -> {

            // 批量保存数据
            baseSysRequestService.saveBatch(tempSysRequestDOList);

        }, DateUtil.offsetSecond(new Date(), 2));

    }

    /**
     * 获取当前上下文的 request对象
     */
    @Nullable
    public static HttpServletRequest getRequest() {

        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            return null;
        }

        return requestAttributes.getRequest();

    }

    /**
     * 获取请求类别
     */
    @NotNull
    public static SysRequestCategoryEnum getRequestCategoryEnum() {

        return getRequestCategoryEnum(getRequest());

    }

    /**
     * 获取请求类别
     */
    @NotNull
    public static SysRequestCategoryEnum getRequestCategoryEnum(HttpServletRequest httpServletRequest) {

        if (httpServletRequest == null) {
            return SysRequestCategoryEnum.PC_BROWSER_WINDOWS;
        }

        return SysRequestCategoryEnum
            .getByCode(Convert.toInt(httpServletRequest.getHeader(SecurityConstant.REQUEST_HEADER_CATEGORY)));

    }

}
