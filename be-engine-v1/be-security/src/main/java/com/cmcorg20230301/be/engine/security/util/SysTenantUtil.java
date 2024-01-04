package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.model.model.dto.UserIdAndTenantIdDTO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
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
    private static SysUserMapper sysUserMapper;

    public SysTenantUtil(SysTenantMapper sysTenantMapper, SysTenantRefUserMapper sysTenantRefUserMapper, SysUserMapper sysUserMapper) {

        SysTenantUtil.sysTenantMapper = sysTenantMapper;
        SysTenantUtil.sysTenantRefUserMapper = sysTenantRefUserMapper;
        SysTenantUtil.sysUserMapper = sysUserMapper;

    }

    /**
     * 获取：租户 id，也可以检查：租户 id是否合法
     */
    @NotNull
    public static Long getTenantId(@Nullable Long tenantId) {

        if (tenantId == null || tenantId.equals(BaseConstant.TOP_TENANT_ID)) {

            return BaseConstant.TOP_TENANT_ID;

        }

        Map<Long, SysTenantDO> map = getSysTenantCacheMap(false);

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
    public static void checkTenantId(Long tenantId) {

        if (tenantId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 如果登录了，则需要判断，租户 id是否是：用户关联的租户 id
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        if (tenantIdSet.contains(tenantId) == false) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, tenantId);

        }

    }

    /**
     * 获取：租户缓存数据：map，备注：这里不包含：默认租户
     *
     * @param addDefaultFlag 是否添加：默认租户
     */
    @NotNull
    @Unmodifiable
    public static Map<Long, SysTenantDO> getSysTenantCacheMap(boolean addDefaultFlag) {

        Map<Long, SysTenantDO> map = MyCacheUtil.getMap(BaseRedisKeyEnum.SYS_TENANT_CACHE, CacheHelper.getDefaultLongMap(new SysTenantDO()), () -> {

            List<SysTenantDO> sysTenantDOList = ChainWrappers.lambdaQueryChain(sysTenantMapper).select(BaseEntity::getId, SysTenantDO::getName, BaseEntityNoId::getEnableFlag, SysTenantDO::getParentId).list();

            return sysTenantDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        });

        // 移除：默认值
        map = CacheHelper.handleDefaultLongMap(map);

        if (addDefaultFlag) {

            Map<Long, SysTenantDO> mapTemp = new HashMap<>(map.size());

            mapTemp.putAll(map);

            SysTenantDO sysTenantDO = getDefaultSysTenantDO();

            // 添加：顶层租户（平台）
            mapTemp.put(sysTenantDO.getId(), sysTenantDO);

            map = mapTemp;

        }

        return map;

    }

    /**
     * 获取：租户对象
     */
    @NotNull
    public static SysTenantDO getSysTenantDO(Long tenantId) {

        if (BaseConstant.TOP_TENANT_ID.equals(tenantId)) {
            return SysTenantUtil.getDefaultSysTenantDO();
        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(tenantId);

        if (sysTenantDO == null) {
            ApiResultVO.error("操作失败：租户不存在", tenantId);
        }

        if (!sysTenantDO.getEnableFlag()) {
            ApiResultVO.error("操作失败：租户已被禁用，无法进行操作", tenantId);
        }

        return sysTenantDO;

    }

    /**
     * 获取：顶层租户（平台）
     */
    @NotNull
    public static SysTenantDO getDefaultSysTenantDO() {

        SysTenantDO sysTenantDO = new SysTenantDO();

        sysTenantDO.setId(BaseConstant.TOP_TENANT_ID);
        sysTenantDO.setParentId(BaseConstant.NEGATIVE_ONE);
        sysTenantDO.setName(BaseConstant.TOP_TENANT_NAME);

        return sysTenantDO;

    }

    /**
     * 获取：用户关联的租户，包含自身的租户，备注：即表示：用户可以查看关联租户的信息
     */
    @NotNull
    public static Set<Long> getUserRefTenantIdSet() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Set<Long> resultSet = new HashSet<>();

        resultSet.add(currentTenantIdDefault); // 添加：默认的租户 id

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {

            CollUtil.addAll(resultSet, getSysTenantCacheMap(false).keySet()); // 添加：所有的 租户 id

        } else {

            // 获取：用户 id关联的 tenantIdSet，map
            Map<Long, Set<Long>> userIdRefTenantIdSetMap = getUserIdRefTenantIdSetMap();

            Set<Long> tenantIdSet = userIdRefTenantIdSetMap.get(currentUserId);

            CollUtil.addAll(resultSet, tenantIdSet);

            String refChildrenFlagStr = SysParamUtil.getValueByUuid(ParamConstant.TENANT_REF_CHILDREN_FLAG_UUID);

            Boolean refChildrenFlag = Convert.toBool(refChildrenFlagStr, false); // 默认：不关联

            if (refChildrenFlag) { // 如果：默认关联子级租户，则：获取下级租户

                Set<Long> tempResultSet = new HashSet<>();

                // 获取：下级租户
                for (Long item : resultSet) {

                    CollUtil.addAll(tempResultSet, getTenantDeepIdSet(item));

                }

                resultSet.addAll(tempResultSet);

            }

        }

        return resultSet;

    }

    /**
     * 通过：租户 id，获取：关联的所有的 子租户（包含本租户）
     */
    @Unmodifiable // 不可对返回值进行修改
    public static Set<Long> getTenantDeepIdSet(Long tenantId) {

        return MyCacheUtil.<Map<Long, Set<Long>>>getMap(BaseRedisKeyEnum.SYS_TENANT_DEEP_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

            List<SysTenantDO> tenantDOList = new ArrayList<>(getSysTenantCacheMap(false).values());

            SysTenantDO sysTenantDO = getDefaultSysTenantDO();

            tenantDOList.add(sysTenantDO); // 添加：顶层租户（平台）

            return MyTreeUtil.getIdAndDeepIdSetMap(tenantDOList, null);

        }).get(tenantId);

    }

    /**
     * 通过：租户 id，获取：下一级租户的主键 idSet，备注：只获取一级
     */
    public static Set<Long> getFirstChildrenTenantIdSet(Long tenantId) {

        return getSysTenantCacheMap(false).values().stream().filter(it -> it.getParentId().equals(tenantId)).map(BaseEntity::getId).collect(Collectors.toSet());

    }

    /**
     * 检查：不能是自身租户，并且必须是子级租户，注意：请自行检查：checkTenantIdSet，是否是属于当前用户的所管理的租户
     *
     * @param checkTenantIdSet 需要检查的租户 idSet
     */
    public static void checkOnlyChildrenTenantIdSet(Set<Long> checkTenantIdSet) {

        // 只能配置该账号的下级租户，并且不能配置本级租户
        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (checkTenantIdSet.contains(currentTenantIdDefault)) {

            ApiResultVO.error("操作失败：无法操作该租户", currentTenantIdDefault);

        } else {

            Set<Long> tenantDeepIdSet = SysTenantUtil.getTenantDeepIdSet(currentTenantIdDefault);

            // 必须完全符合 tenantDeepIdSet
            if (!CollUtil.containsAll(tenantDeepIdSet, checkTenantIdSet)) {

                ApiResultVO.error("操作失败：无法操作该租户", StrUtil.format("tenantDeepIdSet：{}，checkTenantIdSet：{}", tenantDeepIdSet, checkTenantIdSet));

            }

        }

    }

    /**
     * 获取：用户 id关联的 tenantIdSet，map
     */
    @Unmodifiable // 不可对返回值进行修改
    public static Map<Long, Set<Long>> getUserIdRefTenantIdSetMap() {

        return MyCacheUtil.getMap(BaseRedisKeyEnum.USER_ID_REF_TENANT_ID_SET_CACHE, CacheHelper.getDefaultLongSetMap(), () -> {

            List<SysTenantRefUserDO> sysTenantRefUserDOList = ChainWrappers.lambdaQueryChain(sysTenantRefUserMapper).select(SysTenantRefUserDO::getTenantId, SysTenantRefUserDO::getUserId).list();

            return sysTenantRefUserDOList.stream().collect(Collectors.groupingBy(SysTenantRefUserDO::getUserId, Collectors.mapping(SysTenantRefUserDO::getTenantId, Collectors.toSet())));

        });

    }

    /**
     * 处理：MyTenantPageDTO
     *
     * @param onlySelfTenantIdFlag 如果 dto没有传递 tenantIdSet，是否设置为：自身租户
     */
    public static void handleMyTenantPageDTO(@NotNull MyTenantPageDTO dto, boolean onlySelfTenantIdFlag) {

        Set<Long> tenantIdSet = dto.getTenantIdSet();

        // 处理：dto的tenantIdSet
        tenantIdSet = checkAndGetTenantIdSet(onlySelfTenantIdFlag, tenantIdSet);

        dto.setTenantIdSet(tenantIdSet);

    }

    /**
     * 处理或者检查：tenantIdSet
     */
    @NotNull
    public static Set<Long> checkAndGetTenantIdSet(boolean onlySelfTenantIdFlag, Set<Long> tenantIdSet) {

        // 获取：用户关联的租户
        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        if (CollUtil.isEmpty(tenantIdSet)) {

            if (onlySelfTenantIdFlag) {

                // 设置为：当前用户的租户
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

        return tenantIdSet;

    }

    /**
     * 处理：BaseTenantInsertOrUpdateDTO，备注：
     * 如果不存在 id，则强制设置 tenantId为当前用户的租户 id，
     * 如果存在 id，则获取数据库里，实际的 tenantId，然后也是强制设置，并且会检查：该 id所在的租户，是否是当前用户所管理的租户
     *
     * @param getTenantIdBaseEntityFunc1 备注：只会使用 BaseEntityNoIdFather的 tenantId属性
     */
    @SneakyThrows
    public static void handleBaseTenantInsertOrUpdateDTO(@NotNull BaseTenantInsertOrUpdateDTO dto, @NotNull Func1<Set<Long>, Long> getCheckIllegalFunc1, @NotNull Func1<Long, ? extends BaseEntityNoIdSuper> getTenantIdBaseEntityFunc1) {

        Long id = dto.getId();

        if (id == null) {

            dto.setTenantId(UserUtil.getCurrentTenantIdDefault());
            return;

        }

        Long tenantId = dto.getTenantId();

        if (tenantId == null) {

            BaseEntityNoIdSuper baseEntityNoIdSuper = getTenantIdBaseEntityFunc1.call(id);

            if (baseEntityNoIdSuper == null) {

                ApiResultVO.error("操作失败：id不存在", id);

            }

            if (baseEntityNoIdSuper.getTenantId() == null) {

                ApiResultVO.errorMsg("操作失败：tenantId为空，请联系管理员");

            }

            dto.setTenantId(baseEntityNoIdSuper.getTenantId());

        }

        // 检查：idSet所在的租户，是否是当前用户所管理的租户
        SysTenantUtil.checkIllegal(CollUtil.newHashSet(id), getCheckIllegalFunc1);

    }

    /**
     * 检查：是否非法操作，检查：idSet所在的租户，是否是当前用户所管理的租户
     */
    @SneakyThrows
    public static void checkIllegal(Set<Long> idSet, @NotNull Func1<Set<Long>, Long> func1) {

        if (CollUtil.isEmpty(idSet)) {
            return;
        }

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

        if (adminOrDefaultTenantFlag()) {
            return;
        }

        ApiResultVO.errorMsg("操作失败：租户不能进行新增操作");

    }

    /**
     * 检查：是否可以修改
     */
    public static boolean checkUpdate() {

        return adminOrDefaultTenantFlag();

    }

    /**
     * 检查：是否可以删除
     */
    public static void checkDelete() {

        if (adminOrDefaultTenantFlag()) {
            return;
        }

        ApiResultVO.errorMsg("操作失败：租户不能进行删除操作");

    }

    /**
     * 是否是：admin 或者 顶层租户（默认租户）
     *
     * @return true 是 false 不是
     */
    public static boolean adminOrDefaultTenantFlag() {

        Long currentUserId = UserUtil.getCurrentUserId();

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) { // admin可以进行任何操作
            return true;
        }

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseConstant.TOP_TENANT_ID.equals(currentTenantIdDefault)) {
            return true;
        }

        return false;

    }

    /**
     * 检查：userId，是否属于当前用户的租户
     */
    public static void checkUserId(Long userId) {

        if (userId == null) {
            return;
        }

        if (userId.equals(UserUtil.getCurrentUserId())) {
            return;
        }

        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        // 检查：userId，是否合法
        boolean exists = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, userId).in(BaseEntityNoIdSuper::getTenantId, userRefTenantIdSet).exists();

        if (!exists) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, userId);
        }

    }

    /**
     * 检查：userIdSet，是否属于当前用户的租户，备注：只判断本级租户
     */
    public static void checkUserIdSetBelongCurrentTenant(Set<Long> userIdSet) {

        if (CollUtil.isEmpty(userIdSet)) {
            return;
        }

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 检查：userIdSet，是否合法
        Long count = ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntity::getId, userIdSet).eq(BaseEntityNoIdSuper::getTenantId, currentTenantIdDefault).count();

        if (count != userIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, userIdSet);
        }

    }

    /**
     * 检查：UserIdAndTenantIdDTO对象
     */
    public static void checkUserIdAndTenantIdDTO(UserIdAndTenantIdDTO userIdAndTenantIdDTO) {

        checkUserIdAndTenantId(userIdAndTenantIdDTO.getUserId(), userIdAndTenantIdDTO.getTenantId());

    }

    /**
     * 检查：UserIdAndTenantId
     */
    public static void checkUserIdAndTenantId(Long userId, Long tenantId) {

        SysTenantUtil.checkUserId(userId);

        SysTenantUtil.checkTenantId(tenantId);

    }

}
