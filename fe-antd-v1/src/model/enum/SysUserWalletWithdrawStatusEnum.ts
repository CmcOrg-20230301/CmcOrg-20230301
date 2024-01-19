import {IEnum} from "@/model/enum/CommonEnum";
import {PresetStatusColorType} from "antd/es/_util/colors";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";
import {SysUserWalletWithdrawLogDictListWithdrawStatus} from "@/api/http/SysUserWalletWithdrawLog";

export interface ISysUserWalletWithdrawStatusEnum {

    COMMIT: IEnum<number>,
    ACCEPT: IEnum<number>,
    SUCCESS: IEnum<number>,
    REJECT: IEnum<number>,
    CANCEL: IEnum<number>,

}

// 用户提现状态枚举类
export const SysUserWalletWithdrawStatusEnum: ISysUserWalletWithdrawStatusEnum = {

    COMMIT: {
        code: 101,
        name: '待受理', // 待受理（可取消）
        status: 'warning',
    },

    ACCEPT: {
        code: 201,
        name: '受理中', // 受理中（不可取消）
        status: 'processing',
    },

    SUCCESS: {
        code: 301,
        name: '已成功', // 已成功
        status: 'success',
    },

    REJECT: {
        code: 401,
        name: '已拒绝', // 已拒绝（需要填写拒绝理由）
        status: 'error',
    },

    CANCEL: {
        code: 501,
        name: '已取消', // 已取消（用户在待受理的时候，可以取消）
        status: 'default',
    },

}

export const SysUserWalletWithdrawStatusEnumMap = new Map<number, PresetStatusColorType>();

Object.keys(SysUserWalletWithdrawStatusEnum).forEach(key => {

    const item = SysUserWalletWithdrawStatusEnum[key] as IEnum<number>;

    SysUserWalletWithdrawStatusEnumMap.set(item.code!, item.status!)

})

// 设置：用户提现状态的字典
export function UpdateWithdrawStatusDict(setWithdrawStatusDict: (value: (((prevState: (Map<number, ProSchemaValueEnumType> | undefined)) => (Map<number, ProSchemaValueEnumType> | undefined)) | Map<number, ProSchemaValueEnumType> | undefined)) => void) {

    SysUserWalletWithdrawLogDictListWithdrawStatus().then(res => {

        const dictMap = new Map<number, ProSchemaValueEnumType>();

        res.data?.map((it) => {

            dictMap.set(it.id!, {text: it.name, status: SysUserWalletWithdrawStatusEnumMap.get(it.id!)})

        })

        setWithdrawStatusDict(dictMap)

    })

}
