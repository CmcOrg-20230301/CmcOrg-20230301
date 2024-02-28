package com.cmcorg20230301.be.engine.security.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.configuration.security.SecurityConfiguration;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.configuration.IJwtValidatorConfiguration;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 自定义 jwt过滤器，备注：后续接口方法，无需判断账号是否封禁或者不存在
 */
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Resource
    SecurityProperties securityProperties;

    @Resource
    List<IJwtValidatorConfiguration> iJwtValidatorConfigurationList;

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) {

        //        long beginTime = System.currentTimeMillis();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request, response);

        if (usernamePasswordAuthenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        //        log.info("鉴权耗时：{}", DateUtil.formatBetween(System.currentTimeMillis() - beginTime));

        filterChain.doFilter(request, response);

    }

    @SneakyThrows
    @Nullable
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
                                                                  HttpServletResponse response) {

        // 从请求头里，获取：jwt字符串，备注：就算加了不需要登录就可以访问，但是也会走该方法
        String jwtStr = MyJwtUtil.getJwtStrByRequest(request);

        if (jwtStr == null) {
            return null;
        }

        if (SecurityConfiguration.permitAllCheck(request)) {
            return null;
        }

        jwtStr = handleJwtStr(jwtStr); // 处理：jwtStr

        JWT jwt;

        try {

            jwt = JWT.of(jwtStr);

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

            return null;

        }

        // 获取：userId的值
        Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

        // 获取：tenantId的值
        Long tenantId = MyJwtUtil.getPayloadMapTenantIdValue(jwt.getPayload().getClaimsJson());

        if (userId == null || tenantId == null) {
            return null;
        }

        String jwtHash = MyJwtUtil.generateRedisJwtHash(jwtStr, userId, RequestUtil.getRequestCategoryEnum(request));

        String jwtHashRedis = MyCacheUtil.onlyGet(jwtHash);

        // 判断 jwtHash是否存在于 redis中，如果存在，则表示不能使用
        if (StrUtil.isNotBlank(jwtHashRedis)) {
            return loginExpired(response, userId, request); // 提示登录过期，请重新登录
        }

        // 设置：jwt的密钥
        if (setJwtKey(jwt, userId)) {
            return null;
        }

        // 验证算法
        if (jwt.verify() == false) {
            return loginExpired(response, userId, request); // 提示登录过期，请重新登录，目的：为了可以随时修改配置的 jwt前缀，或者用户 jwt后缀修改
        }

        try {

            // 校验时间字段：如果过期了，这里会抛出 ValidateException异常
            JWTValidator.of(jwt).validateDate(new Date());

        } catch (ValidateException e) {

            return loginExpired(response, userId, request); // 提示登录过期，请重新登录

        }

        // 执行：额外的，检查 jwt的方法
        if (CollUtil.isNotEmpty(iJwtValidatorConfigurationList)) {

            for (IJwtValidatorConfiguration item : iJwtValidatorConfigurationList) {

                boolean validFlag = item.validator(jwt, request.getRequestURI(), response);

                if (BooleanUtil.isFalse(validFlag)) {

                    ApiResultVO.error(BaseBizCodeEnum.LOGIN_EXPIRED, userId);

                }

            }

        }

        // 通过 userId 获取用户具有的权限
        return new UsernamePasswordAuthenticationToken(jwt.getPayload().getClaimsJson(), null,
                MyJwtUtil.getSimpleGrantedAuthorityListByUserId(userId, tenantId));

    }

    /**
     * 处理：jwtStr
     */
    @Nullable
    private String handleJwtStr(String jwtStr) {

        // 如果不是正式环境：Authorization Bearer 0
        if (BooleanUtil.isFalse(BaseConfiguration.prodFlag())) {

            if (NumberUtil.isNumber(jwtStr)) {

                SignInVO signInVO = MyJwtUtil.generateJwt(Convert.toLong(jwtStr), null, null, null);

                String jwtStrTmp = signInVO.getJwt();

                log.info("jwtStrTmp：{}", jwtStrTmp);

                jwtStr = MyJwtUtil.getJwtStrByHeadAuthorization(jwtStrTmp);

            }

        }

        return jwtStr;

    }

    /**
     * 设置：jwt的密钥
     */
    private boolean setJwtKey(JWT jwt, Long userId) {

        String jwtSecretSuf = null;

        if (UserUtil.getCurrentUserAdminFlag(userId)) {

            if (BooleanUtil.isFalse(securityProperties.getAdminEnable())) {
                return true;
            }

        } else {

            // 如果不是 admin
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);  // 通过 userId获取到 私钥后缀

            if (StrUtil.isBlank(jwtSecretSuf)) { // 除了 admin账号，每个账号都肯定有 jwtSecretSuf
                return true;
            }

        }

        jwt.setKey(MyJwtUtil.getJwtSecret(jwtSecretSuf).getBytes());

        return false;

    }

    /**
     * 提示登录过期，请重新登录
     * 备注：这里抛出异常不会进入：ExceptionAdvice
     */
    public static UsernamePasswordAuthenticationToken loginExpired(HttpServletResponse response, Long userId,
                                                                   HttpServletRequest request) {

        log.info("登录过期，uri：{}", request.getRequestURI());

        ResponseUtil.out(response, BaseBizCodeEnum.LOGIN_EXPIRED);

        ApiResultVO.error(BaseBizCodeEnum.LOGIN_EXPIRED, userId);

        return null;

    }

}
