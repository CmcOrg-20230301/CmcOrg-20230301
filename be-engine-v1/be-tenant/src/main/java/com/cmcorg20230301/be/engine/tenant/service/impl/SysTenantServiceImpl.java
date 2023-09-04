package com.cmcorg20230301.be.engine.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.dict.service.SysDictService;
import com.cmcorg20230301.be.engine.menu.service.SysMenuService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.model.model.vo.DictTreeVO;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.param.service.SysParamService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantDeleteConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenantDO> implements SysTenantService {

    @Resource
    SysTenantRefUserService sysTenantRefUserService;

    @Resource
    SysMenuService sysMenuService;

    @Resource
    SysParamService sysParamService;

    @Resource
    SysDictService sysDictService;

    List<ITenantDeleteConfiguration> iTenantDeleteConfigurationList;

    public SysTenantServiceImpl(
        @Autowired(required = false) List<ITenantDeleteConfiguration> iTenantDeleteConfigurationList) {

        this.iTenantDeleteConfigurationList = iTenantDeleteConfigurationList;

    }

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysTenantInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 检查：menuIdSet
        insertOrUpdateCheckMenuIdSet(dto);

        Long parentId = MyEntityUtil.getNotNullParentId(dto.getParentId());

        if (parentId == 0) {

            parentId = UserUtil.getCurrentTenantIdDefault(); // 赋值为：当前的租户 id

        }

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        // 相同父节点下：租户名（不能重复）
        boolean exists = lambdaQuery().eq(SysTenantDO::getName, dto.getName()).eq(BaseEntityTree::getParentId, parentId)
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：相同父节点下，租户名不能重复");
        }

        if (dto.getId() != null) { // 如果是修改
            deleteByIdSetSub(CollUtil.newHashSet(dto.getId()), false); // 先移除子表数据
        }

        SysTenantDO sysTenantDO = new SysTenantDO();

        sysTenantDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysTenantDO.setName(dto.getName());
        sysTenantDO.setOrderNo(dto.getOrderNo());
        sysTenantDO.setId(dto.getId());
        sysTenantDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysTenantDO.setDelFlag(false);

        if (dto.getId() == null) {

            // 租户 id，和上级租户 id，保持一致
            sysTenantDO.setTenantId(parentId);

            sysTenantDO.setParentId(parentId);

        }

        saveOrUpdate(sysTenantDO);

        insertOrUpdateSub(dto, sysTenantDO); // 新增：子表数据

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：menuIdSet
     */
    private void insertOrUpdateCheckMenuIdSet(SysTenantInsertOrUpdateDTO dto) {

        if (CollUtil.isEmpty(dto.getMenuIdSet())) {

            return;

        }

        Long currentUserId = UserUtil.getCurrentUserId();

        Set<SysMenuDO> sysMenuDoSet;

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) { // 如果是：admin，则是全部的菜单

            sysMenuDoSet = new HashSet<>(SysMenuUtil.getSysMenuCacheMap().values());

        } else {

            Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

            sysMenuDoSet = UserUtil.getMenuSetByUserId(currentUserId, 2, currentTenantIdDefault);

        }

        if (CollUtil.isEmpty(sysMenuDoSet)) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

        // 用户：拥有的菜单 idSet
        Set<Long> menuIdSet = sysMenuDoSet.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        if (!CollUtil.containsAll(menuIdSet, dto.getMenuIdSet())) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(SysTenantInsertOrUpdateDTO dto, SysTenantDO sysTenantDO) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysTenantDO.getEnableFlag())) {
            return;
        }

        Long tenantId = sysTenantDO.getId();

        // 再新增子表数据
        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {

            List<SysTenantRefUserDO> insertList = new ArrayList<>();

            for (Long item : dto.getUserIdSet()) {

                SysTenantRefUserDO sysTenantRefUserDO = new SysTenantRefUserDO();

                sysTenantRefUserDO.setTenantId(tenantId);
                sysTenantRefUserDO.setUserId(item);

                insertList.add(sysTenantRefUserDO);

            }

            sysTenantRefUserService.saveBatch(insertList);

        }

        boolean addMenuFlag;

        if (dto.getId() == null) {

            addMenuFlag = true;

        } else { // 如果原本就没有菜单，则也可以新增

            addMenuFlag =
                !sysMenuService.lambdaQuery().eq(BaseEntityNoId::getTenantId, dto.getId()).select(BaseEntity::getId)
                    .exists();

        }

        // 再新增菜单
        if (addMenuFlag && CollUtil.isNotEmpty(dto.getMenuIdSet())) {

            Map<Long, SysMenuDO> sysMenuCacheMap = SysMenuUtil.getSysMenuCacheMap();

            Set<SysMenuDO> fullSysMenuDoSet =
                SysMenuUtil.getFullSysMenuDoSet(dto.getMenuIdSet(), sysMenuCacheMap.values());

            if (CollUtil.isNotEmpty(fullSysMenuDoSet)) {

                List<SysMenuDO> insertList = new ArrayList<>();

                for (SysMenuDO item : fullSysMenuDoSet) {

                    SysMenuDO newSysMenuDO = BeanUtil.copyProperties(item, SysMenuDO.class);

                    newSysMenuDO.setTenantId(tenantId); // 设置：新的租户 id

                    insertList.add(newSysMenuDO);

                }

                // 重新设置：id 和 parentId
                MyTreeUtil.treeListSetNewIdAndParentId(insertList);

                sysMenuService.saveBatch(insertList);

            }

        }

        // 新增：字典
        List<SysDictDO> sysDictDOList =
            sysDictService.lambdaQuery().eq(BaseEntityNoId::getTenantId, BaseConstant.TENANT_ID).list();

        for (SysDictDO item : sysDictDOList) {

            item.setId(null);
            item.setCreateTime(null);
            item.setUpdateTime(null);
            item.setTenantId(tenantId);

        }

        sysDictService.saveBatch(sysDictDOList);

        // 新增：参数
        List<SysParamDO> sysParamDOList =
            sysParamService.lambdaQuery().eq(BaseEntityNoId::getTenantId, BaseConstant.TENANT_ID).list();

        for (SysParamDO item : sysParamDOList) {

            item.setId(null);
            item.setCreateTime(null);
            item.setUpdateTime(null);
            item.setTenantId(tenantId);

        }

        sysParamService.saveBatch(sysParamDOList);

    }

    /**
     * 分页排序查询：租户
     */
    @Override
    public Page<SysTenantDO> myPage(SysTenantPageDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseConstant.TENANT_ID.equals(currentTenantIdDefault)) {

            // 处理：MyTenantPageDTO
            SysTenantUtil.handleMyTenantPageDTO(dto, false);

        } else {

            // 处理：MyTenantPageDTO
            SysTenantUtil.handleMyTenantPageDTO(dto, true);

        }

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysTenantDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
            .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getId() != null, BaseEntity::getId, dto.getId()) //
            .eq(BaseEntityTree::getDelFlag, false) //
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictTreeVO> dictList() {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        Map<Long, SysTenantDO> sysTenantCacheMap = SysTenantUtil.getSysTenantCacheMap();

        List<DictTreeVO> dictListVOList =
            sysTenantCacheMap.entrySet().stream().filter(it -> tenantIdSet.contains(it.getKey()))
                .map(it -> new DictTreeVO(it.getValue().getId(), it.getValue().getName(), it.getValue().getParentId()))
                .collect(Collectors.toList());

        if (tenantIdSet.contains(BaseConstant.TENANT_ID)) {

            dictListVOList
                .add(new DictTreeVO(BaseConstant.TENANT_ID, BaseConstant.TENANT_NAME, BaseConstant.NEGATIVE_ONE));

        }

        return new Page<DictTreeVO>().setTotal(dictListVOList.size()).setRecords(dictListVOList);

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

        List<SysTenantDO> allList = lambdaQuery().in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).list();

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

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        // 如果存在下级，则无法删除
        boolean exists = lambdaQuery().in(BaseEntityTree::getParentId, notEmptyIdSet.getIdSet()).exists();

        if (exists) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        // 移除子表数据
        deleteByIdSetSub(notEmptyIdSet.getIdSet(), true);

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量删除：移除子表数据
     *
     * @param deleteFlag 是否是删除
     */
    private void deleteByIdSetSub(Set<Long> idSet, boolean deleteFlag) {

        sysTenantRefUserService.removeByIds(idSet);

        // 删除：关联的参数
        List<SysParamDO> sysParamDOList =
            sysParamService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntity::getId).list();

        if (CollUtil.isNotEmpty(sysParamDOList)) {

            Set<Long> paramIdSet = sysParamDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

            sysParamService.deleteByIdSet(new NotEmptyIdSet(paramIdSet));

        }

        // 删除：关联的字典
        List<SysDictDO> sysDictDOList =
            sysDictService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntity::getId).list();

        if (CollUtil.isNotEmpty(sysDictDOList)) {

            Set<Long> dictIdSet = sysDictDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

            sysDictService.deleteByIdSet(new NotEmptyIdSet(dictIdSet));

        }

        if (deleteFlag) {

            List<SysMenuDO> sysMenuDOList =
                sysMenuService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntity::getId).list();

            if (CollUtil.isNotEmpty(sysMenuDOList)) {

                Set<Long> menuIdSet = sysMenuDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

                sysMenuService.deleteByIdSet(new NotEmptyIdSet(menuIdSet), false);

            }

            if (CollUtil.isNotEmpty(iTenantDeleteConfigurationList) && CollUtil.isNotEmpty(idSet)) {

                for (ITenantDeleteConfiguration item : iTenantDeleteConfigurationList) {

                    item.handle(idSet); // 移除：租户相关的数据

                }

            }

        }

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysTenantInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysTenantDO sysTenantDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysTenantDO == null) {
            return null;
        }

        SysTenantInfoByIdVO sysTenantInfoByIdVO = BeanUtil.copyProperties(sysTenantDO, SysTenantInfoByIdVO.class);

        // 获取：绑定的用户 idSet
        List<SysTenantRefUserDO> sysTenantRefUserDOList =
            sysTenantRefUserService.lambdaQuery().eq(SysTenantRefUserDO::getTenantId, notNullId.getId())
                .select(SysTenantRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysTenantRefUserDOList.stream().map(SysTenantRefUserDO::getUserId).collect(Collectors.toSet());

        // 获取：绑定的菜单 idSet
        List<SysMenuDO> sysMenuDOList =
            sysMenuService.lambdaQuery().eq(BaseEntityNoId::getTenantId, notNullId.getId()).select(BaseEntity::getId)
                .list();

        Set<Long> menuIdSet = sysMenuDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysTenantInfoByIdVO.setUserIdSet(userIdSet);

        sysTenantInfoByIdVO.setMenuIdSet(menuIdSet);

        MyEntityUtil.handleParentId(sysTenantInfoByIdVO);

        return sysTenantInfoByIdVO;

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

        List<SysTenantDO> sysTenantDOList = listByIds(dto.getIdSet());

        for (SysTenantDO item : sysTenantDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysTenantDOList);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通过主键id，获取租户名
     */
    @Override
    public String getNameById(NotNullLong notNullLong) {

        if (notNullLong.getValue().equals(BaseConstant.TENANT_ID)) {

            return "";

        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap().get(notNullLong.getValue());

        if (sysTenantDO == null) {

            return null;

        }

        return sysTenantDO.getName();

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




