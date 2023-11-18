import {YesNoDict} from "@/util/DictUtil";
import {SysPayConfigurationInsertOrUpdateDTO} from "@/api/http/SysPayConfiguration";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {Validate} from "@/util/ValidatorUtil";
import {SysPayTypeEnumDict} from "@/model/enum/SysPayTypeEnum";

export const InitForm: SysPayConfigurationInsertOrUpdateDTO = {} as SysPayConfigurationInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysPayConfigurationInsertOrUpdateDTO>[] => {

    return [

        {
            title: '类型',
            dataIndex: 'type',
            valueType: 'select',
            fieldProps: {
                allowClear: true,
                showSearch: true,
            },
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            valueEnum: SysPayTypeEnumDict,
        },

        {
            title: '名称',
            dataIndex: 'name',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
        },

        {
            title: '网关地址',
            dataIndex: 'serverUrl',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        validator: Validate.url.canNullValidator
                    },
                ],
            },
            tooltip: '例如：https://openapi.alipay.com/gateway.do',
        },

        {
            title: 'appId',
            dataIndex: 'appId',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
        },

        {
            title: '私钥',
            dataIndex: 'privateKey',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
        },

        {
            title: '平台公钥',
            dataIndex: 'platformPublicKey',
        },

        {
            title: '异步接收地址',
            dataIndex: 'notifyUrl',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        validator: Validate.url.canNullValidator
                    },
                ],
            },
        },

        {
            title: '商户号',
            dataIndex: 'merchantId',
        },

        {
            title: '证书序列',
            dataIndex: 'merchantSerialNumber',
            tooltip: '商户证书序列号',
        },

        {
            title: 'apiV3Key',
            dataIndex: 'apiV3Key',
        },

        {
            title: '默认支付',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否是默认支付方式，备注：只会有一个默认支付方式',
        },

        {
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '备注',
            dataIndex: 'remark',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 300,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 300,
                allowClear: true,
            }
        },

    ]

}

export default SchemaFormColumnList
