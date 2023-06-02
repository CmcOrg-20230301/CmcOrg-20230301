package com.cmcorg20230301.engine.be.request.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.cmcorg20230301.engine.be.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.request.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.request.service.SysRequestService;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Aspect
@Component
@Slf4j(topic = LogTopicConstant.REQUEST)
public class SysRequestAop {

    @Resource
    SysRequestService sysRequestService;
    @Resource
    HttpServletRequest httpServletRequest;

    private static List<SysRequestDO> SYS_REQUEST_DO_LIST = new CopyOnWriteArrayList<>();

    private static final int STR_MAX_LENGTH = BaseConstant.STR_MAX_LENGTH_1000 - 3;

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
        sysRequestService.saveBatch(tempSysRequestDOList);

    }

    /**
     * 切入点
     */
    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(operation)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Operation operation) throws Throwable {

        long costMs = System.currentTimeMillis();

        String uri = httpServletRequest.getRequestURI();

        SysRequestDO sysRequestDO = new SysRequestDO();

        // 这个路径不需要记录到数据库
        sysRequestDO.setUri(uri);
        sysRequestDO.setCostMsStr("");
        sysRequestDO.setCostMs(0L);
        sysRequestDO.setName(operation.summary());

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        sysRequestDO.setCreateId(currentUserIdDefault);
        sysRequestDO.setUpdateId(currentUserIdDefault);

        sysRequestDO.setRemark("");
        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);

        // 设置：类型
        sysRequestDO.setType(MyEntityUtil.getNotNullAndTrimStr(operation.description()));

        sysRequestDO.setCategory(RequestUtil.getRequestCategoryEnum(httpServletRequest));

        sysRequestDO.setIp(ServletUtil.getClientIP(httpServletRequest));

        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        sysRequestDO.setSuccessFlag(true);
        sysRequestDO.setErrorMsg("");

        sysRequestDO.setResponseValue("");

        StrBuilder strBuilder = StrBuilder.create();

        int index = 0;

        for (Object item : proceedingJoinPoint.getArgs()) {

            if (index != 0) {
                strBuilder.append(";");
            }

            strBuilder.append(JSONUtil.toJsonStr(item));

            index++;

        }

        sysRequestDO.setRequestParam(StrUtil.maxLength(strBuilder.toString(), STR_MAX_LENGTH));

        Object object = null;

        try {

            if (((MethodSignature)proceedingJoinPoint.getSignature()).getReturnType() == void.class) {

                proceedingJoinPoint.proceed(); // 执行方法，备注：如果执行方法时抛出了异常，catch可以捕获到

            } else {

                object = proceedingJoinPoint.proceed(); // 执行方法，备注：如果执行方法时抛出了异常，catch可以捕获到

                sysRequestDO.setResponseValue(StrUtil.maxLength(JSONUtil.toJsonStr(object), STR_MAX_LENGTH));

            }

        } catch (Throwable e) {

            handleThrowable(sysRequestDO, e, costMs); // 处理：异常

            throw e;

        }

        // 处理：登录相关请求
        handleSignIn(sysRequestDO, object);

        // 处理：耗时相关
        handleCostMs(costMs, sysRequestDO);

        log.info("uri：{}，耗时：{}，成功：{}", sysRequestDO.getUri(), sysRequestDO.getCostMsStr(),
            sysRequestDO.getSuccessFlag());

        SYS_REQUEST_DO_LIST.add(sysRequestDO);

        return object;

    }

    /**
     * 处理：耗时相关
     */
    private void handleCostMs(long costMs, SysRequestDO sysRequestDO) {

        costMs = System.currentTimeMillis() - costMs; // 耗时（毫秒）
        String costMsStr = DateUtil.formatBetween(costMs, BetweenFormatter.Level.MILLISECOND); // 耗时（字符串）

        sysRequestDO.setCostMsStr(costMsStr);
        sysRequestDO.setCostMs(costMs);

    }

    /**
     * 处理：异常
     */
    private void handleThrowable(SysRequestDO sysRequestDO, Throwable e, long costMs) {

        sysRequestDO.setSuccessFlag(false); // 设置：请求失败

        String errorMsg = ExceptionUtil.stacktraceToString(e, STR_MAX_LENGTH);

        sysRequestDO.setErrorMsg(errorMsg);

        // 处理：耗时相关
        handleCostMs(costMs, sysRequestDO);

        SYS_REQUEST_DO_LIST.add(sysRequestDO);

    }

    /**
     * 处理：登录相关请求
     */
    private void handleSignIn(SysRequestDO sysRequestDO, Object object) {

        if (BooleanUtil.isFalse(OperationDescriptionConstant.SIGN_IN.equals(sysRequestDO.getType()))) {
            return;
        }

        // 登录时需要额外处理来获取 用户id
        ApiResultVO<String> apiResultVO = (ApiResultVO)object;

        JWT jwt = JWT.of(MyJwtUtil.getJwtStrByHeadAuthorization(apiResultVO.getData()));

        // 获取：userId的值
        Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

        sysRequestDO.setCreateId(userId);
        sysRequestDO.setUpdateId(userId);

    }

}
