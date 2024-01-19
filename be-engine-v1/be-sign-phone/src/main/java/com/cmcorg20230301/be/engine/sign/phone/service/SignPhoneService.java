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

    String setSignInNameSendCode();

    String setSignInName(SignPhoneSetSignInNameDTO dto);

    String updateSignInNameSendCode();

    String updateSignInName(SignPhoneUpdateSignInNameDTO dto);

    String setEmailSendCodePhone();

    String setEmailSendCodeEmail();

    String setEmail(SignPhoneSetEmailDTO dto);

    String updateEmailSendCodePhone();

    String updateEmailSendCodeEmail();

    String updateEmail(SignPhoneUpdateEmailDTO dto);

    String setWxSendCodePhone();

    GetQrCodeVO setWxGetQrCodeUrl(SignPhoneSetWxGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO setWx(NotNullId notNullId);

    GetQrCodeVO updateWxSendCode(SignPhoneUpdateWxSendCodeDTO dto);

    GetQrCodeVO updateWxGetQrCodeUrlNew(SignPhoneUpdateWxGetQrCodeUrlNewDTO dto);

    SysQrCodeSceneBindVO updateWx(SignPhoneUpdateWxDTO dto);

    String updatePhoneSendCodeNew();

    String updatePhoneSendCodeOld();

    String updatePhone(SignPhoneUpdatePhoneDTO dto);

    String forgetPasswordSendCode(PhoneNotBlankDTO dto);

    String forgetPassword(SignPhoneForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

}
