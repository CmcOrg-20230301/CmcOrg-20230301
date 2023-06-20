import {YesNoDict} from "@/util/DictUtil";
import {SysDeptInsertOrUpdateDTO} from "@/api/SysDept";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysDeptInsertOrUpdateDTO = {} as SysDeptInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysDeptInsertOrUpdateDTO>[] => {

    return [

        {
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '排序号（值越大越前面，默认为 0）',
        },

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
            title: '区域 idSet',
            dataIndex: 'areaIdSet',
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

        {
            title: '父节点id',
            dataIndex: 'parentId',
            tooltip: '父节点id（顶级则为0）',
        },

    ]

}

export default SchemaFormColumnList
