package com.cmcorg20230301.engine.be.sign.email.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.sign.email.dto.*;

public interface SignEmailService {

    String signUpSendCode(EmailNotBlankDTO dto);

    String signUp(SignEmailSignUpDTO dto);

    String signInPassword(SignEmailSignInPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignEmailUpdatePasswordDTO dto);

    String updateAccountSendCode();

    String updateAccount(SignEmailUpdateAccountDTO dto);

    String forgetPasswordSendCode(EmailNotBlankDTO dto);

    String forgetPassword(SignEmailForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

    String bindAccountSendCode(EmailNotBlankDTO dto);

    String bindAccount(SignEmailBindAccountDTO dto);
}
