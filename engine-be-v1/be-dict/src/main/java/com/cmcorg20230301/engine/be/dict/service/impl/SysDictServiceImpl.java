package com.cmcorg20230301.engine.be.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.dict.exception.BizCodeEnum;
import com.cmcorg20230301.engine.be.dict.mapper.SysDictMapper;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.engine.be.dict.model.entity.SysDictDO;
import com.cmcorg20230301.engine.be.dict.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.engine.be.dict.model.vo.SysDictTreeVO;
import com.cmcorg20230301.engine.be.dict.service.SysDictService;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDO> implements SysDictService {

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysDictInsertOrUpdateDTO dto) {

        if (SysDictTypeEnum.DICT.equals(dto.getType())) {

            // 字典 key和 name不能重复
            boolean exists = lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT)
                .and(i -> i.eq(SysDictDO::getDictKey, dto.getDictKey()).or().eq(SysDictDO::getName, dto.getName()))
                .eq(BaseEntity::getEnableFlag, true).ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

            if (exists) {
                ApiResultVO.error(BizCodeEnum.SAME_KEY_OR_NAME_EXIST);
            }

            dto.setValue((byte)-1); // 字典的value为 -1

        } else {

            if (dto.getValue() == null) {
                ApiResultVO.error(BizCodeEnum.VALUE_CANNOT_BE_EMPTY);
            }

            // 字典项 value和 name不能重复
            boolean exists =
                lambdaQuery().eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM).eq(BaseEntity::getEnableFlag, true)
                    .eq(SysDictDO::getDictKey, dto.getDictKey())
                    .and(i -> i.eq(SysDictDO::getValue, dto.getValue()).or().eq(SysDictDO::getName, dto.getName()))
                    .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).exists();

            if (exists) {
                ApiResultVO.error(BizCodeEnum.SAME_VALUE_OR_NAME_EXIST);
            }

        }

        if (dto.getId() != null && SysDictTypeEnum.DICT.equals(dto.getType())) {

            // 如果是修改，并且是字典，那么也需要修改 该字典的字典项的 dictKey
            SysDictDO sysDictDO = lambdaQuery().eq(BaseEntity::getId, dto.getId()).select(SysDictDO::getDictKey).one();

            if (sysDictDO == null) {
                ApiResultVO.error("操作失败：字典不存在，请刷新重试");
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

        saveOrUpdate(sysDictDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysDictDO> myPage(SysDictPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysDictDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .like(StrUtil.isNotBlank(dto.getDictKey()), SysDictDO::getDictKey, dto.getDictKey())
            .eq(dto.getType() != null, SysDictDO::getType, dto.getType())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .orderByDesc(SysDictDO::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysDictTreeVO> tree(SysDictPageDTO dto) {

        dto.setPageSize(-1); // 不分页
        List<SysDictDO> records = myPage(dto).getRecords();

        List<SysDictTreeVO> resList = new ArrayList<>();

        if (records.size() == 0) {
            return resList;
        }

        // 过滤出为 字典项的数据，目的：查询其所属字典，封装成树结构
        List<SysDictDO> dictItemList =
            records.stream().filter(it -> SysDictTypeEnum.DICT_ITEM.equals(it.getType())).collect(Collectors.toList());

        if (dictItemList.size() == 0) {
            // 如果没有字典项类型数据，则直接返回
            for (SysDictDO item : records) {
                resList.add(BeanUtil.copyProperties(item, SysDictTreeVO.class));
            }
            return resList;
        }

        // 查询出 字典项所属 字典的信息
        List<SysDictDO> allDictList =
            records.stream().filter(item -> SysDictTypeEnum.DICT.equals(item.getType())).collect(Collectors.toList());

        Set<Long> dictIdSet = allDictList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        Set<String> dictKeySet = dictItemList.stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

        // 查询数据库
        List<SysDictDO> sysDictDOList = lambdaQuery().notIn(dictIdSet.size() != 0, BaseEntity::getId, dictIdSet)
            .in(dictKeySet.size() != 0, SysDictDO::getDictKey, dictKeySet).eq(SysDictDO::getType, SysDictTypeEnum.DICT)
            .orderByDesc(SysDictDO::getOrderNo).list();

        // 拼接本次返回值所需的所有 字典
        allDictList.addAll(sysDictDOList);

        for (SysDictDO item : allDictList) {
            resList.add(BeanUtil.copyProperties(item, SysDictTreeVO.class));
        }

        // 封装 children
        for (SysDictDO item : dictItemList) {

            SysDictTreeVO sysDictTreeVO = BeanUtil.copyProperties(item, SysDictTreeVO.class);

            for (SysDictTreeVO subItem : resList) {

                if (subItem.getDictKey().equals(item.getDictKey())) {

                    List<SysDictTreeVO> children = subItem.getChildren();

                    if (children == null) {

                        children = new ArrayList<>();
                        subItem.setChildren(children);

                    }

                    children.add(sysDictTreeVO);

                    break;

                }

            }
        }

        return resList;

    }

    /**
     * 批量删除
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 根据 idSet删除
        removeByIds(notEmptyIdSet.getIdSet());

        List<SysDictDO> sysDictDOList =
            lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).eq(SysDictDO::getType, SysDictTypeEnum.DICT)
                .select(SysDictDO::getDictKey).list();

        if (CollUtil.isEmpty(sysDictDOList)) {
            return BaseBizCodeEnum.OK;
        }

        // 如果删除是字典项的父级，则把其下的字典项也跟着删除了
        Set<String> dictKeySet = sysDictDOList.stream().map(SysDictDO::getDictKey).collect(Collectors.toSet());

        lambdaUpdate().in(SysDictDO::getDictKey, dictKeySet).remove();

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysDictDO infoById(NotNullId notNullId) {

        return getById(notNullId.getId());

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @MyTransactional
    public String addOrderNo(ChangeNumberDTO dto) {

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        List<SysDictDO> listByIds = listByIds(dto.getIdSet());

        for (SysDictDO item : listByIds) {
            item.setOrderNo((int)(item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.OK;

    }
}




