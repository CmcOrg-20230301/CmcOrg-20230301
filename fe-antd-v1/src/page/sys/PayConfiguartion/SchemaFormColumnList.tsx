import {YesNoDict} from "@/util/DictUtil";
import {SysPayConfigurationInsertOrUpdateDTO} from "@/api/http/SysPayConfiguration";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {GetDictListByKey} from "../../../../../../LxSaas-20230907/fe-saas-v1/src/util/DictUtil";

export const InitForm: SysPayConfigurationInsertOrUpdateDTO = {} as SysPayConfigurationInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysPayConfigurationInsertOrUpdateDTO>[] => {

    return [

        {
            title: '类型',
            dataIndex: 'type',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
            },
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            request: () => {
                return GetDictListByKey('sys_pay_type')
            },
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
            tooltip: '不能重复',
        },

        {
            title: '网关地址',
            dataIndex: 'serverUrl',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
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