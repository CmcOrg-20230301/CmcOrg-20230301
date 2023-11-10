import {Rule} from "antd/lib/form"

/**
 * 校验
 */
function Validator(rule: Rule, fieldValue: string, iValidateHandle: IValidateHandle) {

    if (!fieldValue) {
        return Promise.reject(new Error(iValidateHandle.emptyErrorMsg))
    }

    if (!iValidateHandle.validate(fieldValue)) {
        return Promise.reject(new Error(iValidateHandle.errorMsg))
    }

    return Promise.resolve()

}

/**
 * 校验：可以为空
 */
function CanNullValidator(rule: Rule, fieldValue: string, iValidateHandle: IValidateHandle) {

    if (!fieldValue) {
        return Promise.resolve()
    }

    if (!iValidateHandle.validate(fieldValue)) {
        return Promise.reject(new Error(iValidateHandle.errorMsg))
    }

    return Promise.resolve()

}

interface IValidateHandle {

    regex: RegExp
    emptyErrorMsg: string
    errorMsg: string
    validate: (value: string) => boolean
    validator: (rule: Rule, fieldValue: string) => Promise<void>
    canNullValidator: (rule: Rule, fieldValue: string) => Promise<void>

}

export interface IValidate {

    integer: IValidateHandle,
    email: IValidateHandle
    password: IValidateHandle
    code: IValidateHandle
    nickname: IValidateHandle
    phone: IValidateHandle
    signInName: IValidateHandle
    url: IValidateHandle
    bankDebitCard: IValidateHandle

}

export const Validate: IValidate = {

    integer: {
        regex: /^-?\d+$/,
        emptyErrorMsg: '请输入数字',
        errorMsg: '请输入数字',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.integer)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.integer)
        },
    },

    email: {
        regex: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
        emptyErrorMsg: '请输入邮箱',
        errorMsg: '邮箱格式错误',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.email)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.email)
        },
    },

    password: {
        regex: /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$/,
        emptyErrorMsg: '请输入密码',
        errorMsg: '密码格式错误：必须包含大小写字母和数字，可以使用特殊字符，长度8-20',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.password)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.password)
        },
    },

    code: {
        regex: /^[0-9]{6}$/,
        emptyErrorMsg: '请输入验证码',
        errorMsg: '验证码格式错误',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.code)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.code)
        },
    },

    nickname: {
        regex: /^[\u4E00-\u9FA5A-Za-z0-9_-]{1,20}$/,
        emptyErrorMsg: '请输入昵称',
        errorMsg: '昵称格式错误：只能包含中文，数字，字母，下划线，横杠，长度1-20',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.nickname)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.nickname)
        },
    },

    phone: {
        regex: /^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/,
        emptyErrorMsg: '请输入手机号',
        errorMsg: '手机号格式错误',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.phone)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.phone)
        },
    },

    signInName: {
        regex: /^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$/,
        emptyErrorMsg: '请输入登录名',
        errorMsg: '登录名格式错误：只能包含中文，数字，字母，下划线，横杠，长度2-20',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.signInName)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.signInName)
        },
    },

    url: {
        regex: /^[a-zA-Z]+:\/\/[\w-+&@#/%?=~_|!:,.;]*[\w-+&@#/%=~_|]$/,
        emptyErrorMsg: '请输入地址',
        errorMsg: '地址格式错误',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.url)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.url)
        },
    },

    bankDebitCard: {
        regex: /^(\d{16}|\d{19}|\d{17})$/,
        emptyErrorMsg: '请输入银行卡号',
        errorMsg: '银行卡号格式错误',
        validate(value: string) {
            return this.regex.test(value)
        },
        validator: (rule: Rule, fieldValue: string,) => {
            return Validator(rule, fieldValue, Validate.bankDebitCard)
        },
        canNullValidator: (rule: Rule, fieldValue: string,) => {
            return CanNullValidator(rule, fieldValue, Validate.bankDebitCard)
        },
    }

}
