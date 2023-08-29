import {RouterMapKeyList} from "@/router/RouterMap";
import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysMenuInsertOrUpdateDTO, SysMenuPage} from "@/api/http/SysMenu";
import {SysRolePage} from "@/api/http/SysRole";
import {FormInstance, ProFormColumnsType} from "@ant-design/pro-components";
import MyIcon, {IconList} from "@/componse/MyIcon/MyIcon";
import {Space} from "antd";
import {OptionProps} from "antd/es/mentions";

export const InitForm: SysMenuInsertOrUpdateDTO = {} as SysMenuInsertOrUpdateDTO

const SchemaFormColumnList = (formRef: React.MutableRefObject<FormInstance<SysMenuInsertOrUpdateDTO> | undefined>): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级菜单',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级菜单',
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
            title: '权限菜单',
            dataIndex: 'authFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '不显示，只代表菜单权限',
        },

        {

            valueType: 'dependency',

            name: ['authFlag'],

            columns: ({authFlag}: SysMenuInsertOrUpdateDTO): ProFormColumnsType<SysMenuInsertOrUpdateDTO>[] => {

                return authFlag

                    ? [

                        {
                            title: '权限',
                            tooltip: '示例：sysMenu:insertOrUpdate,sysMenu:page,sysMenu:deleteByIdSet,sysMenu:infoById,sysMenu:dictList',
                            dataIndex: 'auths',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                        whitespace: true,
                                    },
                                ],
                            },
                            valueType: 'textarea',
                            fieldProps: {
                                showCount: true,
                                maxLength: 255,
                                allowClear: true,
                            },
                        }

                    ]

                    : [

                        {
                            title: '路径',
                            dataIndex: 'path',
                            tooltip: '示例：/admin/welcome，注意：相同父菜单下，子菜单路径不能重复',
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
                            valueType: 'select',
                            fieldProps: {

                                showSearch: true,
                                options: IconList,
                                optionLabelProp: 'children',
                                optionItemRender: (item: OptionProps) => {

                                    return <Space>

                                        <MyIcon icon={item.value ? item.value : undefined}/> {item.value}

                                    </Space>

                                },

                            }
                        },

                        {
                            title: '起始页面',
                            dataIndex: 'firstFlag',
                            valueEnum: YesNoDict,
                            valueType: 'switch',
                            tooltip: '是否为默认打开的页面',
                        },

                        {
                            title: '外链',
                            dataIndex: 'linkFlag',
                            valueEnum: YesNoDict,
                            tooltip: '如果开启，打开页面时，会在一个新的窗口打开此页面，可以配合 router',
                            valueType: 'switch',
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

                    ];

            },

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
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '值越大越前面',
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
