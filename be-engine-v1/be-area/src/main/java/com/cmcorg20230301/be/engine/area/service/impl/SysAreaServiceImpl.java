package com.cmcorg20230301.be.engine.area.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaPageDTO;
import com.cmcorg20230301.be.engine.area.model.vo.SysAreaInfoByIdVO;
import com.cmcorg20230301.be.engine.area.service.SysAreaRefDeptService;
import com.cmcorg20230301.be.engine.area.service.SysAreaService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysAreaMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysDeptMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
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
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysAreaDO> implements SysAreaService {

    @Resource
    SysAreaRefDeptService areaRefDeptService;

    @Resource
    SysDeptMapper sysDeptMapper;

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysAreaInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：区域名，不能重复
        boolean exists = lambdaQuery().eq(SysAreaDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
            .exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：相同父节点下，区域名不能重复");
        }

        SysAreaDO sysAreaDO = new SysAreaDO();

        sysAreaDO.setName(dto.getName());
        sysAreaDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysAreaDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysAreaDO.setId(dto.getId());
        sysAreaDO.setOrderNo(dto.getOrderNo());
        sysAreaDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysAreaDO.setDelFlag(false);

        if (dto.getId() != null) {
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先删除：子表数据
        }

        saveOrUpdate(sysAreaDO);

        insertOrUpdateSub(dto, sysAreaDO); // 新增：子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysAreaInsertOrUpdateDTO dto, SysAreaDO sysAreaDO) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysAreaDO.getEnableFlag())) {
            return;
        }

        // 再插入子表数据
        if (CollUtil.isNotEmpty(dto.getDeptIdSet())) {

            // 检查：部门 idSet，是否合法
            Long count = ChainWrappers.lambdaQueryChain(sysDeptMapper).in(BaseEntity::getId, dto.getDeptIdSet())
                .eq(BaseEntityNoIdFather::getTenantId, dto.getTenantId()).count();

            if (count != dto.getDeptIdSet().size()) {
                ApiResultVO.errorMsg("操作失败：关联的部门数据非法");
            }

            List<SysAreaRefDeptDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getDeptIdSet().size()));

            for (Long item : dto.getDeptIdSet()) {

                SysAreaRefDeptDO sysAreaRefDeptDO = new SysAreaRefDeptDO();

                sysAreaRefDeptDO.setAreaId(sysAreaDO.getId());
                sysAreaRefDeptDO.setDeptId(item);

                insertList.add(sysAreaRefDeptDO);

            }

            areaRefDeptService.saveBatch(insertList);

        }

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysAreaDO> myPage(SysAreaPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysAreaDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysAreaDO> tree(SysAreaPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysAreaDO> sysAreaDOList = myPage(dto).getRecords();

        if (sysAreaDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysAreaDO> allList = lambdaQuery().in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysAreaDOList, allList);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysAreaInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysAreaDO sysAreaDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysAreaDO == null) {
            return null;
        }

        SysAreaInfoByIdVO sysAreaInfoByIdVO = BeanUtil.copyProperties(sysAreaDO, SysAreaInfoByIdVO.class);

        // 设置：部门 idSet
        List<SysAreaRefDeptDO> areaRefDeptDOList =
            areaRefDeptService.lambdaQuery().eq(SysAreaRefDeptDO::getAreaId, notNullId.getId())
                .select(SysAreaRefDeptDO::getDeptId).list();

        sysAreaInfoByIdVO
            .setDeptIdSet(areaRefDeptDOList.stream().map(SysAreaRefDeptDO::getDeptId).collect(Collectors.toSet()));

        // 处理：父级 id
        MyEntityUtil.handleParentId(sysAreaInfoByIdVO);

        return sysAreaInfoByIdVO;

    }

    /**
     * 批量删除
     */
    @Override
    @DSTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkChildrenFlag) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        if (checkChildrenFlag) {

            // 如果存在下级，则无法删除
            boolean exists = lambdaQuery().in(BaseEntityTree::getParentId, idSet).exists();

            if (exists) {
                ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
            }

        }

        // 删除子表数据
        deleteByIdSetSub(idSet);

        removeByIds(idSet);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除：区域部门关联表
        areaRefDeptService.lambdaUpdate().in(SysAreaRefDeptDO::getAreaId, idSet).remove();

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @DSTransactional
    public String addOrderNo(ChangeNumberDTO dto) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.OK;
        }

        List<SysAreaDO> sysAreaDOList = listByIds(dto.getIdSet());

        for (SysAreaDO item : sysAreaDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysAreaDOList);

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




