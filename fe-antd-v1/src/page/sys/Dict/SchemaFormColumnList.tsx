import {YesNoDict} from "@/util/DictUtil";
import {SysDictInsertOrUpdateDTO} from "@/api/http/SysDict";
import {ProFormColumnsType, ProSchemaValueEnumType} from "@ant-design/pro-components";

export const InitForm: SysDictInsertOrUpdateDTO = {} as SysDictInsertOrUpdateDTO

export const DictTypeDict = new Map<number, ProSchemaValueEnumType>();
DictTypeDict.set(1, {text: '字典'})
DictTypeDict.set(2, {text: '字典项'})

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
            valueEnum: DictTypeDict,
        },

        {

            valueType: 'dependency',

            name: ['type'],

            columns: ({type}: SysDictInsertOrUpdateDTO): ProFormColumnsType<SysDictInsertOrUpdateDTO>[] => {

                return type as any === 2 ?

                    [

                        {
                            title: '值',
                            dataIndex: 'value',
                            formItemProps: {
                                rules: [
                                    {
                                        required: true,
                                    },
                                ],
                            },
                            tooltip: '一般为数字：101 201 301 ...',
                        },

                    ] : []

            }

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
