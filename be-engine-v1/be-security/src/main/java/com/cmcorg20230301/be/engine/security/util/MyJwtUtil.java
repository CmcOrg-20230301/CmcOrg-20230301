package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.RegisteredPayload;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.constant.SecurityConstant;
import com.cmcorg20230301.be.engine.security.model.entity.SysMenuDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.util.util.CallBack;
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

    public static final String PAYLOAD_MAP_TENANT_ID_KEY = "tenantId";

    private static SecurityProperties securityProperties;

    public MyJwtUtil(SecurityProperties securityProperties) {

        MyJwtUtil.securityProperties = securityProperties;

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
     * 获取：jwt中的 tenantId值
     */
    @Nullable
    public static Long getPayloadMapTenantIdValue(@Nullable JSONObject claimsJson) {

        if (claimsJson == null) {
            return null;
        }

        NumberWithFormat numberWithFormat = (NumberWithFormat)claimsJson.get(MyJwtUtil.PAYLOAD_MAP_TENANT_ID_KEY);

        if (numberWithFormat == null) {
            return null;
        }

        return numberWithFormat.longValue();

    }

    /**
     * 统一生成 jwt
     */
    @Nullable
    public static String generateJwt(Long userId, String jwtSecretSuf, Consumer<JSONObject> consumer,
        @Nullable Long tenantId) {

        if (userId == null) {
            return null;
        }

        if (UserUtil.getCurrentUserAdminFlag(userId) && BooleanUtil
            .isFalse(MyJwtUtil.securityProperties.getAdminEnable())) {
            return null;
        }

        if (StrUtil.isBlank(jwtSecretSuf)) {

            // 获取用户 jwt私钥后缀，通过 userId
            jwtSecretSuf = MyJwtUtil.getUserJwtSecretSufByUserId(userId);

        }

        if (BooleanUtil.isFalse(UserUtil.getCurrentUserAdminFlag(userId)) && StrUtil.isBlank(jwtSecretSuf)) {
            return null;
        }

        RedissonUtil.batch((batch) -> {

            // 移除密码错误次数相关
            batch.getBucket(BaseRedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + userId).deleteAsync();
            batch.getMap(BaseRedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).removeAsync(userId);

        });

        // 生成 jwt
        return MyJwtUtil.sign(userId, jwtSecretSuf, consumer, tenantId);

    }

    /**
     * 生成 jwt
     */
    @NotNull
    private static String sign(Long userId, String jwtSecretSuf, Consumer<JSONObject> consumer,
        @Nullable Long tenantId) {

        JSONObject payloadMap = JSONUtil.createObj();

        payloadMap.set(PAYLOAD_MAP_USER_ID_KEY, userId);

        // 获取：租户 id
        tenantId = SysTenantUtil.getTenantId(tenantId);

        payloadMap.set(PAYLOAD_MAP_TENANT_ID_KEY, tenantId);

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

        strBuilder.append(BaseRedisKeyEnum.PRE_JWT_HASH.name()).append(":").append(userId).append(":")
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

        if (userId == null || UserUtil.getCurrentUserAdminFlag(userId)) {
            return null;
        }

        return UserUtil.getJwtSecretSuf(userId);

    }

    /**
     * 通过 userId获取到权限的 set
     */
    @Nullable
    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorityListByUserId(Long userId, Long tenantId) {

        if (userId == null || tenantId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 直接抛出异常
            return null;
        }

        // admin账号，自带所有权限
        if (UserUtil.getCurrentUserAdminFlag(userId)) {
            return null;
        }

        // 通过用户 id，获取 菜单 set
        Set<SysMenuDO> sysMenuDoSet = UserUtil.getMenuSetByUserId(userId, 2, tenantId);

        if (CollUtil.isEmpty(sysMenuDoSet)) {
            return null;
        }

        Set<String> authsSet = sysMenuDoSet.stream().map(SysMenuDO::getAuths).collect(Collectors.toSet());

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

    /**
     * 获取：请求里面的 jwtHash值
     */
    public static String getJwtHashByRequest(HttpServletRequest httpServletRequest,
        @Nullable CallBack<Long> jwtHashRemainMsCallBack, @Nullable CallBack<Long> expireTsCallBack) {

        // 从请求头里，获取：jwt字符串
        String jwtStr = MyJwtUtil.getJwtStrByRequest(httpServletRequest);

        if (jwtStr == null) {
            return null;
        }

        JWT jwt = JWT.of(jwtStr); // 备注：这里不会报错

        JSONObject claimsJson = jwt.getPayload().getClaimsJson();

        Date expiresDate = claimsJson.getDate(RegisteredPayload.EXPIRES_AT);

        if (expiresDate == null) { // 备注：这里不会为 null
            return null;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        String jwtHash = MyJwtUtil
            .generateRedisJwtHash(jwtStr, currentUserId, RequestUtil.getRequestCategoryEnum(httpServletRequest));

        // jwt剩余时间
        long remainMs = expiresDate.getTime() - System.currentTimeMillis();

        if (jwtHashRemainMsCallBack != null) {

            jwtHashRemainMsCallBack.setValue(remainMs);

        }

        if (expireTsCallBack != null) {

            expireTsCallBack.setValue(expiresDate.getTime());

        }

        return jwtHash;

    }

}
