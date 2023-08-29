package com.cmcorg20230301.be.engine.security.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则：
 * 错误代码格式：300010 300021
 * 解释：前面5位：错误代码，最后一位：0，系统异常，1，业务异常
 * 注意：10001开头的留给 BaseBizCodeEnum类进行配置，建议用 20001开头配置一些公用异常，实际业务从 30001开头，开始使用
 * 备注：可以每个业务的错误代码配置类，使用相同的 错误代码，比如每个业务的错误代码配置类都从 30001开始，为什么呢，因为
 * ApiResultVO 任何的 error方法都会打印服务名，所有就不用关心是哪个服务报出异常，直接根据打印的服务名，找到对应的错误信息即可，以上。
 */
@AllArgsConstructor
@Getter
public enum BaseBizCodeEnum implements IBizCode {

    API_RESULT_OK(200, BaseBizCodeEnum.OK), //
    API_RESULT_SEND_OK(200, BaseBizCodeEnum.SEND_OK), //
    API_RESULT_SYS_ERROR(100010, "系统异常，请联系管理员"), //
    PARAMETER_CHECK_ERROR(100011, "参数校验出现问题"), //
    ILLEGAL_REQUEST(100021, "非法请求"), //
    PLEASE_DELETE_THE_CHILD_NODE_FIRST(100031, "请先删除子节点"), //
    INSUFFICIENT_PERMISSIONS(100041, "权限不足"), //
    NO_DATA_AFFECTED(100051, "操作失败：未有数据发生变化"), //
    ACCOUNT_IS_DISABLED(100061, "账户被冻结，请联系管理员"), //
    NOT_LOGGED_IN_YET(100111, "尚未登录，请先登录"), // 返回这个 code（100111），会触发前端，登出功能
    LOGIN_EXPIRED(100111, "登录过期，请重新登录"), //
    THE_ACCOUNT_IS_LOGGED_IN_ELSEWHERE(100111, "账号在其他地方登录，您被迫下线"), //
    PARENT_ID_CANNOT_BE_EQUAL_TO_ID(100121, "操作失败：parentId 不能等于 id"), //
    THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION(100131, "操作失败：admin 账号不支持此操作"), //
    OPERATION_TIMED_OUT_PLEASE_TRY_AGAIN(100161, "操作超时，请重新进行操作"), // 返回这个 code（100161），前端会在步骤表单，往前返回步骤
    UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND(100171, "操作失败：您还没有绑定邮箱，无法发送验证码"), //
    UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_PHONE_IS_NOT_BOUND(100181, "操作失败：您还没有绑定手机号码，无法发送验证码"), //

    ;

    private final int code;
    private final String msg;

    public static final String OK = "操作成功";
    public static final String SEND_OK = "发送成功";
    public static final String OK_ENGLISH = "ok";

}
