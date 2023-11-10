import {GetDictListByKey, YesNoDict} from "@/util/DictUtil";
import {SysOtherAppInsertOrUpdateDTO} from "@/api/http/SysOtherApp";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysOtherAppInsertOrUpdateDTO = {} as SysOtherAppInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysOtherAppInsertOrUpdateDTO>[] => {

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
            request: () => {
                return GetDictListByKey('sys_other_app_type')
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
            tooltip: '不能重复',
        },

        {
            title: 'secret',
            dataIndex: 'secret',
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
            title: '关注回复',
            dataIndex: 'subscribeReplyContent',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 500,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 500,
                allowClear: true,
            },
            tooltip: "用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容",
        },

        {
            title: '文字回复',
            dataIndex: 'textReplyContent',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 500,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 500,
                allowClear: true,
            },
            tooltip: "用户发送文字之后，回复的内容",
        },

        {
            title: '图片回复',
            dataIndex: 'imageReplyContent',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 500,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 500,
                allowClear: true,
            },
            tooltip: "用户发送图片之后，回复的内容",
        },

        {
            title: '二维码',
            dataIndex: 'qrCode',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 500,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 500,
                allowClear: true,
            },
            tooltip: "备注：不是二维码图片的地址，而是二维码解码之后的值",
        },

        {
            title: 'openId',
            dataIndex: 'openId',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                    },
                ],
            },
            tooltip: '例如：接收微信公众号消息时的 ToUserName，不能重复',
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
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

    ]

}

export default SchemaFormColumnList
