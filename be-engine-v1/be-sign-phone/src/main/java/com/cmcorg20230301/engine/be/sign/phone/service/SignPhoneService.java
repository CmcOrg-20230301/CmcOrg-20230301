package com.cmcorg20230301.engine.be.sign.phone.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.*;

public interface SignPhoneService {

    String signUpSendCode(PhoneNotBlankDTO dto);

    String signUp(SignPhoneSignUpDTO dto);

    String signInPassword(SignPhoneSignInPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignPhoneUpdatePasswordDTO dto);

    String updateAccountSendCode();

    String updateAccount(SignPhoneUpdateAccountDTO dto);

    String forgetPasswordSendCode(PhoneNotBlankDTO dto);

    String forgetPassword(SignPhoneForgetPasswordDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

    String bindAccountSendCode(PhoneNotBlankDTO dto);

    String bindAccount(SignPhoneBindAccountDTO dto);

    String signInSendCode(PhoneNotBlankDTO dto);

    String signInCode(SignPhoneSignInCodeDTO dto);

}
