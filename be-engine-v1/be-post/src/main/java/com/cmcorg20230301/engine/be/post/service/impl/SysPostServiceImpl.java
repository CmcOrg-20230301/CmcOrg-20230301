package com.cmcorg20230301.engine.be.post.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.engine.be.post.mapper.SysPostMapper;
import com.cmcorg20230301.engine.be.post.model.dto.SysPostInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.post.model.dto.SysPostPageDTO;
import com.cmcorg20230301.engine.be.post.model.entity.SysPostDO;
import com.cmcorg20230301.engine.be.post.model.entity.SysPostRefUserDO;
import com.cmcorg20230301.engine.be.post.model.vo.SysPostInfoByIdVO;
import com.cmcorg20230301.engine.be.post.service.SysPostRefUserService;
import com.cmcorg20230301.engine.be.post.service.SysPostService;
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
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPostDO> implements SysPostService {

    @Resource
    SysPostRefUserService sysPostRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysPostInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：岗位名（不能重复）
        boolean exists = lambdaQuery().eq(SysPostDO::getName, dto.getName())
            .eq(BaseEntityTree::getParentId, MyEntityUtil.getNotNullParentId(dto.getParentId()))
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

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

            List<SysPostRefUserDO> insertList = new ArrayList<>();

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

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysPostDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
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

        List<SysPostDO> allList = list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysPostDOList, allList);

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

        sysPostRefUserService.removeByIds(idSet);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysPostInfoByIdVO infoById(NotNullId notNullId) {

        SysPostInfoByIdVO sysPostInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysPostInfoByIdVO.class);

        if (sysPostInfoByIdVO == null) {
            return null;
        }

        // 获取：绑定的用户 idSet
        List<SysPostRefUserDO> sysPostRefUserDOList =
            sysPostRefUserService.lambdaQuery().eq(SysPostRefUserDO::getPostId, notNullId.getId())
                .select(SysPostRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysPostRefUserDOList.stream().map(SysPostRefUserDO::getUserId).collect(Collectors.toSet());

        sysPostInfoByIdVO.setUserIdSet(userIdSet);

        MyEntityUtil.handleParentId(sysPostInfoByIdVO);

        return sysPostInfoByIdVO;

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

        List<SysPostDO> sysPostDOList = listByIds(dto.getIdSet());

        for (SysPostDO item : sysPostDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysPostDOList);

        return BaseBizCodeEnum.OK;

    }

}




