package com.cmcorg20230301.be.engine.other.app.wx.service;

import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxVerifyDTO;

import javax.servlet.http.HttpServletRequest;

public interface SysOtherAppOfficialAccountWxService {

    String verify(SysOtherAppOfficialAccountWxVerifyDTO dto);

    String receiveMessage(HttpServletRequest request);

}
