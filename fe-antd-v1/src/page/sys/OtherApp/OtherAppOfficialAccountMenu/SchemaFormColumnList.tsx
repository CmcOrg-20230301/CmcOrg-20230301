import {DoGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ProFormColumnsType} from "@ant-design/pro-components";
import {
    SysOtherAppOfficialAccountMenuInsertOrUpdateDTO,
    SysOtherAppOfficialAccountMenuPage
} from "@/api/http/SysOtherApp";
import {SysOtherAppOfficialAccountMenuButtonTypeEnumDict} from "@/model/enum/SysOtherAppOfficialAccountMenuButtonTypeEnum";

export const InitForm: SysOtherAppOfficialAccountMenuInsertOrUpdateDTO = {} as SysOtherAppOfficialAccountMenuInsertOrUpdateDTO

const SchemaFormColumnList = (otherAppId: string | undefined): ProFormColumnsType<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>[] => {

    return [

        {
            title: '上级公众号菜单',
            dataIndex: 'parentId',
            valueType: "treeSelect",
            fieldProps: {
                placeholder: '为空则表示顶级公众号菜单',
                allowClear: true,
                showSearch: true,
                treeNodeFilterProp: 'title',
            },
            request: () => {
                return DoGetDictTreeList(SysOtherAppOfficialAccountMenuPage({otherAppId: otherAppId}));
            }
        },

        {
            title: '菜单名',
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
            title: '菜单类型',
            dataIndex: 'buttonType',
            formItemProps: {
                rules: [
                    {
                        required: true,
                    },
                ],
            },
            valueEnum: SysOtherAppOfficialAccountMenuButtonTypeEnumDict,
        },

        {
            title: '值',
            dataIndex: 'value',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        required: true,
                        whitespace: true,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                allowClear: true,
            },
            tooltip: '如果是按钮，则表示按钮的 key，如果是链接，则表示是 url'
        },

        {
            title: '回复内容',
            dataIndex: 'replyContent',
            valueType: 'textarea',
            formItemProps: {
                rules: [
                    {
                        whitespace: true,
                        max: 500,
                    },
                ],
            },
            fieldProps: {
                showCount: true,
                maxLength: 500,
                allowClear: true,
            },
            tooltip: '备注：一般是点击按钮类型时，回复的内容'
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
