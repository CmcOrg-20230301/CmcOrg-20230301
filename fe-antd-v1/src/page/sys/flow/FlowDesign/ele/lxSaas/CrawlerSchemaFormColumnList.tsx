import {ProFormColumnsType} from "@ant-design/pro-components";

const CrawlerSchemaFormColumnList = (): ProFormColumnsType[] => {

    return [

        {
            title: '爬取内容',
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

export default CrawlerSchemaFormColumnList