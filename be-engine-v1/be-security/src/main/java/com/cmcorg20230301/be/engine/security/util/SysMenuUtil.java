package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysMenuMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleRefMenuMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SysMenuUtil {

    private static SysMenuMapper sysMenuMapper;
    private static SysRoleMapper sysRoleMapper;
    private static SysRoleRefMenuMapper sysRoleRefMenuMapper;

    public SysMenuUtil(SysMenuMapper sysMenuMapper, SysRoleMapper sysRoleMapper,
                       SysRoleRefMenuMapper sysRoleRefMenuMapper) {

        SysMenuUtil.sysMenuMapper = sysMenuMapper;
        SysMenuUtil.sysRoleMapper = sysRoleMapper;
        SysMenuUtil.sysRoleRefMenuMapper = sysRoleRefMenuMapper;

    }

    /**
     * 获取：菜单缓存数据：map
     */
    @NotNull @Unmodifiable // 不可对返回值进行修改
    public static Map<Long, SysMenuDO> getSysMenuCacheMap() {

        Map<Long, SysMenuDO> map =
                MyCacheUtil.getMap(BaseRedisKeyEnum.SYS_MENU_CACHE, CacheHelper.getDefaultLongMap(new SysMenuDO()), () -> {

                    List<SysMenuDO> sysMenuDOList =
                            ChainWrappers.lambdaQueryChain(sysMenuMapper).eq(BaseEntityNoId::getEnableFlag, true).list();

                    return sysMenuDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> {

                        it.setCreateId(null);
                        it.setCreateTime(null);

                        it.setUpdateId(null);
                        it.setUpdateTime(null);

                        it.setVersion(null);

                        return it;

                    }));

                });

        // 移除：默认值
        map = CacheHelper.handleDefaultLongMap(map);

        return map;

    }

    /**
     * 获取：所有菜单：security使用
     */
    @Nullable @Unmodifiable // 不可对返回值进行修改
    private static List<SysMenuDO> getAllMenuIdAndAuthsList() {

        List<SysMenuDO> sysMenuDOList = MyCacheUtil
                .getCollection(BaseRedisKeyEnum.ALL_MENU_ID_AND_AUTHS_LIST_CACHE, CacheHelper.getDefaultList(), () -> {

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
     * 获取：角色关联的菜单集合 map
     */
    @NotNull @Unmodifiable // 不可对返回值进行修改
    public static Map<Long, Set<SysMenuDO>> getRoleRefMenuSetMap(BaseRedisKeyEnum baseRedisKeyEnum) {

        return MyCacheUtil.getMap(baseRedisKeyEnum, CacheHelper.getDefaultLongSetMap(), () -> {

            // 获取所有：roleIdSet
            Set<Long> allRoleIdSet =
                    MyCacheUtil.getCollection(BaseRedisKeyEnum.ROLE_ID_SET_CACHE, CacheHelper.getDefaultSet(), () -> {

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
                Set<SysMenuDO> sysMenuDoSet = doGetMenuSetByRoleId(baseRedisKeyEnum, item);

                resultMap.put(item, sysMenuDoSet); // 添加到：map里面

            }

            return resultMap;

        });

    }

    /**
     * 获取角色关联的菜单 idSet
     */
    @Nullable @Unmodifiable // 不可对返回值进行修改
    private static Set<Long> getRoleRefMenuIdSet(Long roleId) {

        Map<Long, Set<Long>> roleRefMenuIdSetMap = MyCacheUtil
                .getMap(BaseRedisKeyEnum.ROLE_ID_REF_MENU_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

                    List<SysRoleRefMenuDO> sysRoleRefMenuDOList = ChainWrappers.lambdaQueryChain(sysRoleRefMenuMapper)
                            .select(SysRoleRefMenuDO::getRoleId, SysRoleRefMenuDO::getMenuId).list();

                    return sysRoleRefMenuDOList.stream().collect(Collectors.groupingBy(SysRoleRefMenuDO::getRoleId,
                            Collectors.mapping(SysRoleRefMenuDO::getMenuId, Collectors.toSet())));

                });

        return roleRefMenuIdSetMap.get(roleId);

    }

    /**
     * 通过：roleId，获取：菜单 set
     */
    @NotNull
    private static Set<SysMenuDO> doGetMenuSetByRoleId(BaseRedisKeyEnum baseRedisKeyEnum, Long roleId) {

        // 获取：角色关联的菜单 idSet
        Set<Long> menuIdSet = getRoleRefMenuIdSet(roleId);

        if (CollUtil.isEmpty(menuIdSet)) {
            return new HashSet<>();
        }

        // 获取：所有菜单
        List<SysMenuDO> allSysMenuDOList;

        if (BaseRedisKeyEnum.ROLE_ID_REF_FULL_MENU_SET_CACHE.equals(baseRedisKeyEnum)) {

            allSysMenuDOList = SysMenuUtil.getSysMenuCacheMap().values().stream()
                    .sorted(Comparator.comparing(BaseEntityTree::getOrderNo, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

        } else {

            // 获取：所有菜单：security使用
            allSysMenuDOList = getAllMenuIdAndAuthsList();

        }

        if (CollUtil.isEmpty(allSysMenuDOList)) {
            return new HashSet<>();
        }

        // 通过：menuIdSet，获取：完整的 menuDoSet
        return getFullSysMenuDoSet(menuIdSet, allSysMenuDOList);

    }

    /**
     * 通过：menuIdSet，获取：完整的 menuDoSet
     * 注意：由于这里是从：公用的缓存里面获取的值，所以这里的返回值，不要随意改动
     */
    @NotNull
    public static Set<SysMenuDO> getFullSysMenuDoSet(Set<Long> menuIdSet,
                                                     Collection<SysMenuDO> allSysMenuDoCollection) {

        // 开始进行匹配，组装返回值
        Set<SysMenuDO> resultSet =
                allSysMenuDoCollection.stream().filter(it -> menuIdSet.contains(it.getId())).collect(Collectors.toSet());

        if (resultSet.size() == 0) {
            return new HashSet<>();
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

}
