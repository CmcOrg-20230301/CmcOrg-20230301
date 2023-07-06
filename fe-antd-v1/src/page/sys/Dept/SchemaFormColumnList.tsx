import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysDeptInsertOrUpdateDTO, SysDeptPage} from "@/api/http/SysDept";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysAreaPage} from "@/api/http/SysArea";
import {SysUserDictList} from "@/api/http/SysUser";
import {TreeSelect} from "antd";

export const InitForm: SysDeptInsertOrUpdateDTO = {} as SysDeptInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysDeptInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级部门',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级部门',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return GetDictTreeList(SysDeptPage);
            }
        },

        {
            title: '部门名',
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
            title: '关联区域',
            dataIndex: 'areaIdSet',
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
                return GetDictTreeList(SysAreaPage);
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
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '排序号（值越大越前面，默认为 0）',
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
