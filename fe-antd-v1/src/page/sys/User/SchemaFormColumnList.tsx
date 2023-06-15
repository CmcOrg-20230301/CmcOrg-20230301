import {GetDictList, YesNoDict} from "@/util/DictUtil";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUser";
import {validate, ValidatorUtil} from "@/util/ValidatorUtil";
import {SysRolePage} from "@/api/SysRole";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysUserInsertOrUpdateDTO = {} as SysUserInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

    return [

        {
            title: '登录名',
            dataIndex: 'signInName',
            formItemProps: {
                rules: [
                    {
                        min: 0,
                        max: 20,
                        pattern: validate.signInName.regex,
                    },
                ],
            },
        },

        {
            title: '邮箱',
            dataIndex: 'email',
            formItemProps: {
                rules: [
                    {
                        min: 0,
                        max: 200,
                        pattern: validate.email.regex,
                    },
                ],
            },
        },

        {
            title: '密码',
            dataIndex: 'password',
            formItemProps: {
                tooltip: '只有新增时设置值才会生效',
                rules: [
                    {
                        validator: ValidatorUtil.passwordCanNullValidate
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
                        validator: ValidatorUtil.nicknameCanNullValidate
                    },
                ],
            },
        },

        {
            title: '个人简介',
            dataIndex: 'bio',
        },

        {
            title: '头像uri',
            dataIndex: 'avatarUri',
        },

        {
            title: '是否正常',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '关联角色',
            dataIndex: 'roleIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },
            request: () => {
                return GetDictList(SysRolePage)
            }
        },

    ]

}

export default SchemaFormColumnList
