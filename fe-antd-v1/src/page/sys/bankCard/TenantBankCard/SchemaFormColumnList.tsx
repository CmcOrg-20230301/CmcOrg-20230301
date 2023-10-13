import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysUserBankCardInsertOrUpdateUserSelfDTO} from "@/api/http/SysUserBankCard";
import {UserBankCardFormBaseColumnArr} from "@/page/user/Wallet/UserWallet";

export const InitForm: SysUserBankCardInsertOrUpdateUserSelfDTO = {} as SysUserBankCardInsertOrUpdateUserSelfDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysUserBankCardInsertOrUpdateUserSelfDTO>[] => {

    return UserBankCardFormBaseColumnArr

}

export default SchemaFormColumnList
