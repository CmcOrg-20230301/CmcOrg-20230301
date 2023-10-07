package com.cmcorg20230301.be.engine.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.be.engine.user.model.vo.UserSelfInfoVO;
import com.cmcorg20230301.be.engine.user.service.UserSelfService;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
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

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        UserSelfInfoVO sysUserSelfInfoVO = new UserSelfInfoVO();

        sysUserSelfInfoVO.setTenantId(currentTenantIdDefault);

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {

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
                SysUserDO::getWxOpenId, BaseEntity::getCreateTime, SysUserDO::getWxAppId).one();

        if (sysUserInfoDO != null && sysUserDO != null) {

            sysUserSelfInfoVO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());
            sysUserSelfInfoVO.setNickname(sysUserInfoDO.getNickname());
            sysUserSelfInfoVO.setBio(sysUserInfoDO.getBio());

            // 备注：要和 userMyPage接口保持一致
            sysUserSelfInfoVO.setEmail(DesensitizedUtil.email(sysUserDO.getEmail())); // 脱敏
            sysUserSelfInfoVO.setSignInName(DesensitizedUtil.chineseName(sysUserDO.getSignInName())); // 脱敏
            sysUserSelfInfoVO.setPhone(DesensitizedUtil.mobilePhone(sysUserDO.getPhone())); // 脱敏
            sysUserSelfInfoVO.setWxOpenId(
                StrUtil.hide(sysUserDO.getWxOpenId(), 3, sysUserDO.getWxOpenId().length() - 4)); // 脱敏：只显示前 3位，后 4位
            sysUserSelfInfoVO.setWxAppId(
                StrUtil.hide(sysUserDO.getWxAppId(), 3, sysUserDO.getWxAppId().length() - 4)); // 脱敏：只显示前 3位，后 4位

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

    /**
     * 当前用户：刷新jwt私钥后缀
     */
    @Override
    public String userSelfRefreshJwtSecretSuf() {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

        return BaseBizCodeEnum.OK;

    }

    /**
     * 当前用户：重置头像
     */
    @Override
    public String userSelfResetAvatar() {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, currentUserIdNotAdmin)
            .set(SysUserInfoDO::getAvatarFileId, -1).update();

        return BaseBizCodeEnum.OK;

    }

}
