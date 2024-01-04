package com.cmcorg20230301.be.engine.security.configuration.security;

import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.ExceptionAdvice;
import com.cmcorg20230301.be.engine.security.util.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 未登录异常
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {

        // 尚未登录，请先登录
        ResponseUtil.out(response, BaseBizCodeEnum.NOT_LOGGED_IN_YET);

        ExceptionAdvice.handleRequest(request, null, BaseBizCodeEnum.NOT_LOGGED_IN_YET.getMsg(), "");

    }

}
