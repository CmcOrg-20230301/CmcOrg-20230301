import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysRoleInsertOrUpdateDTO} from "@/api/SysRole";
import {TreeSelect} from "antd";
import {SysMenuPage} from "@/api/SysMenu";
import {SysUserDictList} from "@/api/SysUser";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysRoleInsertOrUpdateDTO = {} as SysRoleInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysRoleInsertOrUpdateDTO>[] => {

    return [

        {
            title: '角色名',
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
            title: '关联菜单',
            dataIndex: 'menuIdSet',
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
                return GetDictTreeList(SysMenuPage);
            }
        },

        {
            title: '关联用户',
            dataIndex: 'userIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },
            request: () => {
                return GetDictList(SysUserDictList)
            }
        },

        {
            title: '默认角色',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '每个用户都拥有此角色权限，备注：只会有一个默认角色',
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
