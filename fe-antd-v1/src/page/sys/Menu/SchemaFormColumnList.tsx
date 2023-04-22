import {YesNoDict} from "@/util/DictUtil";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysMenuInsertOrUpdateDTO} from "@/api/SysMenu";

export const InitForm: SysMenuInsertOrUpdateDTO = {} as SysMenuInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {

    return [


        {
            title: '重定向',
            dataIndex: 'redirect',
            tooltip: '重定向，优先级最高',
        },

        {
            title: '排序号',
            dataIndex: 'orderNo',
            formItemProps: {
                rules: [
                    {
                        type: number,
                    },
                ],
            },
            tooltip: '排序号（值越大越前面，默认为 0）',
        },

        {
            title: '权限',
            dataIndex: 'auths',
            tooltip: '权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById',
        },

        {
            title: '图标',
            dataIndex: 'icon',
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
            title: '是否是权限菜单',
            dataIndex: 'authFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否是权限菜单，权限菜单：不显示，只代表菜单权限',
        },

        {
            title: '父节点id',
            dataIndex: 'parentId',
            formItemProps: {
                rules: [
                    {
                        type: number,
                    },
                ],
            },
            tooltip: '父节点id（顶级则为0）',
        },

        {
            title: '是否显示在 左侧的菜单栏里面',
            dataIndex: 'showFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到',
        },

        {
            title: '页面的 path',
            dataIndex: 'path',
            tooltip: '页面的 path，备注：相同父菜单下，子菜单 path不能重复',
        },

        {
            title: '路由',
            dataIndex: 'router',
        },

        {
            title: '菜单名',
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
            title: '角色 idSet',
            dataIndex: 'roleIdSet',
            formItemProps: {
                rules: [
                    {
                        type: number,
                    },
                ],
            },
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },

        },

        {
            title: '主键 id',
            dataIndex: 'id',
            formItemProps: {
                rules: [
                    {
                        type: number,
                    },
                ],
            },
        },

        {
            title: '是否是起始页面',
            dataIndex: 'firstFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单',
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
