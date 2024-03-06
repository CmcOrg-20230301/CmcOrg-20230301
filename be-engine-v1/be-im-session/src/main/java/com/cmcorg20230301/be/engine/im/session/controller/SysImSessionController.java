package com.cmcorg20230301.be.engine.im.session.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionQueryCustomerSessionIdUserSelfDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionSelfPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/im/session")
@Tag(name = "基础-即时通讯-会话-管理")
public class SysImSessionController {

    @Resource
    SysImSessionService baseService;

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysImSession:page')")
    public ApiResultVO<Page<SysImSessionDO>> myPage(@RequestBody @Valid SysImSessionPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto, true));
    }

    @Operation(summary = "查询：用户自我，所属客服会话的主键 id")
    @PostMapping("/query/customer/sessionId/userSelf")
    public ApiResultVO<Long> queryCustomerSessionIdUserSelf(
        @RequestBody @Valid SysImSessionQueryCustomerSessionIdUserSelfDTO dto) {
        return ApiResultVO.okData(baseService.queryCustomerSessionIdUserSelf(dto));
    }

    @Operation(summary = "分页排序查询-会话列表-自我")
    @PostMapping("/page/self")
    public ApiResultVO<Page<SysImSessionDO>> myPageSelf(
        @RequestBody @Valid SysImSessionSelfPageDTO dto) {
        return ApiResultVO.okData(baseService.myPageSelf(dto));
    }

}
