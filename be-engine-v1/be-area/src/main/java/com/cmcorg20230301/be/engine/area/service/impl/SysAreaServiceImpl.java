package com.cmcorg20230301.be.engine.area.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.area.mapper.SysAreaMapper;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaPageDTO;
import com.cmcorg20230301.be.engine.area.model.entity.SysAreaDO;
import com.cmcorg20230301.be.engine.area.model.entity.SysAreaRefDeptDO;
import com.cmcorg20230301.be.engine.area.model.vo.SysAreaInfoByIdVO;
import com.cmcorg20230301.be.engine.area.service.SysAreaRefDeptService;
import com.cmcorg20230301.be.engine.area.service.SysAreaService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.TenantUtil;
import com.cmcorg20230301.be.engine.util.util.MyMapUtil;
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

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysAreaInsertOrUpdateDTO dto) {

        // 检查：租户 id是否合法
        TenantUtil.getTenantId(dto.getTenantId());

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：区域名，不能重复
        boolean exists = lambdaQuery().eq(SysAreaDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

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
        TenantUtil.handleMyTenantPageDTO(dto);

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

        List<SysAreaDO> allList = list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysAreaDOList, allList);

    }

    /**
     * 批量删除
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 检查：是否非法操作
        TenantUtil.checkIllegal(notEmptyIdSet.getIdSet(),
            tenantIdSet -> lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet())
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        // 如果存在下级，则无法删除
        boolean exists = lambdaQuery().in(BaseEntityTree::getParentId, notEmptyIdSet.getIdSet()).exists();

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

        // 删除：区域部门关联表
        areaRefDeptService.lambdaUpdate().in(SysAreaRefDeptDO::getAreaId, idSet).remove();

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysAreaInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = TenantUtil.getUserRefTenantIdSet();

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

        MyEntityUtil.handleParentId(sysAreaInfoByIdVO);

        return sysAreaInfoByIdVO;

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @MyTransactional
    public String addOrderNo(ChangeNumberDTO dto) {

        // 检查：是否非法操作
        TenantUtil.checkIllegal(dto.getIdSet(), tenantIdSet -> lambdaQuery().in(BaseEntity::getId, dto.getIdSet())
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

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

}




