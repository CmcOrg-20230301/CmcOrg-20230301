import {ProFormColumnsType} from "@ant-design/pro-components";
import {AdminInsertOrUpdateDTO} from "@/api/admin/AdminController";

export const InitForm: AdminInsertOrUpdateDTO = {} as AdminInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<AdminInsertOrUpdateDTO>[] => {
    return [
        AdminFormJson
    ]
}

export default SchemaFormColumnList
