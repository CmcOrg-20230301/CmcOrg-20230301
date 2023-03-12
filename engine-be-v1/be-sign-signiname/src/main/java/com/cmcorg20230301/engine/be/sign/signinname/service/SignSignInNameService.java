package com.cmcorg20230301.engine.be.sign.signinname.service;

import com.cmcorg20230301.engine.be.sign.signinname.dto.*;

public interface SignSignInNameService {

    String signUp(SignSignInNameSignUpDTO dto);

    String signInPassword(SignSignInNameSignInPasswordDTO dto);

    String updatePassword(SignSignInNameUpdatePasswordDTO dto);

    String updateAccount(SignSignInNameUpdateAccountDTO dto);

    String signDelete(SignSignInNameSignDeleteDTO dto);
}
