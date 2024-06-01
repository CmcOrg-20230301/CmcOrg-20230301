import {ProFormColumnsType} from "@ant-design/pro-components";

const ChatGptSchemaFormColumnList = (): ProFormColumnsType[] => {

    return [

        {
            title: '预设对话',
            dataIndex: 'preset',
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

export default ChatGptSchemaFormColumnList