import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysUserInsertOrUpdateDTO} from "@/api/SysUser";
import {validate, ValidatorUtil} from "@/util/ValidatorUtil";
import {SysRolePage} from "@/api/SysRole";
import {FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import {RandomNickname} from "@/util/UserUtil";
import {TreeSelect} from "antd";
import {SysDeptPage} from "@/api/SysDept";
import {SysPostPage} from "@/api/SysPost";

export const InitForm: SysUserInsertOrUpdateDTO = {} as SysUserInsertOrUpdateDTO

const SchemaFormColumnList = (formRef: React.MutableRefObject<FormInstance<SysUserInsertOrUpdateDTO>>): ProFormColumnsType<SysUserInsertOrUpdateDTO>[] => {

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

    ]

}

export default SchemaFormColumnList
