package com.cmcorg20230301.be.engine.security.exception;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.RequestUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 参数校验异常：@Valid
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResultVO<?> handleValidException(MethodArgumentNotValidException e) {

        e.printStackTrace();

        // 返回详细的参数校验错误信息
        Map<String, String> map = MapUtil.newHashMap(e.getBindingResult().getFieldErrors().size());

        BindingResult bindingResult = e.getBindingResult();

        for (FieldError item : bindingResult.getFieldErrors()) {
            map.put(item.getField(), item.getDefaultMessage());
        }

        try {

            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, map); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            Method method = e.getParameter().getMethod();

            if (method != null) {

                // 处理：请求
                handleRequest(httpServletRequest, method.getAnnotation(Operation.class), MyEntityUtil
                        .getNotNullStr(StrUtil.maxLength(baseException.getMessage(), BaseConstant.STR_MAX_LENGTH_1000)), //
                    JSONUtil.toJsonStr(e.getBindingResult().getTarget()));

            }

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    /**
     * 处理：请求
     */
    public static void handleRequest(HttpServletRequest httpServletRequest, @Nullable Operation operation,
        String errorMsg, String requestParam) {

        Date date = new Date();

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        String uri = httpServletRequest.getRequestURI();

        SysRequestDO sysRequestDO = new SysRequestDO();

        sysRequestDO.setUri(uri);
        sysRequestDO.setCostMsStr("");
        sysRequestDO.setCostMs(0L);
        sysRequestDO.setName(operation == null ? "" : operation.summary());

        sysRequestDO.setCategory(RequestUtil.getRequestCategoryEnum(httpServletRequest));
        sysRequestDO.setIp(ServletUtil.getClientIP(httpServletRequest));
        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        sysRequestDO.setSuccessFlag(false);
        sysRequestDO.setErrorMsg(MyEntityUtil.getNotNullStr(errorMsg));
        sysRequestDO.setRequestParam(requestParam);

        // 设置：类型
        sysRequestDO.setType(operation == null ? "" : MyEntityUtil.getNotNullAndTrimStr(operation.description()));
        sysRequestDO.setResponseValue("");

        sysRequestDO.setTenantId(currentTenantIdDefault);

        sysRequestDO.setCreateId(currentUserIdDefault);
        sysRequestDO.setCreateTime(date);
        sysRequestDO.setUpdateId(currentUserIdDefault);
        sysRequestDO.setUpdateTime(date);

        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);
        sysRequestDO.setRemark("");

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

    }

    /**
     * 参数校验异常：断言
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResultVO<?> handleIllegalArgumentException(IllegalArgumentException e) {

        e.printStackTrace();

        try {

            ApiResultVO.errorMsg(e.getMessage()); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    /**
     * 参数校验异常：springframework
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ApiResultVO<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        e.printStackTrace();

        try {

            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, e.getMessage()); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            // 处理：请求
            handleRequest(httpServletRequest, null, MyEntityUtil
                .getNotNullStr(StrUtil.maxLength(baseException.getMessage(), BaseConstant.STR_MAX_LENGTH_1000)), "");

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = BaseException.class)
    public ApiResultVO<?> handleBaseException(BaseException e) {

        e.printStackTrace();

        return getBaseExceptionApiResult(e);

    }

    /**
     * 权限不够时的异常处理
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ApiResultVO<?> handleAccessDeniedException(AccessDeniedException e) {

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        log.info("权限不足：{}，uri：{}", currentUserIdDefault, httpServletRequest.getRequestURI());

        try {

            ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            // 处理：请求
            handleRequest(httpServletRequest, null, MyEntityUtil
                .getNotNullStr(StrUtil.maxLength(baseException.getMessage(), BaseConstant.STR_MAX_LENGTH_1000)), "");

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    /**
     * 不记录日志的异常
     */
    @ExceptionHandler(value = NoLogException.class)
    public ApiResultVO<?> handleNoLogException(NoLogException e) {

        try {

            ApiResultVO.sysError(); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    /**
     * 缺省异常处理，直接提示系统异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ApiResultVO<?> handleThrowable(Throwable e) {

        e.printStackTrace();

        try {

            ApiResultVO.sysError(); // 这里肯定会抛出 BaseException异常

        } catch (BaseException baseException) {

            // 处理：请求
            handleRequest(httpServletRequest, null,
                MyEntityUtil.getNotNullStr(StrUtil.maxLength(e.getMessage(), BaseConstant.STR_MAX_LENGTH_1000)), "");

            return getBaseExceptionApiResult(baseException);

        }

        return null; // 这里不会执行，只是为了通过语法检查

    }

    private ApiResultVO<?> getBaseExceptionApiResult(BaseException e) {

        return e.getApiResultVO();

    }

}
