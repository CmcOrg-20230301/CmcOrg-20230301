package com.cmcorg20230301.be.engine.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.dict.service.SysDictService;
import com.cmcorg20230301.be.engine.menu.service.SysMenuService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.*;
import com.cmcorg20230301.be.engine.model.model.vo.DictTreeVO;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.param.service.SysParamService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantConfigurationByIdVO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantService;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
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

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SignWxService signWxService;

    @Nullable List<ITenantSignConfiguration> iTenantSignConfigurationList;

    public SysTenantServiceImpl(
            @Autowired(required = false) @Nullable List<ITenantSignConfiguration> iTenantSignConfigurationList) {

        this.iTenantSignConfigurationList = iTenantSignConfigurationList;

    }

    @Resource
    SysUserConfigurationService sysUserConfigurationService;

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysTenantInsertOrUpdateDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (currentTenantIdDefault.equals(dto.getId())) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST); // 不能修改自身租户的数据
        }

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
                getTenantIdBaseEntityFunc1());

        // 检查：menuIdSet
        insertOrUpdateCheckMenuIdSet(dto.getMenuIdSet());

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

        sysTenantDO.setManageName(MyEntityUtil.getNotNullStr(dto.getManageName(), BaseConstant.TENANT_MANAGE_NAME));

        sysTenantDO.setOrderNo(MyEntityUtil.getNotNullOrderNo(dto.getOrderNo()));
        sysTenantDO.setId(dto.getId());
        sysTenantDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysTenantDO.setDelFlag(false);

        // 处理：SysTenantDO对象
        handleSysTenantDO(dto, currentTenantIdDefault, parentId, sysTenantDO);

        saveOrUpdate(sysTenantDO);

        insertOrUpdateSub(dto, sysTenantDO); // 新增：子表数据

        if (dto.getId() == null) {

            if (CollUtil.isNotEmpty(iTenantSignConfigurationList)) {

                for (ITenantSignConfiguration item : iTenantSignConfigurationList) {

                    item.signUp(sysTenantDO.getId()); // 添加：租户额外的数据

                }

            }

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 处理：SysTenantDO对象
     */
    private void handleSysTenantDO(SysTenantInsertOrUpdateDTO dto, Long currentTenantIdDefault, Long parentId,
                                   SysTenantDO sysTenantDO) {

        if (dto.getId() == null) {

            // 租户 id，和上级租户 id，保持一致
            sysTenantDO.setTenantId(parentId);

            sysTenantDO.setParentId(parentId);

        }

    }

    /**
     * 检查：menuIdSet
     */
    private void insertOrUpdateCheckMenuIdSet(Set<Long> checkMenuIdSet) {

        if (CollUtil.isEmpty(checkMenuIdSet)) {
            return;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        Set<SysMenuDO> sysMenuDoSet;

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) { // 如果是：admin，则是全部的菜单

            sysMenuDoSet = new HashSet<>(SysMenuUtil.getSysMenuCacheMap().values());

        } else {

            Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

            sysMenuDoSet = UserUtil.getMenuSetByUserId(currentUserId, 2, currentTenantIdDefault);

        }

        if (CollUtil.isEmpty(sysMenuDoSet)) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 当前用户：拥有的菜单 idSet
        Set<Long> menuIdSet = sysMenuDoSet.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        if (!CollUtil.containsAll(menuIdSet, checkMenuIdSet)) {
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

            // 新增菜单
            handleFullSysMenuDoSet(tenantId, fullSysMenuDoSet, null);

        }

        // 新增：字典
        List<SysDictDO> sysDictDOList =
                sysDictService.lambdaQuery().eq(BaseEntityNoId::getTenantId, BaseConstant.TOP_TENANT_ID)
                        .eq(SysDictDO::getSystemFlag, true).list();

        // 执行：新增字典
        doHandleAndAddDict(tenantId, sysDictDOList, true);

        // 新增：参数
        List<SysParamDO> sysParamDOList =
                sysParamService.lambdaQuery().eq(BaseEntityNoId::getTenantId, BaseConstant.TOP_TENANT_ID)
                        .eq(SysParamDO::getSystemFlag, false).list();

        // 执行：新增参数
        doHandleAndAddParam(tenantId, sysParamDOList, true);

    }

    /**
     * 执行：新增参数
     */
    private void doHandleAndAddParam(Long tenantId, List<SysParamDO> sysParamDOList, boolean savaFlag) {

        for (SysParamDO item : sysParamDOList) {

            item.setId(null);
            item.setCreateTime(null);
            item.setUpdateTime(null);
            item.setTenantId(tenantId);

        }

        if (savaFlag) {

            sysParamService.saveBatch(sysParamDOList);

        }

    }

    /**
     * 执行：新增字典
     */
    private void doHandleAndAddDict(Long tenantId, List<SysDictDO> sysDictDOList, boolean savaFlag) {

        for (SysDictDO item : sysDictDOList) {

            item.setId(null);
            item.setCreateTime(null);
            item.setUpdateTime(null);
            item.setTenantId(tenantId);

        }

        if (savaFlag) {

            sysDictService.saveBatch(sysDictDOList);

        }

    }

    /**
     * 新增菜单
     */
    private void handleFullSysMenuDoSet(Long tenantId, Set<SysMenuDO> fullSysMenuDoSet,
                                        @Nullable VoidFunc1<Map<Long, Long>> voidFunc1) {

        if (CollUtil.isEmpty(fullSysMenuDoSet)) {
            return;
        }

        List<SysMenuDO> insertList = new ArrayList<>();

        for (SysMenuDO item : fullSysMenuDoSet) {

            SysMenuDO newSysMenuDO = BeanUtil.copyProperties(item, SysMenuDO.class);

            newSysMenuDO.setTenantId(tenantId); // 设置：新的租户 id

            insertList.add(newSysMenuDO);

        }

        // 重新设置：id 和 parentId
        MyTreeUtil.treeListSetNewIdAndParentId(insertList, voidFunc1);

        sysMenuService.saveBatch(insertList);

    }

    /**
     * 分页排序查询：租户
     */
    @SneakyThrows
    @Override
    public Page<SysTenantDO> myPage(SysTenantPageDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (UserUtil.getCurrentTenantTopFlag(currentTenantIdDefault)) {

            // 处理：MyTenantPageDTO
            SysTenantUtil.handleMyTenantPageDTO(dto, false);

        } else {

            // 处理：MyTenantPageDTO
            SysTenantUtil.handleMyTenantPageDTO(dto, true);

        }

        Page<SysTenantDO> page =
                lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysTenantDO::getName, dto.getName())
                        .like(StrUtil.isNotBlank(dto.getManageName()), SysTenantDO::getManageName, dto.getManageName())
                        .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityTree::getRemark, dto.getRemark())
                        .eq(dto.getEnableFlag() != null, BaseEntityTree::getEnableFlag, dto.getEnableFlag())
                        .eq(dto.getId() != null, BaseEntity::getId, dto.getId()) //
                        .eq(BaseEntityTree::getDelFlag, false) //
                        .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                        .orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

        if (CollUtil.isEmpty(page.getRecords())) {
            return page;
        }

        Set<Long> idSet = page.getRecords().stream().map(BaseEntity::getId).collect(Collectors.toSet());

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(4);

        AtomicReference<Map<Long, Long>> refMenuCountMap = new AtomicReference<>();

        AtomicReference<Map<Long, Long>> userCountMap = new AtomicReference<>();

        AtomicReference<Map<Long, Long>> dictCountMap = new AtomicReference<>();

        AtomicReference<Map<Long, Long>> paramCountMap = new AtomicReference<>();

        MyThreadUtil.execute(() -> {

            // 获取：绑定的菜单
            List<SysMenuDO> sysMenuDOList =
                    sysMenuService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntityNoId::getTenantId)
                            .list();

            refMenuCountMap.set(sysMenuDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors.counting())));

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：用户
            List<SysUserDO> sysUserDOList =
                    ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntityNoId::getTenantId, idSet)
                            .select(BaseEntityNoId::getTenantId).list();

            userCountMap.set(sysUserDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors.counting())));

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：字典
            List<SysDictDO> sysDictDOList =
                    sysDictService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntityNoId::getTenantId)
                            .list();

            dictCountMap.set(sysDictDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors.counting())));

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：参数
            List<SysParamDO> sysParamDOList =
                    sysParamService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntityNoId::getTenantId)
                            .list();

            paramCountMap.set(sysParamDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors.counting())));

        }, countDownLatch);

        countDownLatch.await();

        for (SysTenantDO item : page.getRecords()) {

            item.setRefMenuCount(refMenuCountMap.get().getOrDefault(item.getId(), 0L));

            item.setUserCount(userCountMap.get().getOrDefault(item.getId(), 0L));

            item.setDictCount(dictCountMap.get().getOrDefault(item.getId(), 0L));

            item.setParamCount(paramCountMap.get().getOrDefault(item.getId(), 0L));

        }

        return page;

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictTreeVO> dictList() {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        Map<Long, SysTenantDO> sysTenantCacheMap = SysTenantUtil.getSysTenantCacheMap(true);

        List<DictTreeVO> dictTreeVOList =
                sysTenantCacheMap.entrySet().stream().filter(it -> tenantIdSet.contains(it.getKey()))
                        .map(it -> new DictTreeVO(it.getValue().getId(), it.getValue().getName(), it.getValue().getParentId()))
                        .collect(Collectors.toList());

        return new Page<DictTreeVO>().setTotal(dictTreeVOList.size()).setRecords(dictTreeVOList);

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

        // 处理：父级 id
        MyEntityUtil.handleParentId(sysTenantInfoByIdVO);

        return sysTenantInfoByIdVO;

    }

    /**
     * 批量删除
     */
    @Override
    @DSTransactional
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

            sysParamService.deleteByIdSet(new NotEmptyIdSet(paramIdSet), false);

        }

        // 删除：关联的字典
        List<SysDictDO> sysDictDOList =
                sysDictService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntity::getId).list();

        if (CollUtil.isNotEmpty(sysDictDOList)) {

            Set<Long> dictIdSet = sysDictDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

            sysDictService.deleteByIdSet(new NotEmptyIdSet(dictIdSet), false);

        }

        if (deleteFlag) {

            List<SysMenuDO> sysMenuDOList =
                    sysMenuService.lambdaQuery().in(BaseEntityNoId::getTenantId, idSet).select(BaseEntity::getId).list();

            if (CollUtil.isNotEmpty(sysMenuDOList)) {

                Set<Long> menuIdSet = sysMenuDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

                sysMenuService.deleteByIdSet(new NotEmptyIdSet(menuIdSet), false, false);

            }

            if (CollUtil.isNotEmpty(iTenantSignConfigurationList) && CollUtil.isNotEmpty(idSet)) {

                for (ITenantSignConfiguration item : iTenantSignConfigurationList) {

                    item.delete(idSet); // 移除：租户额外的数据

                }

            }

        }

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

        List<SysTenantDO> sysTenantDOList =
                lambdaQuery().in(BaseEntity::getId, dto.getIdSet()).select(BaseEntity::getId, BaseEntityTree::getOrderNo)
                        .list();

        for (SysTenantDO item : sysTenantDOList) {
            item.setOrderNo((int) (item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysTenantDOList);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通过主键id，获取租户相关的配置
     */
    @SneakyThrows
    @Override
    public SysTenantConfigurationByIdVO getConfigurationById(NotNullLong notNullLong) {

        SysTenantConfigurationByIdVO sysTenantConfigurationByIdVO = new SysTenantConfigurationByIdVO();

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(2);

        MyThreadUtil.execute(() -> {

            SysUserConfigurationDO sysUserConfigurationDO = sysUserConfigurationService.getSysUserConfigurationDoByTenantId(notNullLong.getValue());

            sysTenantConfigurationByIdVO.setSignInNameSignUpEnable(sysUserConfigurationDO.getSignInNameSignUpEnable());

            sysTenantConfigurationByIdVO.setEmailSignUpEnable(sysUserConfigurationDO.getEmailSignUpEnable());

            sysTenantConfigurationByIdVO.setPhoneSignUpEnable(sysUserConfigurationDO.getPhoneSignUpEnable());

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            GetQrCodeVO getQrCodeVO = signWxService.signInGetQrCodeUrl(new UserSignBaseDTO(notNullLong.getValue()), false);

            sysTenantConfigurationByIdVO.setWxQrCodeSignUp(getQrCodeVO);

        }, countDownLatch);

        countDownLatch.await();

        return sysTenantConfigurationByIdVO;

    }

    /**
     * 通过主键id，获取租户后台管理系统名
     */
    @Override
    public String getManageNameById(NotNullLong notNullLong) {

        if (UserUtil.getCurrentTenantTopFlag(notNullLong.getValue())) {

            return BaseConstant.TENANT_MANAGE_NAME;

        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(notNullLong.getValue());

        if (sysTenantDO == null) {

            return null;

        }

        return sysTenantDO.getManageName();

    }

    /**
     * 通过主键id，获取租户名
     */
    @Override
    public String getNameById(NotNullLong notNullLong) {

        if (UserUtil.getCurrentTenantTopFlag(notNullLong.getValue())) {

            return "";

        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(notNullLong.getValue());

        if (sysTenantDO == null) {

            return null;

        }

        return sysTenantDO.getName();

    }

    /**
     * 获取：同步最新的数据给租户的数据
     */
    @Override
    public List<SysMenuDO> getSyncMenuInfo(NotNullId notNullId) {

        // 检查：租户 id
        SysTenantUtil.checkTenantId(notNullId.getId());

        Map<Long, SysMenuDO> sysMenuCacheMap = SysMenuUtil.getSysMenuCacheMap();

        // 查询出：所有的菜单数据
        Collection<SysMenuDO> allSysMenuDoCollection = sysMenuCacheMap.values();

        // 获取：当前租户的菜单信息
        List<SysMenuDO> sysMenuDOList =
                allSysMenuDoCollection.stream().filter(it -> it.getTenantId().equals(notNullId.getId()))
                        .collect(Collectors.toList());

        // 当前租户菜单的 uuidSet
        Set<String> uuidSet = sysMenuDOList.stream().map(SysMenuDO::getUuid).collect(Collectors.toSet());

        // 获取：需要增加的菜单集合
        Set<SysMenuDO> needAddMenuSet = allSysMenuDoCollection.stream()
                .filter(it -> UserUtil.getCurrentTenantTopFlag(it.getTenantId()) && !uuidSet.contains(it.getUuid()))
                .collect(Collectors.toSet());

        return MyTreeUtil.getFullTreeList(needAddMenuSet, allSysMenuDoCollection);

    }

    /**
     * 执行：同步最新的数据给租户
     */
    @Override
    @DSTransactional
    public String doSyncMenu(NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet) {

        Set<Long> valueSet = notNullIdAndNotEmptyLongSet.getValueSet();

        if (CollUtil.isEmpty(valueSet)) {
            return BaseBizCodeEnum.OK;
        }

        Long tenantId = notNullIdAndNotEmptyLongSet.getId();

        // 检查：租户 id
        SysTenantUtil.checkTenantId(tenantId);

        // 检查：菜单是否合法
        insertOrUpdateCheckMenuIdSet(valueSet);

        // 执行：新增菜单
        Map<Long, SysMenuDO> sysMenuCacheMap = SysMenuUtil.getSysMenuCacheMap();

        Set<SysMenuDO> fullSysMenuDoSet = SysMenuUtil.getFullSysMenuDoSet(valueSet, sysMenuCacheMap.values());

        // 获取：当前租户的菜单信息
        List<SysMenuDO> sysMenuDOList =
                sysMenuCacheMap.values().stream().filter(it -> it.getTenantId().equals(tenantId))
                        .collect(Collectors.toList());

        // 当前租户菜单的 uuid，菜单，map
        Map<String, SysMenuDO> currentUuidAndSysMenuDoMap =
                sysMenuDOList.stream().collect(Collectors.toMap(SysMenuDO::getUuid, it -> it));

        Map<Long, Long> removeIdMap = MapUtil.newHashMap(); // 被移除的 idMap

        Set<SysMenuDO> newFullSysMenuDoSet = new HashSet<>();

        for (SysMenuDO item : fullSysMenuDoSet) {

            if (currentUuidAndSysMenuDoMap.containsKey(item.getUuid())) { // 如果：同步了一个重复的菜单

                SysMenuDO sysMenuDO = currentUuidAndSysMenuDoMap.get(item.getUuid()); // 获取：该租户对应的菜单信息

                removeIdMap.put(item.getId(), sysMenuDO.getId());

            } else { // 过滤掉：已经存在的菜单

                newFullSysMenuDoSet.add(item);

            }

        }

        // 新增菜单
        handleFullSysMenuDoSet(tenantId, newFullSysMenuDoSet, idMap -> {

            idMap.putAll(removeIdMap); // 这里需要把：移除的 id，添加进去，目的：避免找不到对应的 parentId

        });

        return BaseBizCodeEnum.OK;

    }

    /**
     * 删除租户所有菜单
     */
    @Override
    public String deleteTenantAllMenu(NotEmptyIdSet notEmptyIdSet) {

        List<SysMenuDO> sysMenuDOList =
                sysMenuService.lambdaQuery().in(BaseEntityNoIdSuper::getTenantId, notEmptyIdSet.getIdSet())
                        .select(BaseEntity::getId).list();

        if (CollUtil.isEmpty(sysMenuDOList)) {
            return BaseBizCodeEnum.OK;
        }

        Set<Long> menuIdSet = sysMenuDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        // 执行：删除
        sysMenuService.deleteByIdSet(new NotEmptyIdSet(menuIdSet), false, true);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 执行：同步字典给租户
     * 备注：租户只能，新增修改删除字典项，并且不能是系统内置的字典项
     */
    @Override
    @DSTransactional
    public String doSyncDict() {

        // 查询出：所有租户
        Set<Long> tenantIdSet = SysTenantUtil.getSysTenantCacheMap(false).keySet();

        // 查询出：所有的字典
        List<SysDictDO> allSysDictDOList = sysDictService.lambdaQuery().list();

        // 默认租户的，系统内置字典
        List<SysDictDO> systemSysDictDOList = allSysDictDOList.stream()
                .filter(it -> it.getSystemFlag() && UserUtil.getCurrentTenantTopFlag(it.getTenantId()))
                .collect(Collectors.toList());

        // 根据：字典 key，进行分组
        Map<String, List<SysDictDO>> systemDictKeyMap =
                systemSysDictDOList.stream().collect(Collectors.groupingBy(SysDictDO::getDictKey));

        // 通过：租户 id，进行分组的 map
        Map<Long, List<SysDictDO>> tenantIdGroupMap =
                allSysDictDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId));

        List<SysDictDO> insertList = new ArrayList<>();

        for (Long tenantId : tenantIdSet) {

            List<SysDictDO> tenantSysDictDOList = tenantIdGroupMap.get(tenantId);

            // 当前租户的字典数据，根据：字典 key，进行分组
            Map<String, List<SysDictDO>> tenantDictKeyMap;

            if (tenantSysDictDOList == null) {

                tenantDictKeyMap = MapUtil.newHashMap();

            } else {

                tenantDictKeyMap = tenantSysDictDOList.stream().collect(Collectors.groupingBy(SysDictDO::getDictKey));

            }

            // 添加：需要新增的数据
            for (Map.Entry<String, List<SysDictDO>> item : systemDictKeyMap.entrySet()) {

                List<SysDictDO> tenantDictKeySysDictDOList = tenantDictKeyMap.get(item.getKey());

                // 复制一份新的集合
                List<SysDictDO> newSystemSysDictDOList =
                        item.getValue().stream().map(it -> BeanUtil.copyProperties(it, SysDictDO.class))
                                .collect(Collectors.toList());

                // 处理：newSystemSysDictDOList
                doHandleAndAddDict(tenantId, newSystemSysDictDOList, false);

                insertList.addAll(newSystemSysDictDOList); // 添加到：待新增集合里

                if (CollUtil.isEmpty(tenantDictKeySysDictDOList)) {
                    continue; // 如果：该租户不存在该字典
                }

                // 如果：该租户存在该字典，则找到该租户新增的字典项
                Set<String> systemDictKeySet =
                        newSystemSysDictDOList.stream().map(SysDictDO::getUuid).collect(Collectors.toSet());

                // 不要：名字相同的字典项
                Set<String> systemDictNameSet =
                        newSystemSysDictDOList.stream().map(SysDictDO::getName).collect(Collectors.toSet());

                // 该租户新增的字典项
                List<SysDictDO> tenantAddDictItemSysDictDOList = tenantDictKeySysDictDOList.stream().filter(
                                it -> !it.getSystemFlag() && it.getType().equals(SysDictTypeEnum.DICT_ITEM) && !systemDictKeySet
                                        .contains(it.getUuid()) && !systemDictNameSet.contains(it.getName()))
                        .collect(Collectors.toList());

                if (CollUtil.isEmpty(tenantAddDictItemSysDictDOList)) {
                    continue; // 如果租户没有新增额外的字典项
                }

                // 判断：如果：value，冲突了，则需要修改该租户，字典项的 value
                Set<Integer> systemDictValueSet =
                        newSystemSysDictDOList.stream().map(SysDictDO::getValue).collect(Collectors.toSet());

                int maxValue = newSystemSysDictDOList.stream().mapToInt(SysDictDO::getValue).max().orElse(1);

                int value = maxValue + 1;

                for (SysDictDO sysDictDO : tenantAddDictItemSysDictDOList) {

                    if (!systemDictValueSet.contains(sysDictDO.getValue())) {
                        continue;
                    }

                    // 如果包含了，该 value，则为：value
                    sysDictDO.setValue(value);

                    value = value + 1; // value加 1

                }

                insertList.addAll(tenantAddDictItemSysDictDOList); // 添加到：待新增集合里

            }

        }

        // 删除：租户的所有字典，然后再新增
        sysDictService.lambdaUpdate().ne(BaseEntityNoId::getTenantId, BaseConstant.TOP_TENANT_ID).remove();

        sysDictService.saveBatch(insertList);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 执行：同步参数给租户
     * 备注：租户只能修改非系统内置参数，并且不能新增参数
     */
    @Override
    @DSTransactional
    public String doSyncParam() {

        // 查询出：所有租户
        Set<Long> tenantIdSet = SysTenantUtil.getSysTenantCacheMap(false).keySet();

        // 查询出：所有的参数
        List<SysParamDO> allSysParamDOList = sysParamService.lambdaQuery().list();

        // 默认租户的，非系统内置字典
        List<SysParamDO> defaultTenantSysParamDOList = allSysParamDOList.stream()
                .filter(it -> !it.getSystemFlag() && UserUtil.getCurrentTenantTopFlag(it.getTenantId()))
                .collect(Collectors.toList());

        List<SysParamDO> insertList = new ArrayList<>();

        // 通过：租户 id，进行分组的 map
        Map<Long, List<SysParamDO>> tenantIdGroupMap =
                allSysParamDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId));

        for (Long tenantId : tenantIdSet) {

            // 复制一份新的集合
            List<SysParamDO> newDefaultTenantSysParamDOList =
                    defaultTenantSysParamDOList.stream().map(it -> BeanUtil.copyProperties(it, SysParamDO.class))
                            .collect(Collectors.toList());

            // 把租户自定义的一些值，用来覆盖：默认值，目的：不修改租户已经修改过的值
            List<SysParamDO> tenantSysParamDOList = tenantIdGroupMap.get(tenantId);

            if (CollUtil.isNotEmpty(tenantSysParamDOList)) {

                // 根据 uuid进行分组
                Map<String, SysParamDO> uuidGroupMap =
                        newDefaultTenantSysParamDOList.stream().collect(Collectors.toMap(SysParamDO::getUuid, it -> it));

                for (SysParamDO sysParamDO : tenantSysParamDOList) {

                    SysParamDO newDefaultTenantSysParamDO = uuidGroupMap.get(sysParamDO.getUuid());

                    if (newDefaultTenantSysParamDO == null) {
                        continue; // 如果不存在该配置，则继续下一次循环
                    }

                    // 如果存在该配置，则替换一些属性，备注：这里需要和：SysParamServiceImpl#checkUpdate，方法可以修改字段一致
                    newDefaultTenantSysParamDO.setValue(sysParamDO.getValue());

                }

            }

            // 处理：newDefaultTenantSysParamDOList
            doHandleAndAddParam(tenantId, newDefaultTenantSysParamDOList, false);

            insertList.addAll(newDefaultTenantSysParamDOList); // 添加到：待新增集合里

        }

        // 删除：租户的所有参数，然后再新增
        sysParamService.lambdaUpdate().ne(BaseEntityNoId::getTenantId, BaseConstant.TOP_TENANT_ID).remove();

        sysParamService.saveBatch(insertList);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量：解冻
     */
    @Override
    public String thaw(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        // 检查：是否是属于自己的租户
        SysTenantUtil.checkAndGetTenantIdSet(false, idSet);

        // 并且不能操作自身租户
        if (!SysTenantUtil.adminOrDefaultTenantFlag()) {

            // 检查：不能是自身租户，并且必须是子级租户
            SysTenantUtil.checkOnlyChildrenTenantIdSet(idSet);

        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(SysTenantDO::getEnableFlag, true).update();

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量：冻结
     */
    @Override
    public String freeze(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        // 检查：是否是属于自己的租户
        SysTenantUtil.checkAndGetTenantIdSet(false, idSet);

        // 并且不能操作自身租户
        if (!SysTenantUtil.adminOrDefaultTenantFlag()) {

            // 检查：不能是自身租户，并且必须是子级租户
            SysTenantUtil.checkOnlyChildrenTenantIdSet(idSet);

        }

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(SysTenantDO::getEnableFlag, false).update();

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




