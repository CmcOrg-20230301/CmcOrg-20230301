package com.cmcorg20230301.be.engine.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.menu.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuPageDTO;
import com.cmcorg20230301.be.engine.menu.model.vo.SysMenuInfoByIdVO;
import com.cmcorg20230301.be.engine.menu.service.SysMenuService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefMenuService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysMenuMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.util.util.MyMapUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuDO> implements SysMenuService {

    @Resource
    SysRoleRefMenuService sysRoleRefMenuService;

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysMenuInsertOrUpdateDTO dto) {

        // 检查：是否可以新增
        SysTenantUtil.checkInsert(dto);

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 检查：是否可以修改一些属性
        dto = checkUpdate(dto, dto.getId());

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        if (BooleanUtil.isTrue(dto.getAuthFlag()) && StrUtil.isBlank(dto.getAuths())) {
            ApiResultVO.errorMsg("操作失败：权限菜单的权限不能为空");
        }

        // path不能重复
        if (StrUtil.isNotBlank(dto.getPath())) {

            boolean exists =
                lambdaQuery().eq(SysMenuDO::getPath, dto.getPath()).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                    .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

            if (exists) {
                ApiResultVO.error(BizCodeEnum.MENU_URI_IS_EXIST);
            }

        }

        // 如果是起始页面，则取消之前的起始页面
        if (BooleanUtil.isTrue(dto.getFirstFlag())) {

            lambdaUpdate().set(SysMenuDO::getFirstFlag, false).eq(SysMenuDO::getFirstFlag, true)
                .eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).update();

        }

        if (dto.getId() != null) {
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先删除 子表数据
        }

        SysMenuDO sysMenuDO = getEntityByDTO(dto);

        saveOrUpdate(sysMenuDO);

        insertOrUpdateSub(sysMenuDO, dto); // 新增 子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：是否可以修改一些属性
     */
    private SysMenuInsertOrUpdateDTO checkUpdate(SysMenuInsertOrUpdateDTO dto, Long id) {

        if (id == null) {
            return dto;
        }

        if (SysTenantUtil.insertOrUpdateCommonCheck()) {
            return dto;
        }

        SysMenuDO sysMenuDO = lambdaQuery().eq(BaseEntity::getId, id)
            .select(SysMenuDO::getRouter, SysMenuDO::getAuths, SysMenuDO::getAuthFlag).one();

        SysMenuInsertOrUpdateDTO sysMenuInsertOrUpdateDTO = new SysMenuInsertOrUpdateDTO();

        sysMenuInsertOrUpdateDTO.setParentId(dto.getParentId());
        sysMenuInsertOrUpdateDTO.setName(dto.getName());
        sysMenuInsertOrUpdateDTO.setPath(dto.getPath());
        sysMenuInsertOrUpdateDTO.setIcon(dto.getIcon());
        sysMenuInsertOrUpdateDTO.setRoleIdSet(dto.getRoleIdSet());
        sysMenuInsertOrUpdateDTO.setEnableFlag(dto.getEnableFlag());
        sysMenuInsertOrUpdateDTO.setFirstFlag(dto.getFirstFlag());
        sysMenuInsertOrUpdateDTO.setOrderNo(dto.getOrderNo());
        sysMenuInsertOrUpdateDTO.setShowFlag(dto.getShowFlag());
        sysMenuInsertOrUpdateDTO.setRedirect(dto.getRedirect());
        sysMenuInsertOrUpdateDTO.setRemark(dto.getRemark());
        sysMenuInsertOrUpdateDTO.setTenantId(dto.getTenantId());
        sysMenuInsertOrUpdateDTO.setId(id);

        sysMenuInsertOrUpdateDTO.setRouter(sysMenuDO.getRouter()); // 不能修改
        sysMenuInsertOrUpdateDTO.setAuths(sysMenuDO.getAuths()); // 不能修改
        sysMenuInsertOrUpdateDTO.setAuthFlag(sysMenuDO.getAuthFlag()); // 不能修改

        return sysMenuInsertOrUpdateDTO;

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysMenuDO sysMenuDO, SysMenuInsertOrUpdateDTO dto) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysMenuDO.getEnableFlag())) {
            return;
        }

        // 新增：菜单角色 关联表数据
        if (CollUtil.isNotEmpty(dto.getRoleIdSet())) {

            List<SysRoleRefMenuDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getRoleIdSet().size()));

            for (Long item : dto.getRoleIdSet()) {

                SysRoleRefMenuDO sysRoleRefMenuDO = new SysRoleRefMenuDO();

                sysRoleRefMenuDO.setRoleId(item);
                sysRoleRefMenuDO.setMenuId(sysMenuDO.getId());

                insertList.add(sysRoleRefMenuDO);

            }

            sysRoleRefMenuService.saveBatch(insertList);

        }

    }

    /**
     * 通过 dto，获取 实体类
     */
    private SysMenuDO getEntityByDTO(SysMenuInsertOrUpdateDTO dto) {

        SysMenuDO sysMenuDO = new SysMenuDO();

        sysMenuDO.setName(dto.getName());
        sysMenuDO.setPath(MyEntityUtil.getNotNullStr(dto.getPath()));
        sysMenuDO.setIcon(MyEntityUtil.getNotNullStr(dto.getIcon()));
        sysMenuDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysMenuDO.setId(dto.getId());
        sysMenuDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));

        sysMenuDO.setLinkFlag(StrUtil.startWith(dto.getPath(), "http", true)); // 判断：path是否以 http开头

        sysMenuDO.setRouter(MyEntityUtil.getNotNullStr(dto.getRouter()));
        sysMenuDO.setRedirect(MyEntityUtil.getNotNullStr(dto.getRedirect()));
        sysMenuDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysMenuDO.setFirstFlag(BooleanUtil.isTrue(dto.getFirstFlag()));
        sysMenuDO.setAuthFlag(BooleanUtil.isTrue(dto.getAuthFlag()));
        sysMenuDO.setOrderNo(MyEntityUtil.getNotNullOrderNo(dto.getOrderNo()));

        sysMenuDO.setDelFlag(false);

        if (sysMenuDO.getAuthFlag()) {

            sysMenuDO.setAuths(dto.getAuths()); // 只有权限菜单，才可以设置 auths
            sysMenuDO.setShowFlag(false);

        } else {

            sysMenuDO.setAuths("");
            sysMenuDO.setShowFlag(BooleanUtil.isTrue(dto.getShowFlag()));

        }

        return sysMenuDO;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysMenuDO> myPage(SysMenuPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysMenuDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getPath()), SysMenuDO::getPath, dto.getPath())
            .like(StrUtil.isNotBlank(dto.getAuths()), SysMenuDO::getAuths, dto.getAuths())
            .like(StrUtil.isNotBlank(dto.getRedirect()), SysMenuDO::getRedirect, dto.getRedirect())
            .eq(StrUtil.isNotBlank(dto.getRouter()), SysMenuDO::getRouter, dto.getRouter())
            .eq(dto.getParentId() != null, SysMenuDO::getParentId, dto.getParentId())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getLinkFlag() != null, SysMenuDO::getLinkFlag, dto.getLinkFlag())
            .eq(dto.getFirstFlag() != null, SysMenuDO::getFirstFlag, dto.getFirstFlag())
            .eq(dto.getAuthFlag() != null, SysMenuDO::getAuthFlag, dto.getAuthFlag())
            .eq(dto.getShowFlag() != null, SysMenuDO::getShowFlag, dto.getShowFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysMenuDO> tree(SysMenuPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysMenuDO> sysMenuDOList = myPage(dto).getRecords();

        if (sysMenuDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysMenuDO> allList = lambdaQuery().in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysMenuDOList, allList);

    }

    /**
     * 批量删除
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        // 如果存在下级，则无法删除
        boolean exists = lambdaQuery().in(SysMenuDO::getParentId, notEmptyIdSet.getIdSet()).exists();

        if (exists) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        // 删除子表数据
        deleteByIdSetSub(notEmptyIdSet.getIdSet());

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除：角色菜单关联表
        sysRoleRefMenuService.lambdaUpdate().in(SysRoleRefMenuDO::getMenuId, idSet).remove();

    }

    /**
     * 获取：当前用户绑定的菜单
     */
    @Override
    public List<SysMenuDO> userSelfMenuList() {

        Long userId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseConstant.ADMIN_ID.equals(userId)) {

            // 如果是 admin账号，则查询所有【不是被禁用了的】菜单
            return SysMenuUtil.getSysMenuCacheMap().values().stream()
                .filter(it -> currentTenantIdDefault.equals(it.getTenantId()))
                .sorted(Comparator.comparing(BaseEntityTree::getOrderNo, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        }

        // 获取当前用户绑定的菜单
        Set<SysMenuDO> sysMenuDoSet = UserUtil.getMenuSetByUserId(userId, 1, currentTenantIdDefault);

        if (CollUtil.isEmpty(sysMenuDoSet)) {
            return new ArrayList<>();
        }

        return sysMenuDoSet.stream().sorted(Comparator.comparing(BaseEntityTree::getOrderNo, Comparator.reverseOrder()))
            .collect(Collectors.toList());

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysMenuInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysMenuDO sysMenuDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysMenuDO == null) {
            return null;
        }

        SysMenuInfoByIdVO sysMenuInfoByIdVO = BeanUtil.copyProperties(sysMenuDO, SysMenuInfoByIdVO.class);

        // 设置：角色 idSet
        List<SysRoleRefMenuDO> sysRoleRefMenuDOList =
            sysRoleRefMenuService.lambdaQuery().eq(SysRoleRefMenuDO::getMenuId, notNullId.getId())
                .select(SysRoleRefMenuDO::getRoleId).list();

        sysMenuInfoByIdVO
            .setRoleIdSet(sysRoleRefMenuDOList.stream().map(SysRoleRefMenuDO::getRoleId).collect(Collectors.toSet()));

        MyEntityUtil.handleParentId(sysMenuInfoByIdVO);

        return sysMenuInfoByIdVO;

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @MyTransactional
    public String addOrderNo(ChangeNumberDTO dto) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.OK;
        }

        List<SysMenuDO> sysMenuDOList = listByIds(dto.getIdSet());

        for (SysMenuDO item : sysMenuDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysMenuDOList);

        return BaseBizCodeEnum.OK;

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




