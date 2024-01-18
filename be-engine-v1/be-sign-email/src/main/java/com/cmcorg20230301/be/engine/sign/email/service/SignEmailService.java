package com.cmcorg20230301.be.engine.sign.email.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;

public interface SignEmailService {

    String signUpSendCode(EmailNotBlankDTO dto);

    String signUp(SignEmailSignUpDTO dto);

    SignInVO signInPassword(SignEmailSignInPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignEmailUpdatePasswordDTO dto);

    String updateEmailSendCode();

    String updateEmail(SignEmailUpdateEmailDTO dto);

    String forgetPasswordSendCode(EmailNotBlankDTO dto);

    String forgetPassword(SignEmailForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

}
