package com.cmcorg20230301.be.engine.sign.wx.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.*;

public interface SignWxService {

    SignInVO signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto);

    SignInVO signInMiniProgramCode(SignInMiniProgramCodeDTO dto);

    SignInVO signInBrowserCode(SignInBrowserCodeDTO dto);

    SignInVO signInBrowserCodeUserInfo(SignInBrowserCodeDTO dto);

    GetQrCodeVO signInGetQrCodeUrl(UserSignBaseDTO dto, boolean getQrCodeUrlFlag);

    SignInVO signInByQrCodeId(NotNullId notNullId);

    GetQrCodeVO setPasswordGetQrCodeUrl();

    SysQrCodeSceneBindVO setPasswordGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO setPassword(SignWxSetPasswordDTO dto);

    GetQrCodeVO updatePasswordGetQrCodeUrl();

    SysQrCodeSceneBindVO updatePasswordGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO updatePassword(SignWxUpdatePasswordDTO dto);

    GetQrCodeVO setSignInNameGetQrCodeUrl();

    SysQrCodeSceneBindVO setSignInNameGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO setSignInName(SignWxSetSignInNameDTO dto);

    GetQrCodeVO updateSignInNameGetQrCodeUrl();

    SysQrCodeSceneBindVO updateSignInNameGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO updateSignInName(SignWxUpdateSignInNameDTO dto);

    String setEmailSendCode(SignWxSetEmailSendCodeDTO dto);

    GetQrCodeVO setEmailGetQrCodeUrl();

    SysQrCodeSceneBindVO setEmailGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO setEmail(SignWxSetEmailDTO dto);

    String updateEmailSendCode(SignWxUpdateEmailSendCodeDTO dto);

    GetQrCodeVO updateEmailGetQrCodeUrl();

    SysQrCodeSceneBindVO updateEmailGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO updateEmail(SignWxUpdateEmailDTO dto);

    GetQrCodeVO updateWxGetQrCodeUrlOld();

    SysQrCodeSceneBindVO updateWxGetQrCodeSceneFlagOld(NotNullId notNullId);

    GetQrCodeVO updateWxGetQrCodeUrlNew();

    SysQrCodeSceneBindVO updateWxGetQrCodeSceneFlagNew(NotNullId notNullId);

    SysQrCodeSceneBindVO updateWx(SignWxUpdateWxDTO dto);

    String setPhoneSendCode(SignWxSetPhoneSendCodeDTO dto);

    GetQrCodeVO setPhoneGetQrCodeUrl();

    SysQrCodeSceneBindVO setPhoneGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO setPhone(SignWxSetPhoneDTO dto);

    GetQrCodeVO setSingleSignInGetQrCodeUrlCurrent();

    SysQrCodeSceneBindVO setSingleSignInGetQrCodeSceneFlagCurrent(NotNullId notNullId);

    GetQrCodeVO updateWxGetQrCodeUrlSingleSignIn();

    SysQrCodeSceneBindVO setSingleSignInGetQrCodeSceneFlagSingleSignIn(NotNullId notNullId);

    SysQrCodeSceneBindVO setSingleSignIn(SignWxSetSingleSignInDTO dto);

    GetQrCodeVO signDeleteGetQrCodeUrl();

    SysQrCodeSceneBindVO signDeleteGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO signDelete(NotNullId notNullId);

}
