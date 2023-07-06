package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.cmcorg20230301.engine.be.security.model.constant.SecurityConstant;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.engine.be.security.service.BaseSysRequestService;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class RequestUtil {

    @Resource
    BaseSysRequestService baseSysRequestService;

    private static List<SysRequestDO> SYS_REQUEST_DO_LIST = new CopyOnWriteArrayList<>();

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

        List<SysRequestDO> tempSysRequestDOList;

        synchronized (SYS_REQUEST_DO_LIST) {

            if (CollUtil.isEmpty(SYS_REQUEST_DO_LIST)) {
                return;
            }

            tempSysRequestDOList = SYS_REQUEST_DO_LIST;
            SYS_REQUEST_DO_LIST = new CopyOnWriteArrayList<>();

        }

        log.info("保存请求数据，长度：{}", tempSysRequestDOList.size());

        // 批量保存数据
        baseSysRequestService.saveBatch(tempSysRequestDOList);

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
