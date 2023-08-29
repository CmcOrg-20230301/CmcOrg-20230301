package com.cmcorg20230301.be.engine.security.model.configuration;

import cn.hutool.jwt.JWT;

import javax.servlet.http.HttpServletResponse;

public interface IJwtValidatorConfiguration {

    /**
     * 额外的，检查 jwt的方法，检查通过，返回：true
     */
    boolean validator(final JWT jwt, final String requestUri, final HttpServletResponse response);

}
