package com.cmcorg20230301.be.engine.sign.signinname.service;

import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;

public interface SignSignInNameService {

    String signUp(SignSignInNameSignUpDTO dto);

    SignInVO signInPassword(SignSignInNameSignInPasswordDTO dto);

    String updatePassword(SignSignInNameUpdatePasswordDTO dto);

    String updateAccount(SignSignInNameUpdateAccountDTO dto);

    String signDelete(SignSignInNameSignDeleteDTO dto);

}
