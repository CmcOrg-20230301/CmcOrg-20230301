package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantRefUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     */
    @NotNull
    public static Map<Long, SysTenantDO> getSysTenantCacheMap() {

        return MyCacheUtil.getMap(RedisKeyEnum.SYS_TENANT_CACHE, CacheHelper.getDefaultLongMap(), () -> {

            List<SysTenantDO> sysTenantDOList = ChainWrappers.lambdaQueryChain(sysTenantMapper)
                .select(BaseEntity::getId, SysTenantDO::getName, BaseEntityNoId::getEnableFlag).list();

            return sysTenantDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        });

    }

    /**
     * 获取：用户关联的租户
     * 备注：即表示：用户可以查看关联租户的信息
     */
    public static Set<Long> getUserRefTenantList() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Set<Long> resultSet = new HashSet<>();

        resultSet.add(currentTenantIdDefault); // 添加：默认的租户 id

        // 获取：用户 id关联的 tenantIdSet，map
        Map<Long, Set<Long>> userIdRefTenantIdSetMap = getUserIdRefTenantIdSetMap();

        Set<Long> tenantIdSet = userIdRefTenantIdSetMap.get(currentUserId);

        if (CollUtil.isNotEmpty(tenantIdSet)) {

            resultSet.addAll(tenantIdSet);

        }

        return resultSet;

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

}
