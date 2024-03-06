package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.model.model.vo.DictStringVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserBankCardMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysOpenBankNameEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserBankCardService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class SysUserBankCardServiceImpl extends
    ServiceImpl<SysUserBankCardMapper, SysUserBankCardDO>
    implements SysUserBankCardService {

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysUserBankCardInsertOrUpdateDTO dto) {

        Long userId = dto.getId();

        Set<Long> userIdSet = CollUtil.newHashSet(userId);

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(userIdSet,
            tenantIdSet -> ChainWrappers.lambdaQueryChain(sysUserMapper)
                .eq(BaseEntity::getId, userId)
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        // 执行
        return doInsertOrUpdate(dto, false, userId);

    }

    /**
     * 新增/修改-用户
     */
    @Override
    public String insertOrUpdateUserSelf(SysUserBankCardInsertOrUpdateUserSelfDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        dto.setTenantId(currentTenantIdDefault);

        // 执行
        return doInsertOrUpdate(dto, false, null);

    }

    /**
     * 执行：新增/修改
     */
    @Override
    public String doInsertOrUpdate(SysUserBankCardInsertOrUpdateUserSelfDTO dto, boolean tenantFlag,
        Long userId) {

        Long id;

        if (tenantFlag) {

            id = BaseConstant.TENANT_USER_ID;

        } else {

            if (userId == null) {

                id = UserUtil.getCurrentUserId();

            } else {

                id = userId;

            }

        }

        boolean exists =
            lambdaQuery().eq(SysUserBankCardDO::getId, id)
                .eq(BaseEntityNoIdSuper::getTenantId, dto.getTenantId())
                .exists();

        SysUserBankCardDO sysUserBankCardDO = new SysUserBankCardDO();

        sysUserBankCardDO.setId(id);
        sysUserBankCardDO.setBankCardNo(dto.getBankCardNo());
        sysUserBankCardDO.setOpenBankName(dto.getOpenBankName());
        sysUserBankCardDO.setBranchBankName(dto.getBranchBankName());

        if (exists) {

            if (!dto.getPayeeName().contains(BaseConstant.ASTERISK)) {
                sysUserBankCardDO.setPayeeName(dto.getPayeeName()); // 防止脱敏数据，存入数据库
            }

        } else {

            sysUserBankCardDO.setPayeeName(dto.getPayeeName());

        }

        sysUserBankCardDO.setEnableFlag(true);
        sysUserBankCardDO.setDelFlag(false);
        sysUserBankCardDO.setRemark("");

        if (exists) { // 如果存在：则是修改

            updateById(sysUserBankCardDO);

        } else { // 如果不存在：则是新增

            if (tenantFlag) {

                sysUserBankCardDO.setTenantId(dto.getTenantId());

            }

            save(sysUserBankCardDO);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto) {

        return doMyPage(dto, false);

    }

    /**
     * 执行：分页排序查询
     */
    @Override
    @NotNull
    public Page<SysUserBankCardDO> doMyPage(SysUserBankCardPageDTO dto, boolean tenantFlag) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        if (tenantFlag) {

            dto.setId(BaseConstant.TENANT_USER_ID);

        } else {

            if (BaseConstant.TENANT_USER_ID.equals(dto.getId())) {

                dto.setId(null);

            }

        }

        Page<SysUserBankCardDO> page = lambdaQuery().eq(dto.getId() != null,
                SysUserBankCardDO::getId, dto.getId())

            .like(StrUtil.isNotBlank(dto.getBankCardNo()), SysUserBankCardDO::getBankCardNo,
                dto.getBankCardNo())

            .eq(dto.getOpenBankName() != null, SysUserBankCardDO::getOpenBankName,
                dto.getOpenBankName())

            .like(StrUtil.isNotBlank(dto.getBranchBankName()), SysUserBankCardDO::getBranchBankName,
                dto.getBranchBankName()) //

            .like(StrUtil.isNotBlank(dto.getPayeeName()), SysUserBankCardDO::getPayeeName,
                dto.getPayeeName()) //

            .ne(!tenantFlag, SysUserBankCardDO::getId, BaseConstant.TENANT_USER_ID) //

            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //

            .orderByDesc(BaseEntityNoIdSuper::getUpdateTime).page(dto.page(true));

        for (SysUserBankCardDO item : page.getRecords()) {

            // 脱敏：SysUserBankCardDO
            desensitizedSysUserBankCardDO(item);

        }

        return page;

    }

    /**
     * 脱敏：SysUserBankCardDO
     */
    public static void desensitizedSysUserBankCardDO(SysUserBankCardDO sysUserBankCardDO) {

        if (sysUserBankCardDO == null) {
            return;
        }

        // 备注：需要和：提现记录的脱敏一致
        sysUserBankCardDO
            .setBankCardNo(StrUtil.cleanBlank(
                DesensitizedUtil.bankCard(sysUserBankCardDO.getBankCardNo()))); // 脱敏

        sysUserBankCardDO.setPayeeName(
            DesensitizedUtil.chineseName(sysUserBankCardDO.getPayeeName())); // 脱敏

    }

    /**
     * 下拉列表-开户行名称
     */
    @Override
    public Page<DictStringVO> openBankNameDictList() {

        return new Page<DictStringVO>().setTotal(SysOpenBankNameEnum.DICT_VO_LIST.size())
            .setRecords(SysOpenBankNameEnum.DICT_VO_LIST);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserBankCardDO infoById(NotNullLong notNullLong) {

        if (notNullLong.getValue().equals(BaseConstant.TENANT_USER_ID)) {
            return null;
        }

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysUserBankCardDO::getId, notNullLong.getValue())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

    }

    /**
     * 通过主键id，查看详情-用户
     */
    @Override
    public SysUserBankCardDO infoByIdUserSelf() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysUserBankCardDO sysUserBankCardDO = lambdaQuery().eq(SysUserBankCardDO::getId,
                currentUserId)
            .eq(BaseEntityNoIdSuper::getTenantId, currentTenantIdDefault).one();

        // 脱敏：SysUserBankCardDO
        desensitizedSysUserBankCardDO(sysUserBankCardDO);

        return sysUserBankCardDO;

    }

}
