package com.cmcorg20230301.be.engine.user.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserSingleSignInMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserSingleSignInDO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.be.engine.user.model.vo.UserSelfInfoVO;
import com.cmcorg20230301.be.engine.user.service.UserSelfService;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Service
public class UserSelfServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements UserSelfService {

    @Resource
    SecurityProperties securityProperties;

    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    @Resource
    SysUserSingleSignInMapper sysUserSingleSignInMapper;

    /**
     * 获取：当前用户，基本信息
     */
    @SneakyThrows
    @Override
    public UserSelfInfoVO userSelfInfo() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        UserSelfInfoVO sysUserSelfInfoVO = new UserSelfInfoVO();

        sysUserSelfInfoVO.setId(currentUserId);

        sysUserSelfInfoVO.setTenantId(currentTenantIdDefault);

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {

            sysUserSelfInfoVO.setAvatarFileId(BaseConstant.SYS_ID);
            sysUserSelfInfoVO.setNickname(securityProperties.getAdminNickname());
            sysUserSelfInfoVO.setBio("");
            sysUserSelfInfoVO.setEmail("");
            sysUserSelfInfoVO.setPasswordFlag(true);

            return sysUserSelfInfoVO;

        }

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(3);

        MyThreadUtil.execute(() -> {

            SysUserInfoDO sysUserInfoDO =
                    ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, currentUserId)
                            .select(SysUserInfoDO::getAvatarFileId, SysUserInfoDO::getNickname, SysUserInfoDO::getBio).one();

            if (sysUserInfoDO != null) {

                sysUserSelfInfoVO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());
                sysUserSelfInfoVO.setNickname(sysUserInfoDO.getNickname());
                sysUserSelfInfoVO.setBio(sysUserInfoDO.getBio());

            }

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            SysUserDO sysUserDO = lambdaQuery().eq(BaseEntity::getId, currentUserId)
                    .select(SysUserDO::getEmail, SysUserDO::getPassword, SysUserDO::getSignInName, SysUserDO::getPhone,
                            SysUserDO::getWxOpenId, BaseEntity::getCreateTime, SysUserDO::getWxAppId).one();

            if (sysUserDO != null) {

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

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            SysUserSingleSignInDO sysUserSingleSignInDO = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper).eq(SysUserSingleSignInDO::getId, currentUserId).eq(SysUserSingleSignInDO::getTenantId, currentTenantIdDefault).select(SysUserSingleSignInDO::getWxOpenId, SysUserSingleSignInDO::getPhone, SysUserSingleSignInDO::getEmail).one();

            sysUserSelfInfoVO.setSingleSignInWxFlag(false);
            sysUserSelfInfoVO.setSingleSignInPhoneFlag(false);

            if (sysUserSingleSignInDO != null) {

                if (StrUtil.isNotBlank(sysUserSingleSignInDO.getWxOpenId())) {
                    sysUserSelfInfoVO.setSingleSignInWxFlag(true);
                }

                if (StrUtil.isNotBlank(sysUserSelfInfoVO.getPhone())) {
                    sysUserSelfInfoVO.setSingleSignInPhoneFlag(true);
                }

            }

        }, countDownLatch);

        countDownLatch.await();

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
