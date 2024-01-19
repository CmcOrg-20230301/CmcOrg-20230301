import {IEnum} from "@/model/enum/CommonEnum";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export interface ISysSocketTypeEnum {

    TCP_PROTOBUF: IEnum<number>,

    WEB_SOCKET: IEnum<number>,

}

// socket类型，枚举类
export const SysSocketTypeEnum: ISysSocketTypeEnum = {

    TCP_PROTOBUF: {
        code: 101,
        name: 'tcp_protobuf',
    },

    WEB_SOCKET: {
        code: 201,
        name: 'web_socket',
    },

}

export const SysSocketTypeEnumDict = new Map<number, ProSchemaValueEnumType>();

Object.keys(SysSocketTypeEnum).forEach(key => {

    const item = SysSocketTypeEnum[key] as IEnum<number>;

    SysSocketTypeEnumDict.set(item.code!, {text: item.name})

})
