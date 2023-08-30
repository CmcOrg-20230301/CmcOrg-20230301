package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.*;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static SysMenuMapper sysMenuMapper;
    private static SysRoleMapper sysRoleMapper;
    private static SysRoleRefMenuMapper sysRoleRefMenuMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static SysUserMapper sysUserMapper;
    private static SysUserInfoMapper sysUserInfoMapper;

    public UserUtil(SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper, SysRoleRefMenuMapper sysRoleRefMenuMapper,
        SysRoleRefUserMapper sysRoleRefUserMapper, SysUserMapper sysUserMapper, SysUserInfoMapper sysUserInfoMapper) {

        UserUtil.sysMenuMapper = sysMenuMapper;
        UserUtil.sysRoleMapper = sysRoleMapper;
        UserUtil.sysRoleRefMenuMapper = sysRoleRefMenuMapper;
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

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) {
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
     * 这里只会返回实际的 tenantId 或者 0，备注：0表示默认的 租户 id
     */
    @NotNull
    public static Long getCurrentTenantIdDefault() {

        Long userId = getCurrentTenantIdWillNull();

        if (userId == null) {
            userId = BaseConstant.TENANT_ID;
        }

        return userId;

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
    public static Set<SysMenuDO> getMenuSetByUserId(Long userId, int type) {

        Set<Long> roleIdSet = new HashSet<>();

        // 获取：用户关联的角色
        getUserRefRoleIdSet(userId, roleIdSet);

        // 获取：默认角色 id
        getDefaultRoleId(roleIdSet);

        if (roleIdSet.size() == 0) {
            return null;
        }

        RedisKeyEnum redisKeyEnum;
        if (type == 1) {
            redisKeyEnum = RedisKeyEnum.ROLE_ID_REF_MENU_SET_ONE_CACHE;
        } else {
            redisKeyEnum = RedisKeyEnum.ROLE_ID_REF_MENU_SET_TWO_CACHE;
        }

        Set<SysMenuDO> resultSet = new HashSet<>();

        // 获取：角色关联的菜单集合 map
        Map<Long, Set<SysMenuDO>> roleRefMenuSetMap = getRoleRefMenuSetMap(redisKeyEnum);

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
     * 获取：角色关联的菜单集合 map
     */
    @NotNull
    private static Map<Long, Set<SysMenuDO>> getRoleRefMenuSetMap(RedisKeyEnum redisKeyEnum) {

        return MyCacheUtil.getMap(redisKeyEnum, CacheHelper.getDefaultLongSetMap(), () -> {

            // 获取所有：roleIdSet
            Set<Long> allRoleIdSet =
                MyCacheUtil.getCollection(RedisKeyEnum.ROLE_ID_SET_CACHE, CacheHelper.getDefaultSet(), () -> {

                    return ChainWrappers.lambdaQueryChain(sysRoleMapper).select(BaseEntity::getId)
                        .eq(BaseEntityNoId::getEnableFlag, true).list().stream().map(BaseEntity::getId)
                        .collect(Collectors.toSet());

                });

            if (CacheHelper.defaultCollectionFlag(allRoleIdSet)) {
                return null; // 如果：没有角色
            }

            Map<Long, Set<SysMenuDO>> resultMap = MapUtil.newHashMap();

            for (Long item : allRoleIdSet) {

                // 通过：roleId，获取：菜单 set
                Set<SysMenuDO> sysMenuDoSet = doGetMenuSetByRoleId(redisKeyEnum, item);

                resultMap.put(item, sysMenuDoSet); // 添加到：map里面

            }

            return resultMap;

        });

    }

    /**
     * 通过：roleId，获取：菜单 set
     */
    @Nullable
    private static Set<SysMenuDO> doGetMenuSetByRoleId(RedisKeyEnum redisKeyEnum, Long roleId) {

        // 获取：角色关联的菜单 idSet
        Set<Long> menuIdSet = getRoleRefMenuIdSet(roleId);

        if (CollUtil.isEmpty(menuIdSet)) {
            return null;
        }

        // 获取：所有菜单
        List<SysMenuDO> allSysMenuDOList;

        if (RedisKeyEnum.ROLE_ID_REF_MENU_SET_ONE_CACHE.equals(redisKeyEnum)) {

            allSysMenuDOList = getSysMenuCacheMap().values().stream()
                .sorted(Comparator.comparing(BaseEntityTree::getOrderNo, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        } else {

            // 获取：所有菜单：security使用
            allSysMenuDOList = getAllMenuIdAndAuthsList();

        }

        if (CollUtil.isEmpty(allSysMenuDOList)) {
            return null;
        }

        // 通过：menuIdSet，获取：完整的 menuDoSet
        return getFullSysMenuDoSet(menuIdSet, allSysMenuDOList);

    }

    /**
     * 通过：menuIdSet，获取：完整的 menuDoSet
     */
    @Nullable
    public static Set<SysMenuDO> getFullSysMenuDoSet(Set<Long> menuIdSet,
        Collection<SysMenuDO> allSysMenuDoCollection) {

        // 开始进行匹配，组装返回值
        Set<SysMenuDO> resultSet =
            allSysMenuDoCollection.stream().filter(it -> menuIdSet.contains(it.getId())).collect(Collectors.toSet());

        if (resultSet.size() == 0) {
            return null;
        }

        // 已经添加了 menuIdSet
        Set<Long> resultMenuIdSet = resultSet.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        // 通过：parentId分组的 map
        Map<Long, Set<SysMenuDO>> groupMenuParentIdMap = allSysMenuDoCollection.stream().collect(
            Collectors.groupingBy(BaseEntityTree::getParentId, Collectors.mapping(it -> it, Collectors.toSet())));

        // 再添加 menuIdSet下的所有子级菜单
        for (Long item : menuIdSet) {
            getMenuListByUserIdNext(resultSet, item, resultMenuIdSet, groupMenuParentIdMap);
        }

        // 根据底级节点 list，逆向生成整棵树 list
        resultSet = MyTreeUtil.getFullTreeSet(resultSet, allSysMenuDoCollection);

        // 勾选：上级菜单，自动包含全部子级菜单
        // 勾选：子级菜单，自动包含全部上级菜单
        return resultSet;

    }

    /**
     * 再添加 menuIdSet的所有子级菜单
     */
    private static void getMenuListByUserIdNext(Set<SysMenuDO> resultSysMenuDoSet, Long parentId,
        Set<Long> resultMenuIdSet, Map<Long, Set<SysMenuDO>> groupMenuParentIdMap) {

        // 获取：自己下面的子级
        Set<SysMenuDO> sysMenuDoSet = groupMenuParentIdMap.get(parentId);

        if (CollUtil.isEmpty(sysMenuDoSet)) {
            return;
        }

        for (SysMenuDO item : sysMenuDoSet) {

            if (BooleanUtil.isFalse(resultMenuIdSet.contains(item.getId()))) { // 不能重复添加到 返回值里

                resultMenuIdSet.add(item.getId());
                resultSysMenuDoSet.add(item);

            }

            getMenuListByUserIdNext(resultSysMenuDoSet, item.getId(), resultMenuIdSet, groupMenuParentIdMap); // 继续匹配下一级

        }

    }

    /**
     * 获取：菜单缓存数据：map
     */
    @NotNull
    public static Map<Long, SysMenuDO> getSysMenuCacheMap() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        return MyCacheUtil.<Map<Long, Map<Long, SysMenuDO>>>getMap(RedisKeyEnum.TENANT_SYS_MENU_CACHE,
            CacheHelper.getDefaultLongMap(), () -> {

                List<SysMenuDO> sysMenuDOList =
                    ChainWrappers.lambdaQueryChain(sysMenuMapper).eq(BaseEntityNoId::getEnableFlag, true).list();

                return sysMenuDOList.stream().collect(
                    Collectors.groupingBy(BaseEntity::getTenantId, Collectors.toMap(BaseEntity::getId, it -> it)));

            }).get(currentTenantIdDefault);

    }

    /**
     * 获取：所有菜单：security使用
     */
    @Nullable
    private static List<SysMenuDO> getAllMenuIdAndAuthsList() {

        List<SysMenuDO> sysMenuDOList = MyCacheUtil
            .getCollection(RedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, CacheHelper.getDefaultList(), () -> {

                return ChainWrappers.lambdaQueryChain(sysMenuMapper)
                    .select(BaseEntity::getId, BaseEntityTree::getParentId, SysMenuDO::getAuths)
                    .eq(BaseEntity::getEnableFlag, true).list();

            });

        if (CacheHelper.defaultCollectionFlag(sysMenuDOList)) {

            return null;

        } else {

            return sysMenuDOList;

        }

    }

    /**
     * 获取角色关联的菜单 idSet
     */
    @NotNull
    private static Set<Long> getRoleRefMenuIdSet(Long roleId) {

        Map<Long, Set<Long>> roleRefMenuIdSetMap =
            MyCacheUtil.getMap(RedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

                List<SysRoleRefMenuDO> sysRoleRefMenuDOList = ChainWrappers.lambdaQueryChain(sysRoleRefMenuMapper)
                    .select(SysRoleRefMenuDO::getRoleId, SysRoleRefMenuDO::getMenuId).list();

                return sysRoleRefMenuDOList.stream().collect(Collectors.groupingBy(SysRoleRefMenuDO::getRoleId,
                    Collectors.mapping(SysRoleRefMenuDO::getMenuId, Collectors.toSet())));

            });

        return roleRefMenuIdSetMap.get(roleId);

    }

    /**
     * 获取用户关联的角色
     */
    private static void getUserRefRoleIdSet(Long userId, Set<Long> roleIdSet) {

        Map<Long, Set<Long>> userRefRoleIdSetMap =
            MyCacheUtil.getMap(RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

                List<SysRoleRefUserDO> sysRoleRefUserDOList = ChainWrappers.lambdaQueryChain(sysRoleRefUserMapper)
                    .select(SysRoleRefUserDO::getRoleId, SysRoleRefUserDO::getUserId).list();

                return sysRoleRefUserDOList.stream().collect(Collectors.groupingBy(SysRoleRefUserDO::getUserId,
                    Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));

            });

        Set<Long> userRefRoleIdSet = userRefRoleIdSetMap.get(userId);

        if (CollUtil.isNotEmpty(userRefRoleIdSet)) {
            roleIdSet.addAll(userRefRoleIdSet);
        }

    }

    /**
     * 获取默认角色 id
     */
    private static void getDefaultRoleId(Set<Long> roleIdSet) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Map<Long, Long> map =
            MyCacheUtil.getMap(RedisKeyEnum.TENANT_DEFAULT_ROLE_ID_CACHE, CacheHelper.getDefaultLongMap(), () -> {

                List<SysRoleDO> sysRoleDOList =
                    ChainWrappers.lambdaQueryChain(sysRoleMapper).select(BaseEntity::getId, BaseEntityNoId::getTenantId)
                        .eq(BaseEntity::getEnableFlag, true).list();

                return sysRoleDOList.stream().collect(Collectors.toMap(BaseEntityNoId::getTenantId, BaseEntity::getId));

            });

        Long defaultRoleId = map.get(currentTenantIdDefault);

        if (defaultRoleId != null) {

            roleIdSet.add(defaultRoleId);

        }

    }

    /**
     * 统一的：设置：用户 jwt私钥后缀
     */
    public static void setJwtSecretSuf(long userId) {

        CacheRedisKafkaLocalUtil
            .putSecondMap(RedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId),
                IdUtil.simpleUUID(), null);

    }

    /**
     * 统一的：获取：用户 jwt私钥后缀
     */
    @NotNull
    public static String getJwtSecretSuf(long userId) {

        return MyCacheUtil.getSecondMap(RedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId),
            IdUtil.simpleUUID(), null);

    }

    /**
     * 统一的：删除：用户 jwt私钥后缀
     */
    public static void removeJwtSecretSuf(long userId) {

        CacheRedisKafkaLocalUtil
            .removeSecondMap(RedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId));

    }

    /**
     * 统一的：设置：用户冻结
     */
    public static void setDisable(long userId) {

        CacheRedisKafkaLocalUtil
            .putSecondMap(RedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId), true, null);

    }

    /**
     * 统一的：获取：用户冻结
     */
    public static boolean getDisable(long userId) {

        Boolean disableFlag =
            MyCacheUtil.onlyGetSecondMap(RedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId));

        return BooleanUtil.isTrue(disableFlag);

    }

    /**
     * 统一的：删除：用户冻结
     */
    public static void removeDisable(long userId) {

        CacheRedisKafkaLocalUtil.removeSecondMap(RedisKeyEnum.SYS_USER_DISABLE_CACHE, null, String.valueOf(userId));

    }

}
