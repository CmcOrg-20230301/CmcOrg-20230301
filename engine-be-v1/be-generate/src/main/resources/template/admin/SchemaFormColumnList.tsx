import {AdminInsertOrUpdateDTO} from "@/api/AdminController";

export const InitForm: AdminInsertOrUpdateDTO = {} as AdminInsertOrUpdateDTO

const SchemaFormColumnList = (): ProSchema<AdminInsertOrUpdateDTO>[] => {

    return [
        AdminFormJson
    ]

}

export default SchemaFormColumnList
