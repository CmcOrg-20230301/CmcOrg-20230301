package com.cmcorg20230301.be.engine.sign.single.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysSignConfigurationVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserSingleSignInMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserSingleSignInDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SingleSignInProperties;
import com.cmcorg20230301.be.engine.security.util.CodeUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInCodePhoneDTO;
import com.cmcorg20230301.be.engine.sign.single.model.dto.SignSingleSignInSendCodePhoneDTO;
import com.cmcorg20230301.be.engine.sign.single.service.SignSingleService;
import com.cmcorg20230301.be.engine.sign.wx.model.enums.WxSysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsHelper;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignSingleServiceImpl implements SignSingleService {

    @Resource
    RedissonClient redissonClient;

    @Resource
    SingleSignInProperties singleSignInProperties;

    @Resource
    SysUserSingleSignInMapper sysUserSingleSignInMapper;

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 获取：统一登录相关的配置
     */
    @Override
    public SysSignConfigurationVO getSignInConfiguration() {

        SysSignConfigurationVO sysSignConfigurationVO = new SysSignConfigurationVO();

        sysSignConfigurationVO.setSignInNameSignUpEnable(false);
        sysSignConfigurationVO.setEmailSignUpEnable(singleSignInProperties.getEmailConfigurationId() != null);
        sysSignConfigurationVO.setPhoneSignUpEnable(singleSignInProperties.getSmsConfigurationId() != null);
        sysSignConfigurationVO.setWxQrCodeSignUp(singleSignInProperties.getWxSysOtherAppId() == null ? null : new GetQrCodeVO());

        return sysSignConfigurationVO;

    }

    /**
     * 统一登录：微信扫码登录：获取二维码
     */
    @Override
    public GetQrCodeVO signInGetQrCodeUrlWx(boolean getQrCodeUrlFlag) {

        // 执行
        return SignUtil.getQrCodeUrlWxForSingleSignIn(true, WxSysQrCodeSceneTypeEnum.WX_SINGLE_SIGN_IN);

    }

    /**
     * 统一登录：微信扫码登录：通过二维码 id
     */
    @Override
    public SignInVO signInByQrCodeIdWx(NotNullId notNullId) {

        return redissonClient.<SignInVO>getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN_IN_SINGLE.name() + notNullId.getId()).getAndDelete();

    }

    /**
     * 手机验证码登录-发送验证码
     */
    @Override
    public String signInSendCodePhone(SignSingleSignInSendCodePhoneDTO dto) {

        Long smsConfigurationId = singleSignInProperties.getSmsConfigurationId();

        if (smsConfigurationId == null) {
            ApiResultVO.errorMsg("操作失败：暂未配置手机验证码登录，请刷新重试");
        }

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        Long count = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper).eq(SysUserSingleSignInDO::getPhone, dto.getPhone()).count();

        if (count == 0) {

            ApiResultVO.errorMsg("操作失败：该手机号未设置统一登录，请在【个人中心-统一登录】处，进行设置后再试");

        } else if (count > 1) {

            ApiResultVO.error("操作失败：存在多个手机号，请联系管理员", dto.getPhone());

        }

        return SignUtil
                .sendCode(key, null, null,
                        BaseBizCodeEnum.API_RESULT_SYS_ERROR, (code) -> SysSmsUtil
                                .sendSignIn(SysSmsHelper.getSysSmsSendBO(code, dto.getPhone(), smsConfigurationId)), null);

    }

    /**
     * 手机验证码登录
     */
    @Override
    public SignInVO signInCodePhone(SignSingleSignInCodePhoneDTO dto) {

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            CodeUtil.checkCode(dto.getCode(), bucket.get()); // 检查 code是否正确

            bucket.delete(); // 删除：验证码

            // 获取：手机验证码统一登录的信息
            SysUserSingleSignInDO sysUserSingleSignInDO = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper).eq(SysUserSingleSignInDO::getPhone, dto.getPhone()).select(SysUserSingleSignInDO::getId, SysUserSingleSignInDO::getTenantId).one();

            if (sysUserSingleSignInDO == null) {

                ApiResultVO.errorMsg("操作失败：该手机号未设置统一登录，请在【个人中心-统一登录】处，进行设置后再试");

            }

            SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, sysUserSingleSignInDO.getId()).eq(BaseEntityNoIdSuper::getTenantId, sysUserSingleSignInDO.getTenantId()).one();

            // 返回登录数据
            return SignUtil.signInGetJwt(sysUserDO);

        });

    }

}
