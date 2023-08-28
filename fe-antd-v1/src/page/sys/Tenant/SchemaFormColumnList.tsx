import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysTenantInsertOrUpdateDTO, SysTenantPage} from "@/api/http/SysTenant";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysUserDictList} from "@/api/http/SysUser";

export const InitForm: SysTenantInsertOrUpdateDTO = {} as SysTenantInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysTenantInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级租户',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级租户',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return GetDictTreeList(SysTenantPage);
            }
        },

        {
            title: '租户名',
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
            title: '关联用户',
            dataIndex: 'userIdSet',
            valueType: 'select',
            fieldProps: {
                showSearch: true,
                mode: 'multiple',
                maxTagCount: 'responsive',
            },
            request: () => {
                return GetDictList(SysUserDictList)
            }
        },

        {
            title: '是否启用',
            dataIndex: 'enableFlag',
            valueEnum: YesNoDict,
            valueType: 'switch',
        },

        {
            title: '排序号',
            dataIndex: 'orderNo',
            tooltip: '排序号（值越大越前面，默认为 0）',
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
