import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysUserInsertOrUpdateDTO} from "@/api/http/SysUser";
import {validate, ValidatorUtil} from "@/util/ValidatorUtil";
import {SysRolePage} from "@/api/http/SysRole";
import {FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {RandomNickname} from "@/util/UserUtil";
import {TreeSelect} from "antd";
import {SysDeptPage} from "@/api/http/SysDept";
import {SysPostPage} from "@/api/http/SysPost";
import {SysTenantPage} from "@/api/http/SysTenant";

export const InitForm: SysUserInsertOrUpdateDTO = {} as SysUserInsertOrUpdateDTO

const SchemaFormColumnList = (formRef: React.MutableRefObject<FormInstance<SysUserInsertOrUpdateDTO> | undefined>): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

    // @ts-ignore
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
                        message: validate.signInName.errorMsg,
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
                        message: validate.email.errorMsg,
                    },
                ],
            },
        },

        {
            title: '手机号',
            dataIndex: 'phone',
            formItemProps: {
                rules: [
                    {
                        min: 0,
                        max: 200,
                        pattern: validate.phone.regex,
                        message: validate.phone.errorMsg,
                    },
                ],
            },
        },

        {

            valueType: 'dependency',

            name: ['id'],

            columns: ({id}: SysUserInsertOrUpdateDTO): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

                // @ts-ignore
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

        {
            title: '关联部门',
            dataIndex: 'deptIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 'responsive',
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
            },
            request: () => {
                return GetDictTreeList(SysDeptPage);
            }
        },

        {
            title: '关联岗位',
            dataIndex: 'postIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 'responsive',
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
            },
            request: () => {
                return GetDictTreeList(SysPostPage);
            }
        },

        {
            title: '关联租户',
            dataIndex: 'tenantIdSet',
            valueType: 'treeSelect',
            fieldProps: {
                placeholder: '请选择',
                allowClear: true,
                treeNodeFilterProp: 'title',
                maxTagCount: 'responsive',
                treeCheckable: true,
                showCheckedStrategy: TreeSelect.SHOW_PARENT,
            },
            request: () => {
                return GetDictTreeList(SysTenantPage);
            }
        },

    ]

}

export default SchemaFormColumnList
