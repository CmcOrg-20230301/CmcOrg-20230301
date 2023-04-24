import {YesNoDict} from "@/util/DictUtil";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysDictInsertOrUpdateDTO} from "@/api/SysDict";

export const InitForm: SysDictInsertOrUpdateDTO = {} as SysDictInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysDictInsertOrUpdateDTO>[] => {

    return [


        {
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '排序号（值越大越前面，默认为 0）',
        },

        {
            title: '字典/字典项 名',
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
            title: '字典类型：1 字典 2 字典项',
            dataIndex: 'type',
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
            title: '字典 key',
            dataIndex: 'dictKey',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '字典 key（不能重复），字典项要冗余这个 key，目的：方便操作',
        },

        {
            title: '字典项 value',
            dataIndex: 'value',
            tooltip: '字典项 value（数字 123...）备注：字典为 -1',
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
