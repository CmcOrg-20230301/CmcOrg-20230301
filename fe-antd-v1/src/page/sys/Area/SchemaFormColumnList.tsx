import {GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysAreaInsertOrUpdateDTO, SysAreaPage} from "@/api/SysArea";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysDeptPage} from "@/api/SysDept";
import {TreeSelect} from "antd";

export const InitForm: SysAreaInsertOrUpdateDTO = {} as SysAreaInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysAreaInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级区域',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级区域',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return GetDictTreeList(SysAreaPage);
            }
        },

        {
            title: '区域名',
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
