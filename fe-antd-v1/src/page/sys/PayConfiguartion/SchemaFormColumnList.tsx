import {YesNoDict} from "@/util/DictUtil";
import {SysPayConfigurationInsertOrUpdateDTO} from "@/api/http/SysPayConfiguration";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysPayConfigurationInsertOrUpdateDTO = {} as SysPayConfigurationInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysPayConfigurationInsertOrUpdateDTO>[] => {

    return [

        {
            title: '支付平台',
            dataIndex: 'apiV3Key',
            tooltip: '支付平台，商户APIV3密钥',
        },

        {
            title: '支付平台',
            dataIndex: 'platformPublicKey',
            tooltip: '支付平台，公钥',
        },

        {
            title: '支付平台',
            dataIndex: 'merchantSerialNumber',
            tooltip: '支付平台，商户证书序列号',
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

        {
            title: '支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌',
            dataIndex: 'type',
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
            title: '支付平台',
            dataIndex: 'privateKey',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '支付平台，私钥',
        },

        {
            title: '支付平台',
            dataIndex: 'merchantId',
            tooltip: '支付平台，商户号',
        },

        {
            title: '支付平台',
            dataIndex: 'serverUrl',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '支付平台，网关地址，例如：https://openapi.alipay.com/gateway.do',
        },

        {
            title: '支付平台',
            dataIndex: 'appId',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '支付平台，应用 id',
        },

        {
            title: '租户 id',
            dataIndex: 'tenantId',
            tooltip: '租户 id，可以为空，为空则表示：默认租户：0',
        },

        {
            title: '支付名',
            dataIndex: 'name',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '支付名（不可重复）',
        },

        {
            title: '支付平台',
            dataIndex: 'notifyUrl',
            tooltip: '支付平台，异步接收地址',
        },

        {
            title: '主键 id',
            dataIndex: 'id',
        },

        {
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

    ]

}

export default SchemaFormColumnList
