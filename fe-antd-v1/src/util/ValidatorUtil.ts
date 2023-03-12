import {Rule} from "antd/lib/form"

interface IValidate {

    errorMsg: string
    validate: (value: string) => boolean
    emptyErrorMsg: string

}

const validate: Record<string, IValidate> = {

    email: {
        emptyErrorMsg: '请输入邮箱',
        errorMsg: '邮箱格式错误',
        validate(value: string) {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value)
        },
    },

    password: {
        emptyErrorMsg: '请输入密码',
        errorMsg:
            '密码格式错误：必须包含大小写字母和数字，可以使用特殊字符，长度8-20',
        validate(value: string) {
            return /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$/.test(value)
        },
    },

    code: {
        emptyErrorMsg: '请输入验证码',
        errorMsg: '验证码格式错误',
        validate(value: string) {
            return /^[0-9]{6}$/.test(value)
        },
    },

    nickname: {
        emptyErrorMsg: '请输入昵称',
        errorMsg: '昵称格式错误：只能包含中文，数字，字母，下划线，横杠，长度2-20',
        validate(value: string) {
            return /^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$/.test(value)
        },
    },

    phone: {
        emptyErrorMsg: '请输入手机号',
        errorMsg: '手机号格式错误',
        validate(value: string) {
            return /^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/.test(
                value
            )
        },
    },

    signInName: {
        emptyErrorMsg: '请输入登录名',
        errorMsg: '登录名格式错误：只能包含中文，数字，字母，下划线，横杠，长度2-20',
        validate(value: string) {
            return /^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$/.test(value)
        },
    }

}

export const ValidatorUtil: Record<string,
    (rule: Rule, fieldValue: string) => Promise<void>> = {}

Object.keys(validate).forEach((item) => {

    ValidatorUtil[item + 'Validate'] = (
        rule: Rule,
        fieldValue: string,
    ): Promise<void> => {

        if (!fieldValue) {

            return Promise.reject(new Error(validate[item].emptyErrorMsg))

        }

        if (!validate[item].validate(fieldValue)) {

            return Promise.reject(new Error(validate[item].errorMsg))

        }

        return Promise.resolve()

    }

    // 可以为空的校验
    ValidatorUtil[item + 'CanNullValidate'] = (
        rule: Rule,
        fieldValue: string,
    ): Promise<void> => {

        if (!fieldValue) {

            return Promise.resolve()

        }

        if (!validate[item].validate(fieldValue)) {

            return Promise.reject(new Error(validate[item].errorMsg))

        }

        return Promise.resolve()

    }

})
