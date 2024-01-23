package com.cmcorg20230301.be.engine.sign.phone.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.sign.phone.model.dto.*;

public interface SignPhoneService {

    String signUpSendCode(PhoneNotBlankDTO dto);

    String signUp(SignPhoneSignUpDTO dto);

    SignInVO signInPassword(SignPhoneSignInPasswordDTO dto);

    String signInSendCode(PhoneNotBlankDTO dto);

    SignInVO signInCode(SignPhoneSignInCodeDTO dto);

    String setPasswordSendCode();

    String setPassword(SignPhoneSetPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignPhoneUpdatePasswordDTO dto);

    String setSignInNameSendCode(SignPhoneSetSignInNameSendCodeDTO dto);

    String setSignInName(SignPhoneSetSignInNameDTO dto);

    String updateSignInNameSendCode(SignPhoneUpdateSignInNameSendCodeDTO dto);

    String updateSignInName(SignPhoneUpdateSignInNameDTO dto);

    String setEmailSendCodePhone(SignPhoneSetEmailSendCodePhoneDTO dto);

    String setEmailSendCodeEmail(SignPhoneSetEmailSendCodeEmailDTO dto);

    String setEmail(SignPhoneSetEmailDTO dto);

    String updateEmailSendCodePhone(SignPhoneUpdateEmailSendCodePhoneDTO dto);

    String updateEmailSendCodeEmail(SignPhoneUpdateEmailSendCodeEmailDTO dto);

    String updateEmail(SignPhoneUpdateEmailDTO dto);

    String setWxSendCodePhone();

    GetQrCodeVO setWxGetQrCodeUrl();

    SysQrCodeSceneBindVO setWxGetQrCodeSceneFlag(NotNullId notNullId);

    SysQrCodeSceneBindVO setWx(SignPhoneSetWxDTO dto);

    String updateWxSendCodePhone();

    GetQrCodeVO updateWxGetQrCodeUrlNew();

    SysQrCodeSceneBindVO updateWxGetQrCodeSceneFlagNew(NotNullId notNullId);

    SysQrCodeSceneBindVO updateWx(SignPhoneUpdateWxDTO dto);

    String updatePhoneSendCodeNew(SignPhoneUpdatePhoneSendCodeNewDTO dto);

    String updatePhoneSendCodeOld();

    String updatePhone(SignPhoneUpdatePhoneDTO dto);

    String setSingleSignInSendCodePhone();

    GetQrCodeVO setSingleSignInGetQrCodeUrlSingleSignIn();

    SysQrCodeSceneBindVO setSingleSignInGetQrCodeSceneFlagSingleSignIn(NotNullId notNullId);

    SysQrCodeSceneBindVO setSingleSignIn(SignPhoneSetSingleSignInDTO dto);

    String forgetPasswordSendCode(PhoneNotBlankDTO dto);

    String forgetPassword(SignPhoneForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

}
