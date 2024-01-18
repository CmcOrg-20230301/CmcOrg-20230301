package com.cmcorg20230301.be.engine.sign.phone.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.sign.phone.model.dto.*;

public interface SignPhoneService {

    String signUpSendCode(PhoneNotBlankDTO dto);

    String signUp(SignPhoneSignUpDTO dto);

    SignInVO signInPassword(SignPhoneSignInPasswordDTO dto);

    String updatePasswordSendCode();

    String updatePassword(SignPhoneUpdatePasswordDTO dto);

    String updatePhoneSendCode();

    String updatePhone(SignPhoneUpdatePhoneDTO dto);

    String forgetPasswordSendCode(PhoneNotBlankDTO dto);

    String forgetPassword(SignPhoneForgetPasswordDTO dto);

    String signInSendCode(PhoneNotBlankDTO dto);

    SignInVO signInCode(SignPhoneSignInCodeDTO dto);

    String signDeleteSendCode();

    String signDelete(NotBlankCodeDTO dto);

}
