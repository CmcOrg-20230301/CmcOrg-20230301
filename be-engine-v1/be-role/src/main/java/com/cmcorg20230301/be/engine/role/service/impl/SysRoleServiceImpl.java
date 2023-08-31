package com.cmcorg20230301.be.engine.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.role.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.role.model.dto.SysRolePageDTO;
import com.cmcorg20230301.be.engine.role.model.vo.SysRoleInfoByIdVO;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefMenuService;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefUserService;
import com.cmcorg20230301.be.engine.role.service.SysRoleService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysMenuMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysMenuUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.util.util.MyMapUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleDO> implements SysRoleService {

    @Resource
    SysRoleRefMenuService sysRoleRefMenuService;

    @Resource
    SysRoleRefUserService sysRoleRefUserService;

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysRoleInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 角色名，不能重复
        boolean exists =
            lambdaQuery().eq(SysRoleDO::getName, dto.getName()).ne(dto.getId() != null, BaseEntity::getId, dto.getId())
                .eq(BaseEntityNoId::getTenantId, dto.getTenantId()).exists();

        if (exists) {
            ApiResultVO.error(BizCodeEnum.THE_SAME_ROLE_NAME_EXIST);
        }

        // 如果是默认角色，则取消之前的默认角色
        if (BooleanUtil.isTrue(dto.getDefaultFlag())) {

            lambdaUpdate().set(SysRoleDO::getDefaultFlag, false).eq(SysRoleDO::getDefaultFlag, true)
                .eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).update();

        }

        SysRoleDO sysRoleDO = new SysRoleDO();

        sysRoleDO.setName(dto.getName());
        sysRoleDO.setDefaultFlag(BooleanUtil.isTrue(dto.getDefaultFlag()));
        sysRoleDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysRoleDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysRoleDO.setDelFlag(false);
        sysRoleDO.setId(dto.getId());

        if (dto.getId() != null) {

            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先删除子表数据

        }

        saveOrUpdate(sysRoleDO);

        insertOrUpdateSub(dto, sysRoleDO); // 新增 子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysRoleInsertOrUpdateDTO dto, SysRoleDO sysRoleDO) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysRoleDO.getEnableFlag())) {
            return;
        }

        if (CollUtil.isNotEmpty(dto.getMenuIdSet())) {

            // 获取：没有被禁用的菜单 idSet
            Set<Long> menuIdSet =
                SysMenuUtil.getSysMenuCacheMap().keySet().stream().filter(it -> dto.getMenuIdSet().contains(it))
                    .collect(Collectors.toSet());

            List<SysRoleRefMenuDO> insertList = new ArrayList<>(MyMapUtil.getInitialCapacity(menuIdSet.size()));

            for (Long menuId : menuIdSet) {

                SysRoleRefMenuDO sysRoleRefMenuDO = new SysRoleRefMenuDO();

                sysRoleRefMenuDO.setRoleId(sysRoleDO.getId());
                sysRoleRefMenuDO.setMenuId(menuId);
                insertList.add(sysRoleRefMenuDO);

            }

            sysRoleRefMenuService.saveBatch(insertList);

        }

        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {

            // 获取：没有被禁用的用户 idSet
            List<SysUserDO> sysUserDOList =
                ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntity::getId, dto.getUserIdSet())
                    .eq(BaseEntity::getEnableFlag, true).select(BaseEntity::getId).list();

            Set<Long> userIdSet = sysUserDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

            List<SysRoleRefUserDO> insertList = new ArrayList<>(MyMapUtil.getInitialCapacity(userIdSet.size()));

            for (Long userId : userIdSet) {

                SysRoleRefUserDO sysRoleRefUserDO = new SysRoleRefUserDO();

                sysRoleRefUserDO.setRoleId(sysRoleDO.getId());
                sysRoleRefUserDO.setUserId(userId);
                insertList.add(sysRoleRefUserDO);

            }

            sysRoleRefUserService.saveBatch(insertList);

        }

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysRoleDO> myPage(SysRolePageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysRoleDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getDefaultFlag() != null, SysRoleDO::getDefaultFlag, dto.getDefaultFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntity::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysRoleInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysRoleDO sysRoleDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysRoleDO == null) {
            return null;
        }

        SysRoleInfoByIdVO sysRoleInfoByIdVO = BeanUtil.copyProperties(sysRoleDO, SysRoleInfoByIdVO.class);

        // 完善子表的数据
        List<SysRoleRefMenuDO> menuList =
            sysRoleRefMenuService.lambdaQuery().eq(SysRoleRefMenuDO::getRoleId, sysRoleInfoByIdVO.getId())
                .select(SysRoleRefMenuDO::getMenuId).list();

        List<SysRoleRefUserDO> userList =
            sysRoleRefUserService.lambdaQuery().eq(SysRoleRefUserDO::getRoleId, sysRoleInfoByIdVO.getId())
                .select(SysRoleRefUserDO::getUserId).list();

        sysRoleInfoByIdVO.setMenuIdSet(menuList.stream().map(SysRoleRefMenuDO::getMenuId).collect(Collectors.toSet()));
        sysRoleInfoByIdVO.setUserIdSet(userList.stream().map(SysRoleRefUserDO::getUserId).collect(Collectors.toSet()));

        return sysRoleInfoByIdVO;

    }

    /**
     * 批量删除
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        deleteByIdSetSub(notEmptyIdSet.getIdSet()); // 删除子表数据

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除 角色菜单关联表
        sysRoleRefMenuService.removeByIds(idSet);

        // 删除 角色用户关联表
        sysRoleRefUserService.removeByIds(idSet);

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(BaseEntity::getId, id).select(BaseEntity::getTenantId).one();

    }

}




