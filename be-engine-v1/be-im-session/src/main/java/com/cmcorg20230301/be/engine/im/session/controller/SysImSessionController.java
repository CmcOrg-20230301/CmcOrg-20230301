package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/im/session")
@Tag(name = "基础-即时通讯-会话-管理")
public class SysImSessionController {

    @Resource
    SysImSessionService baseService;

//    @Operation(summary = "新增/修改")
//    @PostMapping("/insertOrUpdate")
//    @PreAuthorize("hasAuthority('sysImSession:insertOrUpdate')")
//    public ApiResultVO<Long> insertOrUpdate(@RequestBody @Valid SysImSessionInsertOrUpdateDTO dto) {
//        return ApiResultVO.okData(baseService.insertOrUpdate(dto));
//    }

//    @Operation(summary = "分页排序查询")
//    @PostMapping("/page")
//    @PreAuthorize("hasAuthority('sysImSession:page')")
//    public ApiResultVO<Page<SysImSessionDO>> myPage(@RequestBody @Valid SysImSessionPageDTO dto) {
//        return ApiResultVO.okData(baseService.myPage(dto,false));
//    }
//
//    @Operation(summary = "查询：用户自我，所属客服会话的主键 id")
//    @PostMapping("/query/customer/sessionId/userSelf")
//    public ApiResultVO<Long> queryCustomerSessionIdUserSelf(@RequestBody @Valid SysImSessionQueryCustomerSessionIdUserSelfDTO dto) {
//        return ApiResultVO.okData(baseService.queryCustomerSessionIdUserSelf(dto));
//    }

}
