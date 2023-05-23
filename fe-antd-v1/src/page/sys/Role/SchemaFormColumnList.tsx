import {YesNoDict} from "@/util/DictUtil";
import {SysRoleInsertOrUpdateDTO} from "@/api/SysRole";

export const InitForm: SysRoleInsertOrUpdateDTO = {} as SysRoleInsertOrUpdateDTO

const SchemaFormColumnList = (): ProSchema<SysRoleInsertOrUpdateDTO>[] => {

    return [


        {
            title: '用户 idSet',
            dataIndex: 'userIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },

        },

        {
            title: '是否是默认角色',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否是默认角色，备注：只会有一个默认角色',
        },

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
            tooltip: '角色名，不能重复',
        },

        {
            title: '菜单 idSet',
            dataIndex: 'menuIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },

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
