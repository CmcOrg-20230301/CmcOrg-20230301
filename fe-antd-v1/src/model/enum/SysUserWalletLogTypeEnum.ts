import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysUserWalletLogTypeEnum {

    ADD_PAY: IEnum<number>,

    ADD_BACKGROUND: IEnum<number>,

    ADD_TIME_CHECK: IEnum<number>,

    REDUCE_WITHDRAW: IEnum<number>,

    REDUCE_BACKGROUND: IEnum<number>,

    REDUCE_USER_BUY: IEnum<number>,

    REDUCE_TENANT_BUY: IEnum<number>,

    ADD_USER_BUY_HASHRATE_PRODUCT_PROFIT_SHARE: IEnum<number>,

}

// 用户钱包操作日志类型，枚举类
export const SysUserWalletLogTypeEnum: ISysUserWalletLogTypeEnum = {

    ADD_PAY: {
        code: 101,
        name: '支付充值',
    },

    ADD_BACKGROUND: {
        code: 102,
        name: '后台充值',
    },

    ADD_TIME_CHECK: {
        code: 103,
        name: '超时返还',
    },

    REDUCE_WITHDRAW: {
        code: 201,
        name: '用户提现',
    },

    REDUCE_BACKGROUND: {
        code: 202,
        name: '后台扣除',
    },

    REDUCE_USER_BUY: {
        code: 203,
        name: '用户购买',
    },

    REDUCE_TENANT_BUY: {
        code: 204,
        name: '租户购买',
    },

    ADD_USER_BUY_HASHRATE_PRODUCT_PROFIT_SHARE: {
        code: 10001,
        name: '用户购买算力分成',
    },

}

export const SysUserWalletLogTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysUserWalletLogTypeEnum).forEach(key => {

    const item = SysUserWalletLogTypeEnum[key];

    SysUserWalletLogTypeEnumDict.set(item.code as number, {text: item.name})

})
