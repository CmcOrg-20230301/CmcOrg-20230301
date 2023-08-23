import {GetDictList, GetDictListByKey} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysSocketRefUserDO, SysSocketRefUserOfflineByIdSet} from "@/api/http/SysSocketRefUser";
import {HandlerRegion} from "@/util/StrUtil";
import {Typography} from "antd";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysUserDictList} from "@/api/http/SysUser";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysSocketRefUserDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '用户', dataIndex: 'userId', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictList(SysUserDictList)
        }
    },

    {title: 'socketId', dataIndex: 'socketId', ellipsis: true, width: 90,},

    {
        title: '类型', dataIndex: 'type', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_socket_type')
        }
    },

    {title: '协议', dataIndex: 'scheme', ellipsis: true, width: 90,},

    {title: '主机', dataIndex: 'host', ellipsis: true, width: 90,},

    {title: '端口', dataIndex: 'port', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90, hideInSearch: true,},

    {
        title: '在线状态', dataIndex: 'onlineType', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_socket_online_type')
        }
    },

    {
        title: 'User-Agent', dataIndex: 'userAgentJsonStr', width: 90, hideInSearch: true, render: (text) => {
            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 90}}>{text}</Typography.Text>
        }
    },

    {title: 'ip', dataIndex: 'ip', ellipsis: true, width: 90,},

    {
        title: 'ip位置', dataIndex: 'region', ellipsis: true, width: 90, renderText: (text) => {
            return HandlerRegion(text)
        }
    },

    {
        title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_request_category')
        }
    },

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
        defaultSortOrder: 'descend',
        width: 90,
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 90,

        render: (dom, entity) => [

            <a key="1" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysSocketRefUserOfflineByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定下线【${entity.id}】吗？`)

            }}>下线</a>,

        ],

    },

];

export default TableColumnList
