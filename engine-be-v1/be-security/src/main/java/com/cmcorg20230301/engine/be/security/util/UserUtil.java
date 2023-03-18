package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.cache.util.CacheHelper;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.*;
import com.cmcorg20230301.engine.be.security.model.entity.*;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static SysMenuMapper sysMenuMapper;
    private static SysRoleMapper sysRoleMapper;
    private static SysRoleRefMenuMapper sysRoleRefMenuMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static SysUserMapper sysUserMapper;

    public UserUtil(SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper, SysRoleRefMenuMapper sysRoleRefMenuMapper,
        SysRoleRefUserMapper sysRoleRefUserMapper, SysUserMapper sysUserMapper) {

        UserUtil.sysMenuMapper = sysMenuMapper;
        UserUtil.sysRoleMapper = sysRoleMapper;
        UserUtil.sysRoleRefMenuMapper = sysRoleRefMenuMapper;
        UserUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;
        UserUtil.sysUserMapper = sysUserMapper;

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
            ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND);
        }

        return sysUserDO.getEmail();

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
     * 获取当前 userId，注意：这里获取 userId之后需要做 非空判断
     * 这里只会返回实际的 userId或者 null
     */
    @Nullable
    private static Long getCurrentUserIdWillNull() {

        return MyJwtUtil.getPayloadMapUserIdValue(getSecurityContextHolderContextAuthenticationPrincipalJsonObject());

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

        for (Long item : roleIdSet) {

            // 获取：角色关联的菜单集合 map
            Map<Long, Set<SysMenuDO>> roleRefMenuSetMap = getRoleRefMenuSetMap(type, redisKeyEnum, item);

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
    private static Map<Long, Set<SysMenuDO>> getRoleRefMenuSetMap(int type, RedisKeyEnum redisKeyEnum, Long item) {

        return MyCacheUtil.get(redisKeyEnum, CacheHelper.getDefaultLongSetMap(), () -> {

            // 获取所有：roleIdSet
            Set<Long> allRoleIdSet =
                MyCacheUtil.get(RedisKeyEnum.ROLE_ID_SET_CACHE, CacheHelper.getDefaultSet(), () -> {

                    return ChainWrappers.lambdaQueryChain(sysRoleMapper).select(BaseEntity::getId)
                        .eq(BaseEntityNoId::getEnableFlag, true).list().stream().map(BaseEntity::getId)
                        .collect(Collectors.toSet());

                });

            if (CacheHelper.defaultCollectionFlag(allRoleIdSet)) {
                return null; // 如果：没有角色
            }

            Map<Long, Set<SysMenuDO>> resultMap = MapUtil.newHashMap();

            for (Long subItem : allRoleIdSet) {

                // 通过：roleId，获取：菜单 set
                Set<SysMenuDO> sysMenuDOSet = doGetMenuSetByRoleId(type, item);

                resultMap.put(subItem, sysMenuDOSet); // 添加到：map里面

            }

            return resultMap;

        });

    }

    /**
     * 通过：roleId，获取：菜单 set
     */
    @Nullable
    private static Set<SysMenuDO> doGetMenuSetByRoleId(int type, Long roleId) {

        // 获取：角色关联的菜单 idSet
        Set<Long> menuIdSet = getRoleRefMenuIdSet(roleId);

        if (CollUtil.isEmpty(menuIdSet)) {
            return null;
        }

        // 获取：所有菜单
        List<SysMenuDO> allSysMenuDOList;
        if (type == 1) {

            allSysMenuDOList = ChainWrappers.lambdaQueryChain(sysMenuMapper)
                .select(BaseEntity::getId, BaseEntityTree::getParentId, SysMenuDO::getPath, SysMenuDO::getIcon,
                    SysMenuDO::getRouter, SysMenuDO::getName, SysMenuDO::getFirstFlag, SysMenuDO::getLinkFlag,
                    SysMenuDO::getShowFlag, SysMenuDO::getAuths, SysMenuDO::getAuthFlag, BaseEntityTree::getOrderNo,
                    SysMenuDO::getRedirect).eq(BaseEntity::getEnableFlag, true).orderByDesc(BaseEntityTree::getOrderNo)
                .list();

        } else {

            // 获取：所有菜单：security使用
            allSysMenuDOList = getAllMenuIdAndAuthsList();

        }

        if (CollUtil.isEmpty(allSysMenuDOList)) {
            return null;
        }

        // 开始进行匹配，组装返回值
        Set<SysMenuDO> resultSysMenuDOSet =
            allSysMenuDOList.stream().filter(it -> menuIdSet.contains(it.getId())).collect(Collectors.toSet());

        if (resultSysMenuDOSet.size() == 0) {
            return null;
        }

        // 已经添加了 menuIdSet
        Set<Long> addMenuIdSet = resultSysMenuDOSet.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        // 通过：parentId分组的 map
        Map<Long, Set<SysMenuDO>> groupMenuParentIdMap = allSysMenuDOList.stream().collect(
            Collectors.groupingBy(BaseEntityTree::getParentId, Collectors.mapping(it -> it, Collectors.toSet())));

        // 再添加 menuIdSet下的所有子级菜单
        for (Long item : menuIdSet) {
            getMenuListByUserIdNext(resultSysMenuDOSet, item, addMenuIdSet, groupMenuParentIdMap);
        }

        return resultSysMenuDOSet;

    }

    /**
     * 再添加 menuIdSet的所有子级菜单
     */
    private static void getMenuListByUserIdNext(Set<SysMenuDO> resultSysMenuDOSet, Long parentId,
        Set<Long> addMenuIdSet, Map<Long, Set<SysMenuDO>> groupMenuParentIdMap) {

        // 获取：自己下面的子级
        Set<SysMenuDO> sysMenuDOSet = groupMenuParentIdMap.get(parentId);

        if (CollUtil.isEmpty(sysMenuDOSet)) {
            return;
        }

        for (SysMenuDO item : sysMenuDOSet) {

            if (BooleanUtil.isFalse(addMenuIdSet.contains(item.getId()))) { // 不能重复添加到 返回值里
                addMenuIdSet.add(item.getId());
                resultSysMenuDOSet.add(item);
            }

            getMenuListByUserIdNext(resultSysMenuDOSet, item.getId(), addMenuIdSet, groupMenuParentIdMap); // 继续匹配下一级

        }

    }

    /**
     * 获取：所有菜单：security使用
     */
    @Nullable
    private static List<SysMenuDO> getAllMenuIdAndAuthsList() {

        List<SysMenuDO> sysMenuDOList =
            MyCacheUtil.get(RedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, CacheHelper.getDefaultList(), () -> {

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
            MyCacheUtil.get(RedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

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
            MyCacheUtil.get(RedisKeyEnum.USER_ID_REF_ROLE_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

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

        Long defaultRoleId = MyCacheUtil.get(RedisKeyEnum.DEFAULT_ROLE_ID_CACHE, BaseConstant.SYS_ID, () -> {

            SysRoleDO sysRoleDO = ChainWrappers.lambdaQueryChain(sysRoleMapper).eq(SysRoleDO::getDefaultFlag, true)
                .eq(BaseEntity::getEnableFlag, true).select(BaseEntity::getId).one();

            if (sysRoleDO != null) {
                return sysRoleDO.getId();
            }

            return null;

        });

        if (BooleanUtil.isFalse(BaseConstant.SYS_ID.equals(defaultRoleId))) {
            roleIdSet.add(defaultRoleId);
        }

    }

    /**
     * 统一的：设置：用户 jwt私钥后缀
     */
    public static void setJwtSecretSuf(long userId) {

        CacheRedisKafkaLocalUtil
            .put(RedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId), IdUtil.simpleUUID(),
                null);

    }

    /**
     * 统一的：获取：用户 jwt私钥后缀
     */
    @NotNull
    public static String getJwtSecretSuf(long userId) {

        return MyCacheUtil
            .get(RedisKeyEnum.USER_ID_AND_JWT_SECRET_SUF_CACHE, null, String.valueOf(userId), IdUtil.simpleUUID(),
                null);

    }

}
