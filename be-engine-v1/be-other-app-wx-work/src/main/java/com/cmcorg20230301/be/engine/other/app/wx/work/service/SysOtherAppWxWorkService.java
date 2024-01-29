package com.cmcorg20230301.be.engine.other.app.wx.work.service;

import com.cmcorg20230301.be.engine.other.app.wx.work.model.dto.SysOtherAppWxWorkVerifyDTO;

import javax.servlet.http.HttpServletRequest;

public interface SysOtherAppWxWorkService {

    String verify(SysOtherAppWxWorkVerifyDTO dto);

    void receiveMessage(HttpServletRequest request);

}
