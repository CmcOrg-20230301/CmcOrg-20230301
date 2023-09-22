import {YesNoDict} from "@/util/DictUtil";
import {SysOtherAppInsertOrUpdateDTO} from "@/api/http/SysOtherApp";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {GetDictListByKey} from "../../../../../../LxSaas-20230907/fe-saas-v1/src/util/DictUtil";

export const InitForm: SysOtherAppInsertOrUpdateDTO = {} as SysOtherAppInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysOtherAppInsertOrUpdateDTO>[] => {

    return [

        {
            title: '类型',
            dataIndex: 'type',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
            },
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            request: () => {
                return GetDictListByKey('sys_other_app_type')
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
            tooltip: '不能重复',
        },

        {
            title: 'appId',
            dataIndex: 'appId',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: '不能重复',
        },

        {
            title: 'secret',
            dataIndex: 'secret',
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
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

    ]

}

export default SchemaFormColumnList
