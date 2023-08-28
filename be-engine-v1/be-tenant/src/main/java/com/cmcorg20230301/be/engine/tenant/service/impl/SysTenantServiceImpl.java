package com.cmcorg20230301.be.engine.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantRefUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenantDO> implements SysTenantService {

    @Resource
    SysTenantRefUserService sysTenantRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysTenantInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：租户名（不能重复）
        boolean exists = lambdaQuery().eq(SysTenantDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：相同父节点下，租户名不能重复");
        }

        if (dto.getId() != null) { // 如果是修改
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId())); // 先移除子表数据
        }

        SysTenantDO sysTenantDO = new SysTenantDO();
        sysTenantDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysTenantDO.setName(dto.getName());
        sysTenantDO.setOrderNo(dto.getOrderNo());
        sysTenantDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysTenantDO.setId(dto.getId());
        sysTenantDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysTenantDO.setDelFlag(false);

        saveOrUpdate(sysTenantDO);

        insertOrUpdateSub(dto, sysTenantDO); // 新增：子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysTenantInsertOrUpdateDTO dto, SysTenantDO sysTenantDO) {

        // 再新增子表数据
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {

            List<SysTenantRefUserDO> insertList = new ArrayList<>();

            for (Long item : dto.getUserIdSet()) {

                SysTenantRefUserDO sysTenantRefUserDO = new SysTenantRefUserDO();

                sysTenantRefUserDO.setTenantId(sysTenantDO.getId());
                sysTenantRefUserDO.setUserId(item);

                insertList.add(sysTenantRefUserDO);

            }

            sysTenantRefUserService.saveBatch(insertList);

        }

    }

    /**
     * 分页排序查询：租户
     */
    @Override
    public Page<SysTenantDO> myPage(SysTenantPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysTenantDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
            .eq(BaseEntityTree::getDelFlag, false).orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询租户（树结构）
     */
    @Override
    public List<SysTenantDO> tree(SysTenantPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysTenantDO> sysTenantDOList = myPage(dto).getRecords();

        if (sysTenantDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysTenantDO> allList = list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysTenantDOList, allList);

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

        sysTenantRefUserService.removeByIds(idSet);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysTenantInfoByIdVO infoById(NotNullId notNullId) {

        SysTenantInfoByIdVO sysTenantInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysTenantInfoByIdVO.class);

        if (sysTenantInfoByIdVO == null) {
            return null;
        }

        // 获取：绑定的用户 idSet
        List<SysTenantRefUserDO> sysTenantRefUserDOList =
            sysTenantRefUserService.lambdaQuery().eq(SysTenantRefUserDO::getTenantId, notNullId.getId())
                .select(SysTenantRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysTenantRefUserDOList.stream().map(SysTenantRefUserDO::getUserId).collect(Collectors.toSet());

        sysTenantInfoByIdVO.setUserIdSet(userIdSet);

        MyEntityUtil.handleParentId(sysTenantInfoByIdVO);

        return sysTenantInfoByIdVO;

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

        List<SysTenantDO> sysTenantDOList = listByIds(dto.getIdSet());

        for (SysTenantDO item : sysTenantDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysTenantDOList);

        return BaseBizCodeEnum.OK;

    }

}




