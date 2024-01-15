package com.cmcorg20230301.be.engine.other.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppOfficialAccountMenuMapper;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuPageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppOfficialAccountMenuDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppOfficialAccountMenuButtonTypeEnum;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppOfficialAccountMenuTypeEnum;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppOfficialAccountMenuService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_OFFICIAL_ACCOUNT_MENU)
public class SysOtherAppOfficialAccountMenuServiceImpl
        extends ServiceImpl<SysOtherAppOfficialAccountMenuMapper, SysOtherAppOfficialAccountMenuDO>
        implements SysOtherAppOfficialAccountMenuService {

    @Resource
    SysOtherAppMapper sysOtherAppMapper;

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysOtherAppOfficialAccountMenuInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
                getTenantIdBaseEntityFunc1());

        Long otherAppId = dto.getOtherAppId();

        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        // 第三方应用，必须是在自己租户下
        SysOtherAppDO sysOtherAppDO =
                ChainWrappers.lambdaQueryChain(sysOtherAppMapper).eq(BaseEntity::getId, otherAppId)
                        .in(BaseEntityNoIdSuper::getTenantId, userRefTenantIdSet).select(SysOtherAppDO::getType).one();

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, otherAppId);
        }

        SysOtherAppOfficialAccountMenuDO sysOtherAppOfficialAccountMenuDO = new SysOtherAppOfficialAccountMenuDO();

        sysOtherAppOfficialAccountMenuDO.setOtherAppId(dto.getOtherAppId());

        Integer type = sysOtherAppDO.getType();

        // 暂时：只能配置：微信公众号类型的第三方应用
        if (SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT.getCode() == type) {

            sysOtherAppOfficialAccountMenuDO.setType(SysOtherAppOfficialAccountMenuTypeEnum.WX_OFFICIAL_ACCOUNT.getCode());

        } else {

            ApiResultVO.error("操作失败：暂不支持配置该类型的第三方应用", dto.getOtherAppId());

        }

        // 同一个类型和同一个第三方 appId下，并且是按钮类型时，value 不能重复
        boolean exists = lambdaQuery().eq(SysOtherAppOfficialAccountMenuDO::getOtherAppId, dto.getOtherAppId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId())
                .eq(SysOtherAppOfficialAccountMenuDO::getType, sysOtherAppOfficialAccountMenuDO.getType())
                .eq(SysOtherAppOfficialAccountMenuDO::getButtonType, SysOtherAppOfficialAccountMenuButtonTypeEnum.CLICK)
                .eq(SysOtherAppOfficialAccountMenuDO::getValue, dto.getValue()).exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：同一个公众号下，按钮类型的菜单，值不能重复");
        }

        sysOtherAppOfficialAccountMenuDO.setName(dto.getName());
        sysOtherAppOfficialAccountMenuDO.setButtonType(dto.getButtonType());

        sysOtherAppOfficialAccountMenuDO.setValue(dto.getValue());
        sysOtherAppOfficialAccountMenuDO.setReplyContent(MyEntityUtil.getNotNullStr(dto.getReplyContent()));

        sysOtherAppOfficialAccountMenuDO.setOrderNo(MyEntityUtil.getNotNullOrderNo(dto.getOrderNo()));
        sysOtherAppOfficialAccountMenuDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));

        sysOtherAppOfficialAccountMenuDO.setId(dto.getId());
        sysOtherAppOfficialAccountMenuDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysOtherAppOfficialAccountMenuDO.setDelFlag(false);
        sysOtherAppOfficialAccountMenuDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysOtherAppOfficialAccountMenuDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysOtherAppOfficialAccountMenuDO> myPage(SysOtherAppOfficialAccountMenuPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery()
                .eq(dto.getOtherAppId() != null, SysOtherAppOfficialAccountMenuDO::getOtherAppId, dto.getOtherAppId())
                .like(StrUtil.isNotBlank(dto.getName()), SysOtherAppOfficialAccountMenuDO::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getValue()), SysOtherAppOfficialAccountMenuDO::getValue, dto.getValue())
                .like(StrUtil.isNotBlank(dto.getReplyContent()), SysOtherAppOfficialAccountMenuDO::getReplyContent,
                        dto.getReplyContent()) //
                .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
                .eq(dto.getType() != null, SysOtherAppOfficialAccountMenuDO::getType, dto.getType())
                .eq(dto.getButtonType() != null, SysOtherAppOfficialAccountMenuDO::getButtonType, dto.getButtonType())
                .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                .orderByDesc(BaseEntityTree::getOrderNo).page(dto.page(true));

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysOtherAppOfficialAccountMenuDO> tree(SysOtherAppOfficialAccountMenuPageDTO dto) {

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysOtherAppOfficialAccountMenuDO> sysOtherAppOfficialAccountMenuDOList = myPage(dto).getRecords();

        if (sysOtherAppOfficialAccountMenuDOList.size() == 0) {
            return new ArrayList<>();
        }

        List<SysOtherAppOfficialAccountMenuDO> allList =
                lambdaQuery().in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()).list();

        if (allList.size() == 0) {
            return new ArrayList<>();
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysOtherAppOfficialAccountMenuDOList, allList);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysOtherAppOfficialAccountMenuDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysOtherAppOfficialAccountMenuDO sysOtherAppOfficialAccountMenuDO =
                lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                        .one();

        MyEntityUtil.handleParentId(sysOtherAppOfficialAccountMenuDO);

        return sysOtherAppOfficialAccountMenuDO;

    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        removeByIds(idSet); // 根据 idSet删除

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

        List<SysOtherAppOfficialAccountMenuDO> sysOtherAppOfficialAccountMenuDOList =
                lambdaQuery().in(BaseEntity::getId, dto.getIdSet()).select(BaseEntity::getId, BaseEntityTree::getOrderNo)
                        .list();

        for (SysOtherAppOfficialAccountMenuDO item : sysOtherAppOfficialAccountMenuDOList) {
            item.setOrderNo((int) (item.getOrderNo() + dto.getNumber()));
        }

        updateBatchById(sysOtherAppOfficialAccountMenuDOList);

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
