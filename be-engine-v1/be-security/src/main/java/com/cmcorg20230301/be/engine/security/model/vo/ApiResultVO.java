package com.cmcorg20230301.be.engine.security.model.vo;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jetbrains.annotations.Contract;

@Data
@Schema(description = "统一响应实体类")
public class ApiResultVO<T> {

    @Schema(description = "响应代码，成功返回：200")
    private Integer code;

    @Schema(description = "响应描述")
    private String msg;

    @Schema(description = "服务器是否收到请求，只会返回 true")
    private Boolean successFlag;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "服务名")
    private String service = BaseConfiguration.applicationName;

    private ApiResultVO(Integer code, String msg, T data) {

        this.msg = msg;
        this.code = code;
        this.data = data;
        this.successFlag = true;

    }

    private void setSuccessFlag(boolean successFlag) {
        // 不允许修改 success的值
    }

    private void setService(String service) {
        // 不允许修改 service的值
    }

    /**
     * Contract注解，目的：让 IDEA知道这里会抛出异常
     */
    @Contract(" -> fail")
    public ApiResultVO<T> error() {
        throw new BaseException(this);
    }

    /**
     * 系统异常
     */
    public static void sysError() {
        error(BaseBizCodeEnum.API_RESULT_SYS_ERROR);
    }

    /**
     * 操作失败
     */
    @Contract("_ -> fail")
    public static <T> ApiResultVO<T> error(IBizCode iBizCode) {
        return new ApiResultVO<T>(iBizCode.getCode(), iBizCode.getMsg(), null).error();
    }

    @Contract("_,_ -> fail")
    public static <T> ApiResultVO<T> error(IBizCode iBizCode, T data) {
        return new ApiResultVO<>(iBizCode.getCode(), iBizCode.getMsg(), data).error();
    }

    @Contract("_,_ -> fail")
    public static <T> ApiResultVO<T> error(String msg, T data) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(), msg, data).error();
    }

    @Contract("_,_ -> fail")
    public static <T> ApiResultVO<T> errorMsg(String msgTemp, Object... paramArr) {
        return new ApiResultVO<T>(BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(), StrUtil.format(msgTemp, paramArr),
            null).error();
    }

    /**
     * 操作成功
     */
    public static <T> ApiResultVO<T> ok(String msg, T data) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, data);
    }

    public static <T> ApiResultVO<T> okData(T data) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), BaseBizCodeEnum.API_RESULT_OK.getMsg(), data);
    }

    public static <T> ApiResultVO<T> okMsg(String msg) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, null);
    }

}
