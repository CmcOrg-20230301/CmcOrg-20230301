import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysApiTokenInsertOrUpdateDTO} from "@/api/http/SysApi.ts";

export const InitForm: SysApiTokenInsertOrUpdateDTO = {} as SysApiTokenInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysApiTokenInsertOrUpdateDTO>[] => {

    return [

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

    ]

}

export default SchemaFormColumnList
