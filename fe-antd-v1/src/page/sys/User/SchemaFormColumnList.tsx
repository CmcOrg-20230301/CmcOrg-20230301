import {YesNoDict} from "@/util/DictUtil";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUser";

export const InitForm: SysUserInsertOrUpdateDTO = {} as SysUserInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

    return [


        {
            title: '前端加密之后的密码',
            dataIndex: 'password',
        },

        {
            title: '手机号码',
            dataIndex: 'phone',
            formItemProps: {
                rules: [
                    {
                        pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
                        max: 100,
                        min: 0,
                    },
                ],
            },
        },

        {
            title: '登录名',
            dataIndex: 'signInName',
            formItemProps: {
                rules: [
                    {
                        pattern: /^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$/,
                        max: 20,
                        min: 0,
                    },
                ],
            },
        },

        {
            title: '昵称',
            dataIndex: 'nickname',
            formItemProps: {
                rules: [
                    {
                        pattern: /^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$/,
                    },
                ],
            },
        },

        {
            title: '角色 idSet',
            dataIndex: 'roleIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },

        },

        {
            title: '个人简介',
            dataIndex: 'bio',
        },

        {
            title: '主键 id',
            dataIndex: 'id',
        },

        {
            title: '正常/冻结',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '邮箱',
            dataIndex: 'email',
            formItemProps: {
                rules: [
                    {
                        pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
                        max: 200,
                        min: 0,
                    },
                ],
            },
        },

        {
            title: '前端加密之后的原始密码',
            dataIndex: 'originPassword',
        },


    ]

}

export default SchemaFormColumnList
