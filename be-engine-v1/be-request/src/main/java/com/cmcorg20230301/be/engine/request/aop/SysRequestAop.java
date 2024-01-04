package com.cmcorg20230301.be.engine.request.aop;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import com.cmcorg20230301.be.engine.security.exception.NoLogException;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

@Aspect
@Component
@Slf4j(topic = LogTopicConstant.REQUEST)
public class SysRequestAop {

    /**
     * 切入点
     */
    @Pointcut("@annotation(io.swagger.v3.oas.annotations.Operation)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(operation)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Operation operation) throws Throwable {

        HttpServletRequest httpServletRequest = RequestUtil.getRequest();

        // 目的：因为 socket也会走这里，但是 socket没有 httpServletRequest对象
        if (httpServletRequest == null) {

            if (((MethodSignature) proceedingJoinPoint.getSignature()).getReturnType() == void.class) {

                proceedingJoinPoint.proceed();

                return null;

            } else {

                return proceedingJoinPoint.proceed();

            }

        }

        long costMs = System.currentTimeMillis();

        String uri = httpServletRequest.getRequestURI();

        Date date = new Date();

        SysRequestDO sysRequestDO = new SysRequestDO();

        // 这个路径不需要记录到数据库
        sysRequestDO.setUri(uri);
        sysRequestDO.setCostMsStr("");
        sysRequestDO.setCostMs(0L);
        sysRequestDO.setName(operation.summary());

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        sysRequestDO.setTenantId(currentTenantIdDefault);

        sysRequestDO.setCreateId(currentUserIdDefault);
        sysRequestDO.setCreateTime(date);
        sysRequestDO.setUpdateId(currentUserIdDefault);
        sysRequestDO.setUpdateTime(date);

        sysRequestDO.setRemark("");
        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);

        // 设置：类型
        sysRequestDO.setType(MyEntityUtil.getNotNullAndTrimStr(operation.description()));

        sysRequestDO.setCategory(RequestUtil.getRequestCategoryEnum(httpServletRequest));

        sysRequestDO.setIp(ServletUtil.getClientIP(httpServletRequest));

        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        // 更新：用户信息
        SysUserInfoUtil.add(currentUserIdDefault, date, sysRequestDO.getIp(), sysRequestDO.getRegion());

        sysRequestDO.setSuccessFlag(true);
        sysRequestDO.setErrorMsg("");

        sysRequestDO.setResponseValue("");

        JSONObject jsonObject = JSONUtil.createObj();

        for (Object item : proceedingJoinPoint.getArgs()) {

            jsonObject.set(item.getClass().getSimpleName(), JSONUtil.toJsonStr(item));

        }

        sysRequestDO.setRequestParam(jsonObject.toString());

        Object object = null;

        try {

            if (((MethodSignature) proceedingJoinPoint.getSignature()).getReturnType() == void.class) {

                proceedingJoinPoint.proceed(); // 执行方法，备注：如果执行方法时抛出了异常，catch可以捕获到

            } else {

                object = proceedingJoinPoint.proceed(); // 执行方法，备注：如果执行方法时抛出了异常，catch可以捕获到

                sysRequestDO.setResponseValue(MyEntityUtil.getNotNullStr(JSONUtil.toJsonStr(object)));

            }

        } catch (Throwable e) {

            handleThrowable(sysRequestDO, e, costMs); // 处理：异常

            if (e instanceof BaseException || e instanceof IllegalArgumentException) {

                throw e;

            }

            if (e instanceof UndeclaredThrowableException) {

                e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();

            }

            MyExceptionUtil.printError(e);

            throw new NoLogException();

        }

        // 处理：登录相关请求
        handleSignIn(sysRequestDO, object);

        // 处理：耗时相关
        handleCostMs(costMs, sysRequestDO);

        log.info("uri：{}，耗时：{}，成功：{}", sysRequestDO.getUri(), sysRequestDO.getCostMsStr(),
                sysRequestDO.getSuccessFlag());

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

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

        String errorMsg = ExceptionUtil.stacktraceToString(e);

        sysRequestDO.setErrorMsg(MyEntityUtil.getNotNullStr(errorMsg));

        // 处理：耗时相关
        handleCostMs(costMs, sysRequestDO);

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

    }

    /**
     * 处理：登录相关请求
     */
    private void handleSignIn(SysRequestDO sysRequestDO, Object object) {

        if (BooleanUtil.isFalse(OperationDescriptionConstant.SIGN_IN.equals(sysRequestDO.getType()))) {
            return;
        }

        // 登录时需要额外处理来获取 用户id
        ApiResultVO<SignInVO> apiResultVO = (ApiResultVO) object;

        JWT jwt = JWT.of(MyJwtUtil.getJwtStrByHeadAuthorization(apiResultVO.getData().getJwt()));

        // 获取：userId的值
        Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

        // 获取：tenantId的值
        Long tenantId = MyJwtUtil.getPayloadMapTenantIdValue(jwt.getPayload().getClaimsJson());

        sysRequestDO.setCreateId(userId);
        sysRequestDO.setUpdateId(userId);

        sysRequestDO.setTenantId(tenantId);

    }

}
