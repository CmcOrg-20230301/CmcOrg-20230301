package com.cmcorg20230301.be.engine.sign.email.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;

public interface SignEmailService {

    String signUpSendCode(EmailNotBlankDTO dto);

    String signUp(SignEmailSignUpDTO dto);

    SignInVO signInPassword(SignEmailSignInPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignEmailUpdatePasswordDTO dto);

    String setSignInNameSendCode(SignEmailSetSignInNameSendCodeDTO dto);

    String setSignInName(SignEmailSetSignInNameDTO dto);

    String updateSignInNameSendCode(SignEmailUpdateSignInNameSendCodeDTO dto);

    String updateSignInName(SignEmailUpdateSignInNameDTO dto);

    String updateEmailSendCodeNew(SignEmailUpdateEmailSendCodeNewDTO dto);

    String updateEmailSendCodeOld();

    String updateEmail(SignEmailUpdateEmailDTO dto);

    String setWxSendCode();

    GetQrCodeVO setWxGetQrCodeUrl();

    SysQrCodeSceneBindVO setWx(SignEmailSetWxDTO dto);

    String setPhoneSendCodeEmail();

    String setPhoneSendCodePhone(SignEmailSetPhoneSendCodePhoneDTO dto);

    String setPhone(SignEmailSetPhoneDTO dto);

    String forgetPasswordSendCode(EmailNotBlankDTO dto);

    String forgetPassword(SignEmailForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

}
