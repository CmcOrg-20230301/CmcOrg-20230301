package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sys/im/session/content")
@Tag(name = "即时通讯-会话-内容-管理")
public class SysImSessionContentController {

    @Resource
    SysImSessionContentService baseService;

    @Operation(summary = "发送内容")
    @PostMapping("/send")
    public ApiResultVO<String> send(@RequestBody @Valid SysImSessionContentSendListDTO dtoList) {
        return ApiResultVO.okData(baseService.send(dtoList));
    }

    @Operation(summary = "查询会话内容")
    @PostMapping("/list")
    public ApiResultVO<List<SysImSessionContentDO>> myList(@RequestBody @Valid SysImSessionContentListDTO dto) {
        return ApiResultVO.okData(baseService.myList(dto));
    }

}
