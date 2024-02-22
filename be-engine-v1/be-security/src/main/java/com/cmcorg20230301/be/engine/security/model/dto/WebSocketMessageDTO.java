package com.cmcorg20230301.be.engine.security.model.dto;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import com.cmcorg20230301.be.engine.model.model.vo.IWebSocketUri;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class WebSocketMessageDTO<T> {

    @Schema(description = "路径")
    private String uri;

    @Nullable
    @Schema(description = "数据")
    private T data;

    @Schema(description = "响应代码，成功返回：200")
    private Integer code;

    @Schema(description = "响应描述")
    private String msg;

    @Schema(description = "服务名")
    private String service = BaseConfiguration.applicationName;

    public WebSocketMessageDTO(String uri) {
        this.uri = uri;
    }

    private WebSocketMessageDTO(String uri, Integer code, String msg, @Nullable T data) {

        this.uri = uri;
        this.msg = msg;
        this.code = code;
        this.data = data;

    }

    private void setService(String service) {
        // 不允许修改 service的值
    }

    /**
     * 系统异常
     */
    public static void sysError(IWebSocketUri iWebSocketUri) {
        error(iWebSocketUri, BaseBizCodeEnum.API_RESULT_SYS_ERROR);
    }

    /**
     * 操作失败
     */
    public static <T> WebSocketMessageDTO<T> error(IWebSocketUri iWebSocketUri, IBizCode iBizCode) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), iBizCode.getCode(), iBizCode.getMsg(), null);
    }

    public static <T> WebSocketMessageDTO<T> error(IWebSocketUri iWebSocketUri, IBizCode iBizCode, @Nullable T data) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), iBizCode.getCode(), iBizCode.getMsg(), data);
    }

    public static <T> WebSocketMessageDTO<T> error(IWebSocketUri iWebSocketUri, String msg, @Nullable T data) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(), msg, data);
    }

    public static <T> WebSocketMessageDTO<T> errorMsg(IWebSocketUri iWebSocketUri, String msgTemp, Object... paramArr) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(),
                StrUtil.format(msgTemp, paramArr), null);
    }

    public static <T> WebSocketMessageDTO<T> errorCode(String uri, Integer code) {
        return new WebSocketMessageDTO<>(uri, code, null, null);
    }

    /**
     * 操作成功
     */
    public static <T> WebSocketMessageDTO<T> ok(IWebSocketUri iWebSocketUri, String msg, @Nullable T data) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, data);
    }

    public static <T> WebSocketMessageDTO<T> okData(IWebSocketUri iWebSocketUri, @Nullable T data) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), BaseBizCodeEnum.API_RESULT_OK.getCode(),
                null, data);
    }

    public static <T> WebSocketMessageDTO<T> okMsg(IWebSocketUri iWebSocketUri, String msg) {
        return new WebSocketMessageDTO<>(iWebSocketUri.getUri(), BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, null);
    }

}
