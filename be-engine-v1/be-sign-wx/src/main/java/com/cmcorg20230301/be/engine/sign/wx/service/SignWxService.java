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

    SysQrCodeSceneBindVO setPassword(SignWxSetPasswordDTO dto);

    GetQrCodeVO updatePasswordGetQrCodeUrl();

    SysQrCodeSceneBindVO updatePassword(SignWxUpdatePasswordDTO dto);

    GetQrCodeVO setSignInNameGetQrCodeUrl(SignWxSetSignInNameGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO setSignInName(SignWxSetSignInNameDTO dto);

    GetQrCodeVO updateSignInNameGetQrCodeUrl(SignWxUpdateSignInNameGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO updateSignInName(SignWxUpdateSignInNameDTO dto);

    String setEmailSendCode(SignWxSetEmailSendCodeDTO dto);

    GetQrCodeVO setEmailGetQrCodeUrl(SignWxSetEmailGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO setEmail(SignWxSetEmailDTO dto);

    String updateEmailSendCode(SignWxUpdateEmailSendCodeDTO dto);

    GetQrCodeVO updateEmailGetQrCodeUrl(SignWxUpdateEmailGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO updateEmail(SignWxUpdateEmailDTO dto);

    GetQrCodeVO updateWxGetQrCodeUrlOld();

    GetQrCodeVO updateWxGetQrCodeUrlNew(SignWxUpdateWxGetQrCodeUrlNewDTO dto);

    SysQrCodeSceneBindVO updateWx(SignWxUpdateWxDTO dto);

    String setPhoneSendCode(SignWxSetPhoneSendCodeDTO dto);

    GetQrCodeVO setPhoneGetQrCodeUrl(SignWxSetPhoneGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO setPhone(SignWxSetPhoneDTO dto);

    GetQrCodeVO signDeleteGetQrCodeUrl();

    SysQrCodeSceneBindVO signDelete(SignWxSignDeleteDTO dto);

}
