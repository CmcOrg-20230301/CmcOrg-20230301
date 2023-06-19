import {GetDictList, YesNoDict} from "@/util/DictUtil";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUser";
import {validate, ValidatorUtil} from "@/util/ValidatorUtil";
import {SysRoleInsertOrUpdateDTO, SysRolePage} from "@/api/SysRole";
import {FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {RandomNickname} from "@/util/UserUtil";

export const InitForm: SysUserInsertOrUpdateDTO = {} as SysUserInsertOrUpdateDTO

const SchemaFormColumnList = (formRef: React.MutableRefObject<FormInstance<SysRoleInsertOrUpdateDTO> | undefined>): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

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

            valueType: 'dependency',

            name: ['id'],

            columns: ({id}: SysUserInsertOrUpdateDTO): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

                return id ?

                    [] : [

                        {
                            title: '密码',
                            dataIndex: 'password',
                            formItemProps: {
                                rules: [
                                    {
                                        validator: ValidatorUtil.passwordCanNullValidate
                                    }
                                ],
                            },
                        }

                    ]

            }

        },

        {
            dataIndex: 'nickname',
            formItemProps: {
                rules: [
                    {
                        validator: ValidatorUtil.nicknameCanNullValidate
                    },
                ],
            },
            title: (props, type, dom) => <>

                <span>昵称</span>

                <a className={"m-l-4"} onClick={() => {

                    formRef.current?.setFieldsValue({nickname: RandomNickname()})

                }}>随机</a>

            </>
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
