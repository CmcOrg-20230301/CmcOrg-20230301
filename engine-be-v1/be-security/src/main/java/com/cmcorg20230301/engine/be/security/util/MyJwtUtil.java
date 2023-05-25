package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.constant.SecurityConstant;
import com.cmcorg20230301.engine.be.security.model.entity.SysMenuDO;
import com.cmcorg20230301.engine.be.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class MyJwtUtil {

    // 系统里的 jwt密钥
    private static final String JWT_SECRET_SYS =
        "4282dde8cb54c0c68082ada1b1d9ce048195cd309jqk0e07d1ed3e1871b462a8b75fee46467b96f33dea65a11862f1ea4867aed76243dfe7e1efb89638d3da6570d1";

    public static final String PAYLOAD_MAP_USER_ID_KEY = "userId";

    private static SecurityProperties securityProperties;
    private static SysUserMapper sysUserMapper;

    public MyJwtUtil(SecurityProperties securityProperties, SysUserMapper sysUserMapper) {

        MyJwtUtil.securityProperties = securityProperties;
        MyJwtUtil.sysUserMapper = sysUserMapper;

    }

    /**
     * 获取：jwt中的 userId值
     */
    @Nullable
    public static Long getPayloadMapUserIdValue(@Nullable JSONObject claimsJson) {

        if (claimsJson == null) {
            return null;
        }

        NumberWithFormat numberWithFormat = (NumberWithFormat)claimsJson.get(MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY);

        if (numberWithFormat == null) {
            return null;
        }

        return numberWithFormat.longValue();

    }

    /**
     * 统一生成 jwt
     */
    @Nullable
    public static String generateJwt(Long userId, String jwtSecretSuf, Consumer<JSONObject> consumer) {

        if (userId == null) {
            return null;
        }

        if (BaseConstant.ADMIN_ID.equals(userId) && BooleanUtil
            .isFalse(MyJwtUtil.securityProperties.getAdminEnable())) {
            return null;
        }

        if (StrUtil.isBlank(jwtSecretSuf)) {
            // 获取用户 jwt私钥后缀，通过 userId
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);
        }

        if (BooleanUtil.isFalse(BaseConstant.ADMIN_ID.equals(userId)) && StrUtil.isBlank(jwtSecretSuf)) {
            return null;
        }

        RedissonUtil.batch((batch) -> {

            // 移除密码错误次数相关
            batch.getBucket(RedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + userId).deleteAsync();
            batch.getMap(RedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).removeAsync(userId);

        });

        // 生成 jwt
        return MyJwtUtil.sign(userId, jwtSecretSuf, consumer);

    }

    /**
     * 生成 jwt
     */
    @NotNull
    private static String sign(Long userId, String jwtSecretSuf, Consumer<JSONObject> consumer) {

        JSONObject payloadMap = JSONUtil.createObj().set(PAYLOAD_MAP_USER_ID_KEY, userId);

        if (consumer != null) {
            consumer.accept(payloadMap);
        }

        String jwt = JWT.create() //
            .setExpiresAt(new Date(System.currentTimeMillis() + BaseConstant.JWT_EXPIRE_TIME)) // 设置过期时间
            .addPayloads(payloadMap) // 增加JWT载荷信息
            .setKey(MyJwtUtil.getJwtSecret(jwtSecretSuf).getBytes()) // 设置密钥
            .sign();

        return SecurityConstant.JWT_PREFIX + jwt;

    }

    /**
     * 生成 redis中，jwt存储使用的 key（jwtHash），目的：不直接暴露明文的 jwt
     */
    @NotNull
    public static String generateRedisJwtHash(String jwtStr, Long userId,
        SysRequestCategoryEnum sysRequestCategoryEnum) {

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(RedisKeyEnum.PRE_JWT_HASH.name()).append(":").append(userId).append(":")
            .append(sysRequestCategoryEnum.getCode()).append(":").append(DigestUtil.sha512Hex(jwtStr));

        return strBuilder.toString();

    }

    /**
     * 获取 jwt密钥：配置的私钥前缀 + JWT_SECRET_SYS + 用户的私钥后缀
     * 备注：admin的 jwtSecretSuf 就是 "null"
     */
    @NotNull
    public static String getJwtSecret(String jwtSecretSuf) {

        return MyJwtUtil.securityProperties.getJwtSecretPre() + MyJwtUtil.JWT_SECRET_SYS + jwtSecretSuf;

    }

    /**
     * 从请求头里，获取：jwt字符串
     */
    @Nullable
    public static String getJwtStrByRequest(HttpServletRequest request) {

        String authorization = request.getHeader(SecurityConstant.AUTHORIZATION);

        if (authorization == null || BooleanUtil.isFalse(authorization.startsWith(SecurityConstant.JWT_PREFIX))) {
            return null;
        }

        String jwtStr = getJwtStrByHeadAuthorization(authorization);

        if (StrUtil.isBlank(jwtStr)) {
            return null;
        }

        return jwtStr;

    }

    /**
     * 获取：jwtStr
     */
    public static String getJwtStrByHeadAuthorization(@NotNull String authorization) {

        return authorization.replace(SecurityConstant.JWT_PREFIX, "");

    }

    /**
     * 获取用户 jwt私钥后缀，通过 userId
     */
    @Nullable
    public static String getUserJwtSecretSufByUserId(Long userId) {

        if (userId == null || BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        return UserUtil.getJwtSecretSuf(userId);

    }

    /**
     * 通过 userId获取到权限的 set
     */
    @Nullable
    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorityListByUserId(Long userId) {

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 直接抛出异常
            return null;
        }

        // admin账号，自带所有权限
        if (BaseConstant.ADMIN_ID.equals(userId)) {
            return null;
        }

        // 通过用户 id，获取 菜单 set
        Set<SysMenuDO> sysMenuDOSet = UserUtil.getMenuSetByUserId(userId, 2);

        if (CollUtil.isEmpty(sysMenuDOSet)) {
            return null;
        }

        Set<String> authsSet = sysMenuDOSet.stream().map(SysMenuDO::getAuths).collect(Collectors.toSet());

        // 组装权限，并去重
        Set<String> authSet = new HashSet<>();

        for (String item : authsSet) {

            if (StrUtil.isBlank(item)) {
                continue;
            }

            List<String> splitList = StrUtil.split(item, ",");

            for (String auth : splitList) {

                if (StrUtil.isNotBlank(auth)) {
                    authSet.add(auth);
                }

            }

        }

        return authSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

    }

}
