package com.cmcorg20230301.engine.be.security.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.security.configuration.BaseConfiguration;
import com.cmcorg20230301.engine.be.security.configuration.security.IJwtValidatorConfiguration;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.security.util.ResponseUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 自定义 jwt过滤器，备注：后续接口方法，无需判断账号是否封禁或者不存在
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static RedissonClient redissonClient;
    private static SecurityProperties securityProperties;
    private static List<IJwtValidatorConfiguration> iJwtValidatorConfigurationList;

    public JwtAuthorizationFilter(RedissonClient redissonClient, SecurityProperties securityProperties,
        List<IJwtValidatorConfiguration> iJwtValidatorConfigurationList) {

        JwtAuthorizationFilter.redissonClient = redissonClient;
        JwtAuthorizationFilter.securityProperties = securityProperties;
        JwtAuthorizationFilter.iJwtValidatorConfigurationList = iJwtValidatorConfigurationList;

    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request, response);

        if (usernamePasswordAuthenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);

    }

    @SneakyThrows
    @Nullable
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
        HttpServletResponse response) {

        // 从请求头里，获取：jwt字符串
        String jwtStr = MyJwtUtil.getJwtStrByRequest(request);

        if (jwtStr == null) {
            return null;
        }

        jwtStr = handleJwtStr(jwtStr); // 处理：jwtStr

        JWT jwt;
        try {
            jwt = JWT.of(jwtStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 获取：userId的值
        Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

        if (userId == null) {
            return null;
        }

        String jwtHash = MyJwtUtil.generateRedisJwtHash(jwtStr, userId, RequestUtil.getRequestCategoryEnum(request));

        String jwtHashRedis = MyCacheUtil.onlyGet(jwtHash, null, true);

        // 判断 jwtHash是否存在于 redis中，如果存在，则表示不能使用
        if (StrUtil.isNotBlank(jwtHashRedis)) {
            return loginExpired(response); // 提示登录过期，请重新登录
        }

        // 设置：jwt的密钥
        if (setJwtKey(jwt, userId)) {
            return null;
        }

        // 验证算法
        if (!jwt.verify()) {
            return loginExpired(response); // 提示登录过期，请重新登录，目的：为了可以随时修改配置的 jwt前缀，或者用户 jwt后缀修改
        }

        try {

            // 校验时间字段：如果过期了，这里会抛出 ValidateException异常
            JWTValidator.of(jwt).validateDate(new Date());

        } catch (ValidateException e) {

            return loginExpired(response); // 提示登录过期，请重新登录

        }

        // 执行：额外的，检查 jwt的方法
        if (CollUtil.isNotEmpty(iJwtValidatorConfigurationList)) {

            for (IJwtValidatorConfiguration item : iJwtValidatorConfigurationList) {

                boolean validFlag = item.validator(jwt, request.getRequestURI(), response);

                if (BooleanUtil.isFalse(validFlag)) {
                    return null;
                }

            }

        }

        // 通过 userId 获取用户具有的权限
        return new UsernamePasswordAuthenticationToken(jwt.getPayload().getClaimsJson(), null,
            MyJwtUtil.getSimpleGrantedAuthorityListByUserId(userId));

    }

    /**
     * 处理：jwtStr
     */
    @Nullable
    private String handleJwtStr(String jwtStr) {

        // 如果不是正式环境
        if (BooleanUtil.isFalse(BaseConfiguration.prodFlag())) {

            // Authorization Bearer 0
            String jwtStrTmp = MyJwtUtil.generateJwt(Convert.toLong(jwtStr), null, null);

            jwtStr = MyJwtUtil.getJwtStrByHeadAuthorization(jwtStrTmp);

        }

        return jwtStr;

    }

    /**
     * 设置：jwt的密钥
     */
    private boolean setJwtKey(JWT jwt, Long userId) {

        String jwtSecretSuf = null;

        if (BaseConstant.ADMIN_ID.equals(userId)) {

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
     */
    @Nullable
    public static UsernamePasswordAuthenticationToken loginExpired(HttpServletResponse response) {

        ResponseUtil.out(response, BaseBizCodeEnum.LOGIN_EXPIRED);

        return null;

    }

}
