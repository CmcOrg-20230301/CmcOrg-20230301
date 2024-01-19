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

    String setPassword(SignWxSetPasswordDTO dto);

    GetQrCodeVO updatePasswordGetQrCodeUrl();

    String updatePassword(SignWxUpdatePasswordDTO dto);

    GetQrCodeVO setSignInNameGetQrCodeUrl();

    String setSignInName(SignWxSetSignInNameDTO dto);

    GetQrCodeVO updateSignInNameGetQrCodeUrl();

    String updateSignInName(SignWxUpdateSignInNameDTO dto);

    String setEmailSendCode(SignWxSetEmailSendCodeDTO dto);

    GetQrCodeVO setEmailGetQrCodeUrl();

    String setEmail(SignWxSetEmailDTO dto);

    String updateEmailSendCode(SignWxUpdateEmailSendCodeDTO dto);

    GetQrCodeVO updateEmailGetQrCodeUrl();

    String updateEmail(SignWxUpdateEmailDTO dto);

    GetQrCodeVO updateWxGetQrCodeUrlOld();

    GetQrCodeVO updateWxGetQrCodeUrlNew(SignWxUpdateWxGetQrCodeUrlNewDTO dto);

    SysQrCodeSceneBindVO updateWx(SignWxUpdateWxDTO dto);

    String setPhoneSendCode(SignWxSetPhoneSendCodeDTO dto);

    GetQrCodeVO setPhoneGetQrCodeUrl(SignWxSetPhoneGetQrCodeUrlDTO dto);

    String setPhone(SignWxSetPhoneDTO dto);

    GetQrCodeVO forgetPasswordGetQrCodeUrl(SignWxForgetPasswordGetQrCodeUrlDTO dto);

    String forgetPassword(SignWxForgetPasswordDTO dto);

    GetQrCodeVO signDeleteGetQrCodeUrl();

    String signDelete(SignWxSignDeleteDTO dto);

}
