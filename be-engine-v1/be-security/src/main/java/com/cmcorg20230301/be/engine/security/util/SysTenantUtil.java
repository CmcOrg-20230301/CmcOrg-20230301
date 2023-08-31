package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 租户相关的工具类
 */
@Component
public class SysTenantUtil {

    private static SysTenantMapper sysTenantMapper;
    private static SysTenantRefUserMapper sysTenantRefUserMapper;

    public SysTenantUtil(SysTenantMapper sysTenantMapper, SysTenantRefUserMapper sysTenantRefUserMapper) {

        SysTenantUtil.sysTenantMapper = sysTenantMapper;
        SysTenantUtil.sysTenantRefUserMapper = sysTenantRefUserMapper;

    }

    /**
     * 获取：租户 id，也可以检查：租户 id是否合法
     */
    @NotNull
    public static Long getTenantId(@Nullable Long tenantId) {

        if (tenantId == null || tenantId.equals(BaseConstant.TENANT_ID)) {

            return BaseConstant.TENANT_ID;

        }

        Map<Long, SysTenantDO> map = getSysTenantCacheMap();

        SysTenantDO sysTenantDO = map.get(tenantId);

        if (sysTenantDO == null) {

            ApiResultVO.error(BaseBizCodeEnum.TENANT_DOES_NOT_EXIST, tenantId);

        } else if (sysTenantDO.getEnableFlag() == false) {

            ApiResultVO.error(BaseBizCodeEnum.TENANT_HAS_BEEN_DISABLED, tenantId);

        }

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        if (currentUserIdDefault.equals(BaseConstant.SYS_ID)) {

            return tenantId; // 如果：未登录，则直接返回 租户 id

        }

        // 如果登录了，则需要判断，租户 id是否是：用户关联的租户 id
        checkTenantId(tenantId);

        return tenantId;

    }

    /**
     * 如果登录了，则需要判断，租户 id是否是：用户关联的租户 id
     */
    public static void checkTenantId(@NotNull Long tenantId) {

        // 如果登录了，则需要判断，租户 id是否是：用户关联的租户 id
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        if (tenantIdSet.contains(tenantId) == false) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, tenantId);

        }

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
     * 获取：用户关联的租户，包含自身的租户
     * 备注：即表示：用户可以查看关联租户的信息
     */
    @NotNull
    public static Set<Long> getUserRefTenantIdSet() {

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
     * 处理：MyTenantPageDTO
     *
     * @param onlySelfTenantIdFlag 如果 dto没有传递 tenantIdSet，是否设置为：自身租户
     */
    public static void handleMyTenantPageDTO(@NotNull MyTenantPageDTO dto, boolean onlySelfTenantIdFlag) {

        Set<Long> tenantIdSet = dto.getTenantIdSet();

        // 获取：用户关联的租户
        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        if (CollUtil.isEmpty(tenantIdSet)) {

            if (onlySelfTenantIdFlag) {

                tenantIdSet = CollUtil.newHashSet(UserUtil.getCurrentTenantIdDefault());

            } else {

                tenantIdSet = userRefTenantIdSet;

            }

        } else {

            // 必须完全符合 userRefTenantIdSet
            if (!CollUtil.containsAll(userRefTenantIdSet, tenantIdSet)) {

                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

            }

        }

        dto.setTenantIdSet(tenantIdSet);

    }

    /**
     * 处理：BaseTenantInsertOrUpdateDTO
     */
    @SneakyThrows
    public static void handleBaseTenantInsertOrUpdateDTO(@NotNull BaseTenantInsertOrUpdateDTO dto,
        @NotNull Func1<Set<Long>, Long> getCheckIllegalFunc1,
        @NotNull Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1) {

        Long id = dto.getId();

        if (id == null) {

            dto.setTenantId(UserUtil.getCurrentTenantIdDefault());

            return;

        }

        Long tenantId = dto.getTenantId();

        if (tenantId == null) {

            BaseEntity baseEntity = getTenantIdBaseEntityFunc1.call(id);

            if (baseEntity == null) {

                ApiResultVO.error("操作失败：id不存在", id);

            }

            if (baseEntity.getTenantId() == null) {

                ApiResultVO.errorMsg("操作失败：tenantId为空，请联系管理员");

            }

            dto.setTenantId(baseEntity.getTenantId());

        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(CollUtil.newHashSet(id), getCheckIllegalFunc1);

    }

    /**
     * 检查：是否非法操作
     */
    @SneakyThrows
    public static void checkIllegal(Set<Long> idSet, @NotNull Func1<Set<Long>, Long> func1) {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        Long count = func1.call(tenantIdSet);

        if (idSet.size() != count) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

    }

    /**
     * 检查：是否可以新增
     */
    public static void checkInsert(BaseTenantInsertOrUpdateDTO dto) {

        Long id = dto.getId();

        if (id != null) {
            return;
        }

        if (insertOrUpdateCommonCheck()) {
            return;
        }

        ApiResultVO.errorMsg("操作失败：租户不能进行新增操作");

    }

    /**
     * 新增或者修改操作，通用的检查方法
     */
    public static boolean insertOrUpdateCommonCheck() {

        Long currentUserId = UserUtil.getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) { // admin可以进行任何操作
            return true;
        }

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseConstant.TENANT_ID.equals(currentTenantIdDefault)) { // 如果是：顶层租户的 用户，则可以：新增或者修改
            return true;
        }

        return false;

    }

}
