import {YesNoDict} from "@/util/DictUtil";
import {SysDictInsertOrUpdateDTO} from "@/api/SysDict";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysDictInsertOrUpdateDTO = {} as SysDictInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysDictInsertOrUpdateDTO>[] => {

    return [

        {
            title: 'key',
            dataIndex: 'dictKey',
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
            title: '名称',
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
            title: '字典类型',
            dataIndex: 'type',
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            tooltip: '1 字典 2 字典项',
        },

        {
            title: 'value',
            dataIndex: 'value',
            tooltip: '数字 1 2 3 ...',
        },

        {
            title: '排序号',
            dataIndex: 'orderNo',
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
