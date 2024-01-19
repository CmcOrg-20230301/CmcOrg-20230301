import {IEnum} from "@/model/enum/CommonEnum.ts";
import {UserSelfInfoVO} from "@/api/http/UserSelf.ts";

export interface ISysUserAccountLevelEnum {

    PHONE: IEnum<number>, // 手机（最高级）
    WX: IEnum<number>, // 微信
    EMAIL: IEnum<number>, // 邮箱
    SIGN_IN_NAME: IEnum<number>, // 登录名（最低级）（默认）

}

// 用户进行敏感操作时，账户等级枚举类
export const SysUserAccountLevelEnum: ISysUserAccountLevelEnum = {

    PHONE: {
        code: 101,
        name: '手机',
    },

    WX: {
        code: 201,
        name: '微信',
    },

    EMAIL: {
        code: 301,
        name: '邮箱',
    },

    SIGN_IN_NAME: {
        code: 401,
        name: '登录名',
    },

}

// 通过：用户信息，获取：SysUserAccountLevelEnum
export function GetSysUserAccountLevelEnum(userSelfInfo: UserSelfInfoVO) {

    if (userSelfInfo.phone) {
        return SysUserAccountLevelEnum.PHONE
    }

    if (userSelfInfo.wxAppId) {
        return SysUserAccountLevelEnum.WX
    }

    if (userSelfInfo.email) {
        return SysUserAccountLevelEnum.EMAIL
    }

    if (userSelfInfo.signInName) {
        return SysUserAccountLevelEnum.SIGN_IN_NAME
    }

    return DEFAULT_SYS_USER_ACCOUNT_LEVEL_ENUM

}

export const DEFAULT_SYS_USER_ACCOUNT_LEVEL_ENUM = SysUserAccountLevelEnum.SIGN_IN_NAME