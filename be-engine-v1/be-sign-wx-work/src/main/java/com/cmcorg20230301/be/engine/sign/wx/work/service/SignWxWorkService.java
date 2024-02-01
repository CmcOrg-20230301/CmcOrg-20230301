package com.cmcorg20230301.be.engine.sign.wx.work.service;

import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.SignInBrowserCodeDTO;

public interface SignWxWorkService {

    SignInVO signInBrowserCode(SignInBrowserCodeDTO dto);

}
