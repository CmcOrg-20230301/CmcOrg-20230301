package com.cmcorg20230301.be.engine.post.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostPageDTO;
import com.cmcorg20230301.be.engine.post.model.vo.SysPostInfoByIdVO;
import com.cmcorg20230301.be.engine.post.service.SysPostRefUserService;
import com.cmcorg20230301.be.engine.post.service.SysPostService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysPostMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
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
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPostDO> implements SysPostService {

    @Resource
    SysPostRefUserService sysPostRefUserService;

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysPostInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：岗位名（不能重复）
        boolean exists = lambdaQuery().eq(SysPostDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
            .exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：相同父节点下，岗位名不能重复");
        }

        if (dto.getId() != null) { // 如果是修改
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先移除子表数据
        }

        SysPostDO sysPostDO = new SysPostDO();

        sysPostDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysPostDO.setName(dto.getName());
        sysPostDO.setOrderNo(dto.getOrderNo());
        sysPostDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysPostDO.setId(dto.getId());
        sysPostDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysPostDO.setDelFlag(false);

        saveOrUpdate(sysPostDO);

        insertOrUpdateSub(dto, sysPostDO); // 新增：子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysPostInsertOrUpdateDTO dto, SysPostDO sysPostDO) {

        // 再新增子表数据
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {

            // 检查：用户 idSet，是否合法
            Long count = ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntity::getId, dto.getUserIdSet())
                .eq(BaseEntityNoIdFather::getTenantId, dto.getTenantId()).count();

            if (count != dto.getUserIdSet().size()) {
                ApiResultVO.errorMsg("操作失败：关联的用户数据非法");
            }

            List<SysPostRefUserDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getUserIdSet().size()));

            for (Long item : dto.getUserIdSet()) {

                SysPostRefUserDO sysPostRefUserDO = new SysPostRefUserDO();

                sysPostRefUserDO.setPostId(sysPostDO.getId());
                sysPostRefUserDO.setUserId(item);

                insertList.add(sysPostRefUserDO);

            }

            sysPostRefUserService.saveBatch(insertList);

        }

    }

    /**
     * 分页排序查询：岗位
     */
    @Override
    public Page<SysPostDO> myPage(SysPostPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysPostDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .eq(BaseEntityTree::getDelFlag, false).orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询岗位（树结构）
     */
    @Override
    public List<SysPostDO> tree(SysPostPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysPostDO> sysPostDOList = myPage(dto).getRecords();

        if (sysPostDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysPostDO> allList = lambdaQuery().in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysPostDOList, allList);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysPostInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysPostDO sysPostDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysPostDO == null) {
            return null;
        }

        SysPostInfoByIdVO sysPostInfoByIdVO = BeanUtil.copyProperties(sysPostDO, SysPostInfoByIdVO.class);

        // 获取：绑定的用户 idSet
        List<SysPostRefUserDO> sysPostRefUserDOList =
            sysPostRefUserService.lambdaQuery().eq(SysPostRefUserDO::getPostId, notNullId.getId())
                .select(SysPostRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysPostRefUserDOList.stream().map(SysPostRefUserDO::getUserId).collect(Collectors.toSet());

        sysPostInfoByIdVO.setUserIdSet(userIdSet);

        // 处理：父级 id
        MyEntityUtil.handleParentId(sysPostInfoByIdVO);

        return sysPostInfoByIdVO;

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

        // 移除子表数据
        deleteByIdSetSub(idSet);

        removeByIds(idSet);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量删除：移除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        sysPostRefUserService.removeByIds(idSet);

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    public String addOrderNo(ChangeNumberDTO dto) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.OK;
        }

        List<SysPostDO> sysPostDOList =
            lambdaQuery().in(BaseEntity::getId, dto.getIdSet()).select(BaseEntity::getId, BaseEntityTree::getOrderNo)
                .list();

        for (SysPostDO item : sysPostDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysPostDOList);

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




