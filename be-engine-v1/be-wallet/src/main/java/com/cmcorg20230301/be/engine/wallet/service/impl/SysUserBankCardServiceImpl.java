package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictStringVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserBankCardMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysOpenBankNameEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysUserBankCardService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysUserBankCardServiceImpl extends ServiceImpl<SysUserBankCardMapper, SysUserBankCardDO>
    implements SysUserBankCardService {

    /**
     * 新增/修改-用户
     */
    @Override
    public String insertOrUpdateUserSelf(SysUserBankCardInsertOrUpdateUserSelfDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        boolean exists = lambdaQuery().eq(SysUserBankCardDO::getId, currentUserId).exists();

        dto.setId(currentUserId);

        SysUserBankCardDO sysUserBankCardDO = new SysUserBankCardDO();

        sysUserBankCardDO.setId(dto.getId());
        sysUserBankCardDO.setBankCardNo(dto.getBankCardNo());
        sysUserBankCardDO.setOpenBankName(dto.getOpenBankName());
        sysUserBankCardDO.setBranchBankName(dto.getBranchBankName());
        sysUserBankCardDO.setPayeeName(dto.getPayeeName());
        sysUserBankCardDO.setEnableFlag(true);
        sysUserBankCardDO.setDelFlag(false);
        sysUserBankCardDO.setRemark("");

        if (exists) { // 如果存在：则是修改

            updateById(sysUserBankCardDO);

        } else { // 如果不存在：则是新增

            save(sysUserBankCardDO);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        Page<SysUserBankCardDO> page = lambdaQuery().eq(dto.getId() != null, SysUserBankCardDO::getId, dto.getId())
            .like(StrUtil.isNotBlank(dto.getBankCardNo()), SysUserBankCardDO::getBankCardNo, dto.getBankCardNo())
            .eq(dto.getOpenBankName() != null, SysUserBankCardDO::getOpenBankName, dto.getOpenBankName())
            .like(StrUtil.isNotBlank(dto.getBranchBankName()), SysUserBankCardDO::getBranchBankName,
                dto.getBranchBankName())
            .like(StrUtil.isNotBlank(dto.getPayeeName()), SysUserBankCardDO::getPayeeName, dto.getPayeeName())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(BaseEntityNoIdFather::getUpdateTime).page(dto.page(true));

        for (SysUserBankCardDO item : page.getRecords()) {

            // 脱敏：SysUserBankCardDO
            desensitizedSysUserBankCardDO(item);

        }

        return page;

    }

    /**
     * 脱敏：SysUserBankCardDO
     */
    private void desensitizedSysUserBankCardDO(SysUserBankCardDO sysUserBankCardDO) {

        if (sysUserBankCardDO == null) {
            return;
        }

        // 备注：需要和：提现记录的脱敏一致
        sysUserBankCardDO
            .setBankCardNo(StrUtil.cleanBlank(DesensitizedUtil.bankCard(sysUserBankCardDO.getBankCardNo()))); // 脱敏

        sysUserBankCardDO.setPayeeName(DesensitizedUtil.chineseName(sysUserBankCardDO.getPayeeName())); // 脱敏

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
    public SysUserBankCardDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysUserBankCardDO sysUserBankCardDO = lambdaQuery().eq(SysUserBankCardDO::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

        // 脱敏：SysUserBankCardDO
        desensitizedSysUserBankCardDO(sysUserBankCardDO);

        return sysUserBankCardDO;

    }

    /**
     * 通过主键id，查看详情-用户
     */
    @Override
    public SysUserBankCardDO infoByIdUserSelf() {

        Long currentUserId = UserUtil.getCurrentUserId();

        SysUserBankCardDO sysUserBankCardDO = lambdaQuery().eq(SysUserBankCardDO::getId, currentUserId).one();

        // 脱敏：SysUserBankCardDO
        desensitizedSysUserBankCardDO(sysUserBankCardDO);

        return sysUserBankCardDO;

    }

}
