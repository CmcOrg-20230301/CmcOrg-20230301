import {YesNoDict} from "@/util/DictUtil";
import {SysParamInsertOrUpdateDTO} from "@/api/SysParam";

export const InitForm: SysParamInsertOrUpdateDTO = {} as SysParamInsertOrUpdateDTO

const SchemaFormColumnList = (): ProSchema<SysParamInsertOrUpdateDTO>[] => {

    return [


        {
            title: '配置名',
            dataIndex: 'name',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '配置名，以 id为不变值进行使用，不要用此属性',
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
            title: '值',
            dataIndex: 'value',
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
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },


    ]

}

export default SchemaFormColumnList
