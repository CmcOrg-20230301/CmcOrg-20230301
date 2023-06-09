package com.cmcorg20230301.engine.be.dept.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.area.model.entity.SysAreaRefDeptDO;
import com.cmcorg20230301.engine.be.area.service.SysAreaRefDeptService;
import com.cmcorg20230301.engine.be.dept.mapper.SysDeptMapper;
import com.cmcorg20230301.engine.be.dept.model.dto.SysDeptInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.dept.model.dto.SysDeptPageDTO;
import com.cmcorg20230301.engine.be.dept.model.entity.SysDeptDO;
import com.cmcorg20230301.engine.be.dept.model.entity.SysDeptRefUserDO;
import com.cmcorg20230301.engine.be.dept.model.vo.SysDeptInfoByIdVO;
import com.cmcorg20230301.engine.be.dept.service.SysDeptRefUserService;
import com.cmcorg20230301.engine.be.dept.service.SysDeptService;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.MyTreeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDeptDO> implements SysDeptService {

    @Resource
    SysAreaRefDeptService sysAreaRefDeptService;

    @Resource
    SysDeptRefUserService sysDeptRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysDeptInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：部门名（不能重复）
        boolean exists = lambdaQuery().eq(SysDeptDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

        if (exists) {
            ApiResultVO.error("操作失败：相同父节点下，部门名不能重复");
        }

        SysDeptDO sysDeptDO = new SysDeptDO();
        sysDeptDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysDeptDO.setName(dto.getName());
        sysDeptDO.setOrderNo(dto.getOrderNo());
        sysDeptDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysDeptDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysDeptDO.setId(dto.getId());
        sysDeptDO.setDelFlag(false);

        if (dto.getId() != null) {
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先删除子表数据，再插入
        }

        saveOrUpdate(sysDeptDO);

        insertOrUpdateSub(dto, sysDeptDO); // 新增：子表数据

        return BaseBizCodeEnum.OK;
    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysDeptInsertOrUpdateDTO dto, SysDeptDO sysDeptDO) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysDeptDO.getEnableFlag())) {
            return;
        }

        // 插入子表数据：区域，部门关联表
        if (CollUtil.isNotEmpty(dto.getAreaIdSet())) {

            List<SysAreaRefDeptDO> insertList = new ArrayList<>();

            for (Long item : dto.getAreaIdSet()) {

                SysAreaRefDeptDO sysAreaRefDeptDO = new SysAreaRefDeptDO();
                sysAreaRefDeptDO.setAreaId(item);
                sysAreaRefDeptDO.setDeptId(sysDeptDO.getId());

                insertList.add(sysAreaRefDeptDO);

            }

            sysAreaRefDeptService.saveBatch(insertList);

        }

        // 插入子表数据：部门，用户关联表
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {

            List<SysDeptRefUserDO> insertList = new ArrayList<>();
            for (Long item : dto.getUserIdSet()) {

                SysDeptRefUserDO sysDeptRefUserDO = new SysDeptRefUserDO();

                sysDeptRefUserDO.setUserId(item);
                sysDeptRefUserDO.setDeptId(sysDeptDO.getId());

                insertList.add(sysDeptRefUserDO);

            }

            sysDeptRefUserService.saveBatch(insertList);

        }

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysDeptDO> myPage(SysDeptPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysDeptDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
            .eq(BaseEntityTree::getDelFlag, false).orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysDeptDO> tree(SysDeptPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysDeptDO> sysDeptDOList = myPage(dto).getRecords();

        if (sysDeptDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysDeptDO> allList = list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysDeptDOList, allList);

    }

    /**
     * 批量删除
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 如果存在下级，则无法删除
        boolean exists = lambdaQuery().in(BaseEntityTree::getParentId, notEmptyIdSet.getIdSet()).exists();

        if (exists) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        // 移除子表数据
        deleteByIdSetSub(notEmptyIdSet.getIdSet());

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量删除：移除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        sysAreaRefDeptService.lambdaUpdate().in(SysAreaRefDeptDO::getDeptId, idSet).remove();

        sysDeptRefUserService.lambdaUpdate().in(SysDeptRefUserDO::getDeptId, idSet).remove();

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysDeptDO infoById(NotNullId notNullId) {

        SysDeptInfoByIdVO sysDeptInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysDeptInfoByIdVO.class);

        if (sysDeptInfoByIdVO == null) {
            return null;
        }

        // 获取：绑定的区域 idSet
        List<SysAreaRefDeptDO> sysAreaRefDeptDOList =
            sysAreaRefDeptService.lambdaQuery().eq(SysAreaRefDeptDO::getDeptId, notNullId.getId())
                .select(SysAreaRefDeptDO::getAreaId).list();

        Set<Long> areaIdSet =
            sysAreaRefDeptDOList.stream().map(SysAreaRefDeptDO::getAreaId).collect(Collectors.toSet());

        // 获取：绑定的用户 idSet
        List<SysDeptRefUserDO> sysDeptRefUserDOList =
            sysDeptRefUserService.lambdaQuery().eq(SysDeptRefUserDO::getDeptId, notNullId.getId())
                .select(SysDeptRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysDeptRefUserDOList.stream().map(SysDeptRefUserDO::getUserId).collect(Collectors.toSet());

        sysDeptInfoByIdVO.setAreaIdSet(areaIdSet);
        sysDeptInfoByIdVO.setUserIdSet(userIdSet);

        MyEntityUtil.handleParentId(sysDeptInfoByIdVO);

        return sysDeptInfoByIdVO;

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @MyTransactional
    public String addOrderNo(ChangeNumberDTO dto) {

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.OK;
        }

        List<SysDeptDO> sysDeptDOList = listByIds(dto.getIdSet());

        for (SysDeptDO item : sysDeptDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysDeptDOList);

        return BaseBizCodeEnum.OK;

    }

}




