import {GetDictListByKey, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysSocketDisableByIdSet, SysSocketDO, SysSocketEnableByIdSet} from "@/api/http/SysSocket";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysSocketDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '类型', dataIndex: 'type', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_socket_type')
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: '协议', dataIndex: 'scheme', ellipsis: true, width: 90,},

    {title: '主机', dataIndex: 'host', ellipsis: true, width: 90,},

    {title: '端口', dataIndex: 'port', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90, hideInSearch: true},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
        defaultSortOrder: 'descend',
        width: 90,
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 90,

        render: (dom, entity) => {

            let txt = entity.enableFlag ? '禁用' : '启用'

            return [

                <a key="1" className={entity.enableFlag ? "red3" : "green2"} onClick={() => {

                    ExecConfirm(() => {

                        return entity.enableFlag ? SysSocketDisableByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        }) : SysSocketEnableByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }, undefined, `确定${txt}【${entity.id}】吗？`)

                }}>{txt}</a>,

            ]

        }

    },

];

export default TableColumnList
