package com.cmcorg20230301.be.engine.sign.signinname.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.SysQrCodeSceneBindExistUserDTO;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;

public interface SignSignInNameService {

    String signUp(SignSignInNameSignUpDTO dto);

    SignInVO signInPassword(SignSignInNameSignInPasswordDTO dto);

    String updatePassword(SignSignInNameUpdatePasswordDTO dto);

    String updateSignInName(SignSignInNameUpdateSignInNameDTO dto);

    String setEmailSendCode(SignSignInNameSetEmailSendCodeDTO dto);

    String setEmail(SignSignInNameSetEmailDTO dto);

    GetQrCodeVO setWxGetQrCodeUrl(SignSignInNameSetWxGetQrCodeUrlDTO dto);

    SysQrCodeSceneBindVO setWx(NotNullId notNullId);

    String setWxExistUser(SysQrCodeSceneBindExistUserDTO dto);

    String setPhoneSendCode(SignSignInNameSetPhoneSendCodeDTO dto);

    String setPhone(SignSignInNameSetPhoneDTO dto);

    String signDelete(SignSignInNameSignDeleteDTO dto);

}
