package com.cmcorg20230301.engine.be.sign.wx.service;

import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInPhoneCodeDTO;

public interface SignWxService {

    String signInPhoneCode(SignInPhoneCodeDTO dto);

    String signInCode(SignInCodeDTO dto);

}
