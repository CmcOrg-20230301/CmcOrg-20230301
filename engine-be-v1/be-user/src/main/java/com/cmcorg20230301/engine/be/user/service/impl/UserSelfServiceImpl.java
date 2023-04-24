package com.cmcorg20230301.engine.be.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import com.cmcorg20230301.engine.be.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.engine.be.user.model.vo.UserSelfInfoVO;
import com.cmcorg20230301.engine.be.user.service.UserSelfService;
import com.cmcorg20230301.engine.be.util.util.NicknameUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserSelfServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements UserSelfService {

    @Resource
    SecurityProperties securityProperties;
    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    /**
     * 获取：当前用户，基本信息
     */
    @Override
    public UserSelfInfoVO userSelfInfo() {

        Long currentUserId = UserUtil.getCurrentUserId();

        UserSelfInfoVO sysUserSelfInfoVO = new UserSelfInfoVO();

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) {

            sysUserSelfInfoVO.setAvatarFileId(BaseConstant.SYS_ID);
            sysUserSelfInfoVO.setNickname(securityProperties.getAdminNickname());
            sysUserSelfInfoVO.setBio("");
            sysUserSelfInfoVO.setEmail("");
            sysUserSelfInfoVO.setPasswordFlag(true);

            return sysUserSelfInfoVO;

        }

        SysUserInfoDO sysUserInfoDO =
            ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, currentUserId)
                .select(SysUserInfoDO::getAvatarFileId, SysUserInfoDO::getNickname, SysUserInfoDO::getBio).one();

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntity::getId, currentUserId)
            .select(SysUserDO::getEmail, SysUserDO::getPassword, SysUserDO::getSignInName, SysUserDO::getPhone,
                SysUserDO::getWxOpenId, BaseEntity::getCreateTime).one();

        if (sysUserInfoDO != null && sysUserDO != null) {

            sysUserSelfInfoVO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());
            sysUserSelfInfoVO.setNickname(sysUserInfoDO.getNickname());
            sysUserSelfInfoVO.setBio(sysUserInfoDO.getBio());

            sysUserSelfInfoVO.setEmail(DesensitizedUtil.email(sysUserDO.getEmail())); // 脱敏

            sysUserSelfInfoVO.setSignInName(DesensitizedUtil.chineseName(sysUserDO.getSignInName())); // 脱敏

            sysUserSelfInfoVO.setPhone(DesensitizedUtil.mobilePhone(sysUserDO.getPhone())); // 脱敏

            sysUserSelfInfoVO.setWxOpenId(DesensitizedUtil.mobilePhone(
                StrUtil.hide(sysUserDO.getWxOpenId(), 3, sysUserDO.getWxOpenId().length() - 4))); // 脱敏：只显示前 3位，后 4位

            sysUserSelfInfoVO.setPasswordFlag(StrUtil.isNotBlank(sysUserDO.getPassword()));
            sysUserSelfInfoVO.setCreateTime(sysUserDO.getCreateTime());

        }

        return sysUserSelfInfoVO;

    }

    /**
     * 当前用户：基本信息：修改
     */
    @Override
    public String userSelfUpdateInfo(UserSelfUpdateInfoDTO dto) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
        sysUserInfoDO.setId(currentUserIdNotAdmin);
        sysUserInfoDO.setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), NicknameUtil.getRandomNickname()));
        sysUserInfoDO.setBio(MyEntityUtil.getNotNullAndTrimStr(dto.getBio()));

        sysUserInfoMapper.updateById(sysUserInfoDO);

        return BaseBizCodeEnum.OK;

    }

}
