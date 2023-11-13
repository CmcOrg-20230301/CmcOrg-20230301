import {GetDictListByKey, YesNoDict} from "@/util/DictUtil";
import {SysFileStorageConfigurationInsertOrUpdateDTO} from "@/api/http/SysFileStorageConfiguration";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: SysFileStorageConfigurationInsertOrUpdateDTO = {} as SysFileStorageConfigurationInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysFileStorageConfigurationInsertOrUpdateDTO>[] => {

    return [

        {
            title: '文件存储名',
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
            title: '存储类型',
            dataIndex: 'type',
            valueType: 'select',
            fieldProps: {
                allowClear: true,
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
                return GetDictListByKey('sys_file_storage_configuration_type')
            },
        },

        {
            title: '钥匙',
            dataIndex: 'accessKey',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: 'accessKey'
        },

        {
            title: '秘钥',
            dataIndex: 'secretKey',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            tooltip: 'secretKey'
        },

        {
            title: '上传端点',
            dataIndex: 'uploadEndpoint',
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
            title: '公开下载端点',
            dataIndex: 'publicDownloadEndpoint',
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
            title: '公开类型桶',
            dataIndex: 'bucketPublicName',
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
            title: '私有类型桶',
            dataIndex: 'bucketPrivateName',
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

        {
            title: '是否默认',
            dataIndex: 'defaultFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
            tooltip: '备注：只会有一个默认存储',
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
