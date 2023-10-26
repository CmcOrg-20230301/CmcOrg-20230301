package com.cmcorg20230301.be.engine.dict.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.dict.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictListByDictKeyDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.be.engine.dict.service.SysDictService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysDictMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysDictUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDO> implements SysDictService {

    /**
     * 新增/修改
     * 备注：这里修改了，租户管理那边也要一起修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysDictInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 检查：是否可以修改一些属性
        checkInsertOrUpdate(dto, dto.getId());

        if (SysDictTypeEnum.DICT.equals(dto.getType())) {

            // 字典 key和 name不能重复
            boolean exists = lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT)
                .and(i -> i.eq(SysDictDO::getDictKey, dto.getDictKey()).or().eq(SysDictDO::getName, dto.getName()))
                .eq(BaseEntity::getEnableFlag, true).ne(dto.getId() != null, BaseEntity::getId, dto.getId())
                .eq(BaseEntityNoId::getTenantId, dto.getTenantId()).exists();

            if (exists) {
                ApiResultVO.error(BizCodeEnum.SAME_KEY_OR_NAME_EXIST);
            }

            dto.setValue(-1); // 字典的value为 -1

        } else {

            if (dto.getValue() == null) {
                ApiResultVO.error(BizCodeEnum.VALUE_CANNOT_BE_EMPTY);
            }

            // 字典项 value和 name不能重复
            boolean exists =
                lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM).eq(BaseEntity::getEnableFlag, true)
                    .eq(SysDictDO::getDictKey, dto.getDictKey())
                    .and(i -> i.eq(SysDictDO::getValue, dto.getValue()).or().eq(SysDictDO::getName, dto.getName()))
                    .ne(dto.getId() != null, BaseEntity::getId, dto.getId())
                    .eq(BaseEntityNoId::getTenantId, dto.getTenantId()).exists();

            if (exists) {
                ApiResultVO.error(BizCodeEnum.SAME_VALUE_OR_NAME_EXIST);
            }

        }

        if (dto.getId() != null && SysDictTypeEnum.DICT.equals(dto.getType())) {

            // 如果是修改，并且是字典，那么也需要修改 该字典的字典项的 dictKey
            SysDictDO sysDictDO = lambdaQuery().eq(BaseEntity::getId, dto.getId()).select(SysDictDO::getDictKey).one();

            if (sysDictDO == null) {
                ApiResultVO.errorMsg("操作失败：字典不存在，请刷新重试");
            }

            if (!sysDictDO.getDictKey().equals(dto.getDictKey())) {

                lambdaUpdate().eq(SysDictDO::getDictKey, sysDictDO.getDictKey())
                    .eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM).set(SysDictDO::getDictKey, dto.getDictKey())
                    .update();

            }

        }

        SysDictDO sysDictDO = new SysDictDO();
        sysDictDO.setDictKey(dto.getDictKey());
        sysDictDO.setName(dto.getName());
        sysDictDO.setType(dto.getType());
        sysDictDO.setValue(dto.getValue());
        sysDictDO.setOrderNo(MyEntityUtil.getNotNullOrderNo(dto.getOrderNo()));
        sysDictDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysDictDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysDictDO.setDelFlag(false);
        sysDictDO.setId(dto.getId());

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseConstant.TOP_TENANT_ID.equals(currentTenantIdDefault)) { // 如果是：顶层租户

            sysDictDO.setSystemFlag(BooleanUtil.isTrue(dto.getSystemFlag()));

        } else {

            if (dto.getId() == null) {
                sysDictDO.setSystemFlag(false);
            }

        }

        saveOrUpdate(sysDictDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：是否可以修改一些属性
     */
    private void checkInsertOrUpdate(SysDictInsertOrUpdateDTO dto, Long id) {

        if (SysTenantUtil.adminOrDefaultTenantFlag()) {
            return;
        }

        if (id == null) {

            if (dto.getType().equals(SysDictTypeEnum.DICT)) {

                ApiResultVO.errorMsg("操作失败：租户不能新增字典，只能新增字典项");

            }

            return;

        }

        boolean exists = lambdaQuery().eq(BaseEntity::getId, id).eq(SysDictDO::getSystemFlag, true).exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：租户不能修改系统内置");
        }

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysDictDO> myPage(SysDictPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysDictDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .like(StrUtil.isNotBlank(dto.getDictKey()), SysDictDO::getDictKey, dto.getDictKey())
            .eq(dto.getType() != null, SysDictDO::getType, dto.getType())
            .eq(dto.getValue() != null, SysDictDO::getValue, dto.getValue())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(SysDictDO::getOrderNo).page(dto.page(true));

    }

    /**
     * 通过：dictKey获取字典项集合，备注：会进行缓存
     */
    @Override
    public List<DictIntegerVO> listByDictKey(SysDictListByDictKeyDTO dto) {

        return SysDictUtil.listByDictKey(dto.getDictKey());

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysDictDO> tree(SysDictPageDTO dto) {

        dto.setPageSize(-1); // 不分页
        List<SysDictDO> records = myPage(dto).getRecords();

        if (records.size() == 0) {
            return new ArrayList<>();
        }

        // 过滤出：为字典项的数据，目的：查询其所属字典，封装成树结构
        List<SysDictDO> dictItemList =
            records.stream().filter(it -> SysDictTypeEnum.DICT_ITEM.equals(it.getType())).collect(Collectors.toList());

        if (dictItemList.size() == 0) {

            // 如果没有字典项类型数据，则直接返回
            return records;

        }

        // 查询出：字典项所属，字典的信息
        List<SysDictDO> allDictList =
            records.stream().filter(item -> SysDictTypeEnum.DICT.equals(item.getType())).collect(Collectors.toList());

        Set<Long> dictIdSet = allDictList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        Set<String> dictKeySet = dictItemList.stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

        // 查询数据库：字典信息
        List<SysDictDO> sysDictDOList = lambdaQuery().notIn(dictIdSet.size() != 0, BaseEntity::getId, dictIdSet)
            .in(dictKeySet.size() != 0, SysDictDO::getDictKey, dictKeySet).eq(SysDictDO::getType, SysDictTypeEnum.DICT)
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).orderByDesc(SysDictDO::getOrderNo).list();

        // 拼接本次返回值所需的，所有字典
        allDictList.addAll(sysDictDOList);

        Map<String, SysDictDO> dictMap =
            allDictList.stream().collect(Collectors.toMap(it -> it.getTenantId() + it.getDictKey(), it -> it));

        // 封装 children
        for (SysDictDO item : dictItemList) {

            SysDictDO sysDictDO = dictMap.get(item.getTenantId() + item.getDictKey());

            List<SysDictDO> children = sysDictDO.getChildren();

            if (children == null) {

                children = new LinkedList<>();
                sysDictDO.setChildren(children);

            }

            children.add(item); // 添加：字典项

        }

        return allDictList;

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysDictDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
            .one();

    }

    /**
     * 批量删除
     */
    @Override
    @DSTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        if (!SysTenantUtil.adminOrDefaultTenantFlag()) {

            boolean exists =
                lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).eq(SysDictDO::getSystemFlag, true)
                    .exists();

            if (exists) {
                ApiResultVO.errorMsg("操作失败：租户不能删除系统内置");
            }

        }

        List<SysDictDO> sysDictDOList =
            lambdaQuery().in(BaseEntity::getId, idSet).eq(SysDictDO::getType, SysDictTypeEnum.DICT)
                .select(SysDictDO::getDictKey, BaseEntityNoIdFather::getTenantId).list();

        removeByIds(idSet); // 根据 idSet删除

        if (CollUtil.isEmpty(sysDictDOList)) {
            return BaseBizCodeEnum.OK;
        }

        // 如果删除是字典项的父级，则把其下的字典项也跟着删除了
        Map<Long, List<SysDictDO>> groupMap =
            sysDictDOList.stream().collect(Collectors.groupingBy(BaseEntityNoIdFather::getTenantId));

        for (Map.Entry<Long, List<SysDictDO>> item : groupMap.entrySet()) {

            Set<String> dictKeySet = item.getValue().stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

            lambdaUpdate().in(SysDictDO::getDictKey, dictKeySet).eq(BaseEntityNoIdFather::getTenantId, item.getKey())
                .remove();

        }

        return BaseBizCodeEnum.OK;

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

        List<SysDictDO> sysDictDOList =
            lambdaQuery().in(BaseEntity::getId, dto.getIdSet()).select(BaseEntity::getId, SysDictDO::getOrderNo).list();

        for (SysDictDO item : sysDictDOList) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysDictDOList);

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
