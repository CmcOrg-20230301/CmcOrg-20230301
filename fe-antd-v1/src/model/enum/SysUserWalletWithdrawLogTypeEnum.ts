import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysUserWalletWithdrawTypeEnum {

    USER: IEnum,
    TENANT: IEnum,

}

// 用户提现类型枚举类
export const SysUserWalletWithdrawLogTypeEnum: ISysUserWalletWithdrawTypeEnum = {

    USER: {
        code: 1,
        name: '用户',
    },

    TENANT: {
        code: 2,
        name: '租户',
    },

}

export const SysUserWalletWithdrawLogTypeDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysUserWalletWithdrawLogTypeEnum).forEach(key => {

    const item = SysUserWalletWithdrawLogTypeEnum[key];

    SysUserWalletWithdrawLogTypeDict.set(item.code as number, {text: item.name})

})
