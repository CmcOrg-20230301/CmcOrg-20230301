package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleRefUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static SysRoleMapper sysRoleMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static SysUserMapper sysUserMapper;
    private static SysUserInfoMapper sysUserInfoMapper;

    public UserUtil(SysRoleMapper sysRoleMapper, SysRoleRefUserMapper sysRoleRefUserMapper, SysUserMapper sysUserMapper,
        SysUserInfoMapper sysUserInfoMapper) {

        UserUtil.sysRoleMapper = sysRoleMapper;
        UserUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;
        UserUtil.sysUserMapper = sysUserMapper;
        UserUtil.sysUserInfoMapper = sysUserInfoMapper;

    }

    /**
     * 获取当前 userId
     * 这里只会返回实际的 userId，如果为 null，则会抛出异常
     */
    @NotNull
    public static Long getCurrentUserId() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.NOT_LOGGED_IN_YET);
        }

        return userId;

    }

    /**
     * 获取当前用户的昵称
     * 这里只会返回实际的昵称，如果为 null，则会抛出异常
     */
    @NotNull
    public static String getCurrentUserNickName() {

        Long userId = getCurrentUserId();

        SysUserInfoDO sysUserInfoDO = ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, userId)
            .select(SysUserInfoDO::getNickname).one();

        if (sysUserInfoDO == null) {
            return "";
        }

        return sysUserInfoDO.getNickname();

    }

    /**
     * 获取当前用户的 邮箱，如果是 admin账号，则会报错，只会返回当前用户的 邮箱，不会返回 null
     */
    @NotNull
    public static String getCurrentUserEmailNotAdmin() {

        Long currentUserIdNotAdmin = getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getEmail).one();

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getEmail())) {
            ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND);
        }

        return sysUserDO.getEmail();

    }

    /**
     * 获取当前用户的 手机号码，如果是 admin账号，则会报错，只会返回当前用户的 手机号码，不会返回 null
     */
    @NotNull
    public static String getCurrentUserPhoneNotAdmin() {

        Long currentUserIdNotAdmin = getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getPhone).one();

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getPhone())) {
            ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_PHONE_IS_NOT_BOUND);
        }

        return sysUserDO.getPhone();

    }

    /**
     * 获取当前 userId，如果是 admin账号，则会报错，只会返回 用户id，不会返回 null
     * 因为 admin不支持一些操作，例如：修改密码，修改邮箱等
     */
    @NotNull
    public static Long getCurrentUserIdNotAdmin() {

        Long currentUserId = getCurrentUserId();

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {
            ApiResultVO.error(BaseBizCodeEnum.THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION);
        }

        return currentUserId;

    }

    /**
     * 这里只会返回实际的 userId 或者 -1，备注：-1表示没有 用户id，在大多数情况下，表示的是 系统
     */
    @NotNull
    public static Long getCurrentUserIdDefault() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            userId = BaseConstant.SYS_ID;
        }

        return userId;

    }

    /**
     * 这里只会返回实际的 wxOpenId 或者 空字符串
     */
    @NotNull
    public static String getCurrentUserWxOpenIdDefault() {

        String currentUserWxOpenIdWillNull = getCurrentUserWxOpenIdWillNull();

        if (currentUserWxOpenIdWillNull == null) {
            currentUserWxOpenIdWillNull = "";
        }

        return currentUserWxOpenIdWillNull;

    }

    /**
     * 用户是否是系统管理员
     */
    public static boolean getCurrentUserAdminFlag() {

        return BaseConstant.ADMIN_ID.equals(getCurrentUserIdDefault());

    }

    /**
     * 用户是否是系统管理员
     */
    public static boolean getCurrentUserAdminFlag(Long userId) {

        return BaseConstant.ADMIN_ID.equals(userId);

    }

    /**
     * 这里只会返回实际的 tenantId 或者 0，备注：0表示默认的 租户 id
     */
    @NotNull
    public static Long getCurrentTenantIdDefault() {

        Long userId = getCurrentTenantIdWillNull();

        if (userId == null) {
            userId = BaseConstant.TOP_TENANT_ID;
        }

        return userId;

    }

    /**
     * 获取当前 wxOpenId，注意：这里获取 wxOpenId之后需要做 非空判断
     * 这里只会返回实际的 wxOpenId或者 null
     */
    @Nullable
    private static String getCurrentUserWxOpenIdWillNull() {

        return MyJwtUtil.getPayloadMapWxOpenIdValue(getSecurityContextHolderContextAuthenticationPrincipalJsonObject());

    }

    /**
     * 获取当前 userId，注意：这里获取 userId之后需要做 非空判断
     * 这里只会返回实际的 userId或者 null
     */
    @Nullable
    private static Long getCurrentUserIdWillNull() {

        return MyJwtUtil.getPayloadMapUserIdValue(getSecurityContextHolderContextAuthenticationPrincipalJsonObject());

    }

    /**
     * 获取当前 tenantId，注意：这里获取 tenantId之后需要做 非空判断
     * 这里只会返回实际的 tenantId或者 null
     */
    @Nullable
    private static Long getCurrentTenantIdWillNull() {

        return MyJwtUtil.getPayloadMapTenantIdValue(getSecurityContextHolderContextAuthenticationPrincipalJsonObject());

    }

    /**
     * 获取：当前 security上下文里面存储的用户信息
     */
    @Nullable
    public static JSONObject getSecurityContextHolderContextAuthenticationPrincipalJsonObject() {

        JSONObject result = null;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {

            Object principalObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principalObject instanceof JSONObject) {
                result = (JSONObject)principalObject;
            }

        }

        return result;

    }

    /**
     * 获取：当前 security上下文里面存储的用户信息，通过：key
     */
    @Nullable
    public static <T> T getSecurityContextHolderContextAuthenticationPrincipalJsonObjectValueByKey(String key) {

        JSONObject principalJSONObject = getSecurityContextHolderContextAuthenticationPrincipalJsonObject();

        if (principalJSONObject == null) {
            return null;
        }

        return (T)principalJSONObject.get(key);

    }

    /**
     * 通过用户 id，获取 菜单 set
     * type：1 完整的菜单信息 2 给 security获取权限时使用
     */
    @Nullable
    public static Set<SysMenuDO> getMenuSetByUserId(@NotNull Long userId, int type, @NotNull Long tenantId) {

        Set<Long> roleIdSet = new HashSet<>();

        // 获取：用户关联的角色
        getUserRefRoleIdSet(userId, roleIdSet);

        // 获取：默认角色 id
        getDefaultRoleId(roleIdSet, tenantId);

        if (roleIdSet.size() == 0) {
            return null;
        }

        BaseRedisKeyEnum baseRedisKeyEnum;

        if (type == 1) {
            baseRedisKeyEnum = BaseRedisKeyEnum.ROLE_ID_REF_FULL_MENU_SET_CACHE;
        } else {
            baseRedisKeyEnum = BaseRedisKeyEnum.ROLE_ID_REF_SECURITY_MENU_SET_CACHE;
        }

        Set<SysMenuDO> resultSet = new HashSet<>();

        // 获取：角色关联的菜单集合 map
        Map<Long, Set<SysMenuDO>> roleRefMenuSetMap = SysMenuUtil.getRoleRefMenuSetMap(baseRedisKeyEnum);

        for (Long item : roleIdSet) {

            // 获取：角色关联的菜单集合
            Set<SysMenuDO> roleRefMenuSet = roleRefMenuSetMap.get(item);

            // 如果：关联的菜单集合为空
            if (CollUtil.isEmpty(roleRefMenuSet)) {
                continue;
            }

            if (CollUtil.isNotEmpty(roleRefMenuSet)) {

                resultSet.addAll(roleRefMenuSet); // 添加到：返回值里

            }

        }

        return resultSet;

    }

    /**
     * 获取用户关联的角色
     */
    private static void getUserRefRoleIdSet(Long userId, Set<Long> roleIdSet) {

        Map<Long, Set<Long>> userRefRoleIdSetMap = getUserRefRoleIdSetMap();

        Set<Long> userRefRoleIdSet = userRefRoleIdSetMap.get(userId);

        if (CollUtil.isNotEmpty(userRefRoleIdSet)) {
            roleIdSet.addAll(userRefRoleIdSet);
        }

    }

    /**
     * 获取：用户 id关联的 roleIdSet
     */
    @Unmodifiable // 不可对返回值进行修改
    public static Map<Long, Set<Long>> getUserRefRoleIdSetMap() {

        return MyCacheUtil
            .getMap(BaseRedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

                List<SysRoleRefUserDO> sysRoleRefUserDOList = ChainWrappers.lambdaQueryChain(sysRoleRefUserMapper)
                    .select(SysRoleRefUserDO::getRoleId, SysRoleRefUserDO::getUserId).list();

                return sysRoleRefUserDOList.stream().collect(Collectors.groupingBy(SysRoleRefUserDO::getUserId,
                    Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));

            });

    }

    /**
     * 获取默认角色 id
     */
    private static void getDefaultRoleId(Set<Long> roleIdSet, @NotNull Long tenantId) {

        Map<Long, Long> map = MyCacheUtil
            .getMap(BaseRedisKeyEnum.TENANT_DEFAULT_ROLE_ID_CACHE, CacheHelper.getDefaultLongMap(BaseConstant.SYS_ID),
                () -> {

                    List<SysRoleDO> sysRoleDOList = ChainWrappers.lambdaQueryChain(sysRoleMapper)
                        .select(BaseEntity::getId, BaseEntityNoId::getTenantId).eq(BaseEntity::getEnableFlag, true)
                        .eq(SysRoleDO::getDefaultFlag, true).list();

                    return sysRoleDOList.stream()
                        .collect(Collectors.toMap(BaseEntityNoId::getTenantId, BaseEntity::getId));

                });

        Long defaultRoleId = map.get(tenantId);

        if (defaultRoleId != null) {

            roleIdSet.add(defaultRoleId);

        }

    }

    /**
     * 统一的：设置：用户 jwt私钥后缀
     */
    public static void setJwtSecretSuf(long userId) {

        CacheRedisKafkaLocalUtil
            .putSecondMap(BaseRedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId),
                IdUtil.simpleUUID(), null);

    }

    /**
     * 统一的：获取：用户 jwt私钥后缀
     */
    @NotNull
    public static String getJwtSecretSuf(long userId) {

        return MyCacheUtil.getSecondMap(BaseRedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId),
            IdUtil.simpleUUID(), null);

    }

    /**
     * 统一的：删除：用户 jwt私钥后缀
     */
    public static void removeJwtSecretSuf(long userId) {

        CacheRedisKafkaLocalUtil
            .removeSecondMap(BaseRedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId));

    }

    /**
     * 统一的：设置：用户冻结
     */
    public static void setDisable(long userId) {

        CacheRedisKafkaLocalUtil
            .putSecondMap(BaseRedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId), true, null);

    }

    /**
     * 统一的：获取：用户冻结
     */
    public static boolean getDisable(long userId) {

        Boolean disableFlag =
            MyCacheUtil.onlyGetSecondMap(BaseRedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId));

        return BooleanUtil.isTrue(disableFlag);

    }

    /**
     * 统一的：删除：用户冻结
     */
    public static void removeDisable(long userId) {

        CacheRedisKafkaLocalUtil.removeSecondMap(BaseRedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId));

    }

}
