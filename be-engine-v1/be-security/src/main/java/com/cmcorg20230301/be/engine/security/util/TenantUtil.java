package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 租户相关的工具类
 */
@Component
public class TenantUtil {

    private static SysTenantMapper sysTenantMapper;
    private static SysTenantRefUserMapper sysTenantRefUserMapper;

    public TenantUtil(SysTenantMapper sysTenantMapper, SysTenantRefUserMapper sysTenantRefUserMapper) {

        TenantUtil.sysTenantMapper = sysTenantMapper;
        TenantUtil.sysTenantRefUserMapper = sysTenantRefUserMapper;

    }

    /**
     * 获取：租户 id
     */
    @NotNull
    public static Long getTenantId(@Nullable Long tenantId) {

        if (tenantId == null || tenantId.equals(BaseConstant.TENANT_ID)) {

            return BaseConstant.TENANT_ID;

        }

        Map<Long, SysTenantDO> map = getSysTenantCacheMap();

        SysTenantDO sysTenantDO = map.get(tenantId);

        if (sysTenantDO == null) {

            ApiResultVO.error("操作失败：租户不存在", tenantId);

        } else if (sysTenantDO.getEnableFlag() == false) {

            ApiResultVO.error("操作失败：租户已被禁用", tenantId);

        }

        return tenantId;

    }

    /**
     * 获取：租户缓存数据：map
     * 备注：这里不包含：默认租户
     */
    @NotNull
    public static Map<Long, SysTenantDO> getSysTenantCacheMap() {

        return MyCacheUtil.getMap(RedisKeyEnum.SYS_TENANT_CACHE, CacheHelper.getDefaultLongMap(), () -> {

            List<SysTenantDO> sysTenantDOList = ChainWrappers.lambdaQueryChain(sysTenantMapper)
                .select(BaseEntity::getId, SysTenantDO::getName, BaseEntityNoId::getEnableFlag,
                    SysTenantDO::getParentId).list();

            return sysTenantDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        });

    }

    /**
     * 获取：用户关联的租户
     * 备注：即表示：用户可以查看关联租户的信息
     */
    private static Set<Long> getUserRefTenantIdSet() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Set<Long> resultSet = new HashSet<>();

        resultSet.add(currentTenantIdDefault); // 添加：默认的租户 id

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) {

            CollUtil.addAll(resultSet, getSysTenantCacheMap().keySet()); // 添加：所有的 租户 id

        } else {

            // 获取：用户 id关联的 tenantIdSet，map
            Map<Long, Set<Long>> userIdRefTenantIdSetMap = getUserIdRefTenantIdSetMap();

            Set<Long> tenantIdSet = userIdRefTenantIdSetMap.get(currentUserId);

            CollUtil.addAll(resultSet, tenantIdSet);

            // 获取：下级租户
            for (Long item : resultSet) {

                CollUtil.addAll(resultSet, getTenantDeepIdSet(item));

            }

        }

        return resultSet;

    }

    /**
     * 通过：租户 id，获取：关联的所有的 子租户（包含本租户）
     */
    private static Set<Long> getTenantDeepIdSet(Long tenantId) {

        return MyCacheUtil
            .<Map<Long, Set<Long>>>getMap(RedisKeyEnum.SYS_TENANT_DEEP_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(),
                () -> {

                    Map<Long, SysTenantDO> sysTenantCacheMap = getSysTenantCacheMap();

                    // 通过：父级 id分组，value：子级 idSet
                    Map<Long, Set<Long>> groupParentIdMap = sysTenantCacheMap.values().stream().collect(Collectors
                        .groupingBy(BaseEntityTree::getParentId,
                            Collectors.mapping(BaseEntity::getId, Collectors.toSet())));

                    Map<Long, Set<Long>> resultMap = new HashMap<>(sysTenantCacheMap.size());

                    for (Long item : sysTenantCacheMap.keySet()) {

                        Set<Long> resultSet = new HashSet<>();

                        getUserRefTenantIdListNext(resultSet, item, groupParentIdMap);

                        resultMap.put(item, resultSet);

                    }

                    return resultMap;

                }).get(tenantId);

    }

    /**
     * 获取：下级租户
     */
    private static void getUserRefTenantIdListNext(Set<Long> resultSet, Long parentId,
        Map<Long, Set<Long>> groupParentIdMap) {

        // 获取：自己下面的子级
        Set<Long> childrenIdSet = groupParentIdMap.get(parentId);

        if (CollUtil.isEmpty(childrenIdSet)) {
            return;
        }

        for (Long item : childrenIdSet) {

            resultSet.add(item);

            getUserRefTenantIdListNext(resultSet, item, groupParentIdMap); // 继续匹配下一级

        }

    }

    /**
     * 获取：用户 id关联的 tenantIdSet，map
     */
    private static Map<Long, Set<Long>> getUserIdRefTenantIdSetMap() {

        return MyCacheUtil
            .getMap(RedisKeyEnum.USER_ID_REF_TENANT_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

                List<SysTenantRefUserDO> sysTenantRefUserDOList = ChainWrappers.lambdaQueryChain(sysTenantRefUserMapper)
                    .select(SysTenantRefUserDO::getTenantId, SysTenantRefUserDO::getUserId).list();

                return sysTenantRefUserDOList.stream().collect(Collectors.groupingBy(SysTenantRefUserDO::getUserId,
                    Collectors.mapping(SysTenantRefUserDO::getTenantId, Collectors.toSet())));

            });

    }

    /**
     * 通过：dto的 tenantId，获取：tenantIdSet
     *
     * @param tenantId 传 null，就返回用户关联所有的 tenantId
     */
    @NotNull
    public static Set<Long> getTenantIdSetByDtoTenantId(@Nullable Long tenantId) {

        Set<Long> tenantIdSet = CollUtil.newHashSet();

        Set<Long> userRefTenantIdSet = TenantUtil.getUserRefTenantIdSet();

        if (userRefTenantIdSet.contains(tenantId) == false) {

            tenantIdSet = userRefTenantIdSet;

        } else {

            tenantIdSet.add(tenantId);

        }

        return tenantIdSet;

    }

}
