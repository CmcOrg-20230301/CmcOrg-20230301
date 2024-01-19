import {YesNoDict} from "@/util/DictUtil";
import {SysSmsConfigurationInsertOrUpdateDTO} from "@/api/http/SysSmsConfiguration";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysSmsTypeEnumDict} from "@/model/enum/SysSmsTypeEnum.ts";

export const InitForm: SysSmsConfigurationInsertOrUpdateDTO = {} as SysSmsConfigurationInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysSmsConfigurationInsertOrUpdateDTO>[] => {

    return [

        {
            title: '短信类型',
            dataIndex: 'type',
            valueEnum: SysSmsTypeEnumDict,
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
        },

        {
            title: '短信名',
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
            title: '钥匙',
            dataIndex: 'secretId',
        },

        {
            title: '秘钥',
            dataIndex: 'secretKey',
        },

        {
            title: '短信应用 id',
            dataIndex: 'sdkAppId',
        },

        {
            title: '签名内容',
            dataIndex: 'signName',
        },

        {title: '通用短信', dataIndex: 'sendCommon'},

        {title: '注册短信', dataIndex: 'sendSignUp'},

        {title: '登录短信', dataIndex: 'sendSignIn'},

        {title: '设置密码', dataIndex: 'sendSetPassword'},

        {title: '修改密码', dataIndex: 'sendUpdatePassword'},

        {title: '设置登录名', dataIndex: 'sendSetSignInName'},

        {title: '修改登录名', dataIndex: 'sendUpdateSignInName'},

        {title: '设置邮箱', dataIndex: 'sendSetEmail'},

        {title: '修改邮箱', dataIndex: 'sendUpdateEmail'},

        {title: '设置微信', dataIndex: 'sendSetWx'},

        {title: '修改微信', dataIndex: 'sendUpdateWx'},

        {title: '设置手机', dataIndex: 'sendSetPhone'},

        {title: '修改手机', dataIndex: 'sendUpdatePhone'},

        {title: '忘记密码', dataIndex: 'sendForgetPassword'},

        {title: '账号注销', dataIndex: 'sendSignDelete'},

        {
            title: '默认发送',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否是默认短信发送，备注：只会有一个默认短信发送',
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
