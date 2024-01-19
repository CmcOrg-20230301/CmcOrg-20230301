import {IEnum} from "@/model/enum/CommonEnum.ts";
import {SysTenantConfigurationByIdVO} from "@/api/http/SysTenant.ts";
import {ReactNode} from "react";
import {MailOutlined, MobileOutlined, UserOutlined} from "@ant-design/icons";
import {Rule} from "antd/lib/form";
import {Validate} from "@/util/ValidatorUtil.ts";

export interface ISysSignTypeItemEnum extends IEnum<string> {

    showFlag: (sysTenantConfigurationByIdVO: SysTenantConfigurationByIdVO, signType: TSignType) => boolean

    noSignInBtnFlag?: boolean // 是否不展示：登录按钮，默认：false

    noSignUpLinkFlag?: boolean // 是否不展示：注册链接，默认：false

    noForgetPasswordFlag?: boolean // 是否不展示：忘记密码，默认：false

    signInAddAccountFlag?: boolean // 登录的时候，是否，统一在账户密码处登录，默认：false

    placeholder: string // 显示在输入框的 placeholder

    prefix: ReactNode // 在输入框的图标

    validator?: (rule: Rule, fieldValue: string) => Promise<void>

}

export type TSignType = 1 | 2 // 1 登录 2 注册

export interface ISysSignTypeEnum {

    SignInName: ISysSignTypeItemEnum,

    Email: ISysSignTypeItemEnum,

    Phone: ISysSignTypeItemEnum,

    WxQrCode: ISysSignTypeItemEnum,

}

export const SysSignTypeEnum: ISysSignTypeEnum = {

    SignInName: {
        code: '101',
        name: '账号密码登录',
        signInAddAccountFlag: true,
        placeholder: '登录名',
        prefix: <UserOutlined/>,
        validator: Validate.signInName.validator,
        showFlag: (sysTenantConfigurationByIdVO: SysTenantConfigurationByIdVO, signType: TSignType): boolean => {

            return sysTenantConfigurationByIdVO.signInNameSignUpEnable === true

        }
    },

    Email: {
        code: '201',
        name: '邮箱登录',
        signInAddAccountFlag: true,
        placeholder: '邮箱',
        prefix: <MailOutlined/>,
        validator: Validate.email.validator,
        showFlag: (sysTenantConfigurationByIdVO: SysTenantConfigurationByIdVO, signType: TSignType): boolean => {

            return sysTenantConfigurationByIdVO.emailSignUpEnable === true

        }
    },

    Phone: {
        code: '301',
        name: '手机登录',
        placeholder: '手机号',
        prefix: <MobileOutlined/>,
        noSignUpLinkFlag: true,
        validator: Validate.phone.validator,
        showFlag: (sysTenantConfigurationByIdVO: SysTenantConfigurationByIdVO, signType: TSignType): boolean => {

            return sysTenantConfigurationByIdVO.phoneSignUpEnable === true

        }
    },

    WxQrCode: {
        code: '401',
        name: '微信登录',
        placeholder: '微信',
        prefix: null,
        noSignInBtnFlag: true,
        noSignUpLinkFlag: true,
        noForgetPasswordFlag: true,
        showFlag: (sysTenantConfigurationByIdVO: SysTenantConfigurationByIdVO, signType: TSignType): boolean => {

            if (signType === 2) {
                return false;
            }

            return Boolean(sysTenantConfigurationByIdVO.wxQrCodeSignUp)

        }
    },

}

export const SysSignTypeEnumMap = new Map<string, ISysSignTypeItemEnum>();

Object.keys(SysSignTypeEnum).forEach(key => {

    const item = SysSignTypeEnum[key] as ISysSignTypeItemEnum;

    SysSignTypeEnumMap.set(item.code!, item)

})
