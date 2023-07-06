import {GetDictList, GetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysPostInsertOrUpdateDTO, SysPostPage} from "@/api/http/SysPost";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {SysUserDictList} from "@/api/http/SysUser";

export const InitForm: SysPostInsertOrUpdateDTO = {} as SysPostInsertOrUpdateDTO

const SchemaFormColumnList = (): ProFormColumnsType<SysPostInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级岗位',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级岗位',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return GetDictTreeList(SysPostPage);
            }
        },

        {
            title: '岗位名',
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
