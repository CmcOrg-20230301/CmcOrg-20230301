import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysPayTradeStatusEnum {

    NOT_EXIST: IEnum,

    WAIT_BUYER_PAY: IEnum,

    WAIT_BUYER_CONSUME: IEnum,

    TRADE_CLOSED: IEnum,

    TRADE_SUCCESS: IEnum,

    TRADE_FINISHED: IEnum,

}

// 交易状态枚举类
export const SysPayTradeStatusEnum: ISysPayTradeStatusEnum = {

    NOT_EXIST: {
        code: -1,
        name: '订单不存在',
    },

    WAIT_BUYER_PAY: {
        code: 101,
        name: '等待付款',
    },

    WAIT_BUYER_CONSUME: {
        code: 201,
        name: '等待核销', // 支付完成，等待核销，例如：谷歌支付
    },

    TRADE_CLOSED: {
        code: 301,
        name: '交易关闭', // 未付款交易超时关闭，或支付完成后全额退款
    },

    TRADE_SUCCESS: {
        code: 401,
        name: '支付成功', //
    },

    TRADE_FINISHED: {
        code: 501,
        name: '交易结束', // 交易结束，不可退款
    },

}

export const SysPayTradeStatusEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysPayTradeStatusEnum).forEach(key => {

    const item = SysPayTradeStatusEnum[key];

    SysPayTradeStatusEnumDict.set(item.code as number, {text: item.name})

})
