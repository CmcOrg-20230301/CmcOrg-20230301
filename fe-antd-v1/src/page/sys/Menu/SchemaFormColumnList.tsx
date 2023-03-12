import {SysRolePage} from "@/api/admin/SysRoleController";
import {RouterMapKeyList} from "@/router/RouterMap";
import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysMenuInsertOrUpdateDTO, SysMenuPage} from "@/api/admin/SysMenuController";

export const InitForm: SysMenuInsertOrUpdateDTO = {} as SysMenuInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级菜单',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级区域',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return GetDictTreeList(SysMenuPage);
            }
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
            title: '路径',
            dataIndex: 'path',
            tooltip: '相同父菜单下，子菜单路径不能重复',
        },

        {
            title: '路由',
            dataIndex: 'router',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                options: RouterMapKeyList,
            },
        },

        {
            title: '图标',
            dataIndex: 'icon',
        },

        {
            title: '权限',
            dataIndex: 'auths',
            tooltip: '示例：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById',
        },

        {
            title: '权限菜单',
            dataIndex: 'authFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '不显示，只代表菜单权限',
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
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '起始页面',
            dataIndex: 'firstFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否为默认打开的页面',
        },

        {
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '值越大越前面',
        },

        {
            title: '是否显示',
            dataIndex: 'showFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '是否在左侧菜单栏显示',
        },

        {
            title: '重定向',
            dataIndex: 'redirect',
            tooltip: '优先级最高',
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
