import {AdminInsertOrUpdateDTO} from "@/api/http/AdminController";
import {ProFormColumnsType} from "@ant-design/pro-components";

export const InitForm: AdminInsertOrUpdateDTO = {} as AdminInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<AdminInsertOrUpdateDTO>[] => {

    // @ts-ignore
    return [
        AdminFormJson
    ]

}

export default SchemaFormColumnList
