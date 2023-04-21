import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysMenuDO, SysMenuInsertOrUpdateDTO} from "@/api/SysMenu";

const TableColumnList = (currentForm: React.MutableRefObject<SysMenuInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysMenuDO>[] => [];

export default TableColumnList
