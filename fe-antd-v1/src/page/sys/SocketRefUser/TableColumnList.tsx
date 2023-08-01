import {GetDictListByKey} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysSocketRefUserDO} from "@/api/http/SysSocketRefUser";
import {HandlerRegion} from "@/util/StrUtil";
import {Typography} from "antd";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysSocketRefUserDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'id', dataIndex: 'id', ellipsis: true, width: 90,},

    {title: '用户id', dataIndex: 'userId', ellipsis: true, width: 90,},

    {title: '用户昵称', dataIndex: 'nickname', ellipsis: true, width: 90,},

    {title: 'socketId', dataIndex: 'socketId', ellipsis: true, width: 90,},

    {title: '类型', dataIndex: 'type', ellipsis: true, width: 90,},

    {title: '协议', dataIndex: 'scheme', ellipsis: true, width: 90,},

    {title: '主机', dataIndex: 'host', ellipsis: true, width: 90,},

    {title: '端口', dataIndex: 'port', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90,},

    {title: '在线状态', dataIndex: 'onlineType', ellipsis: true, width: 90,},

    {
        title: 'User-Agent', dataIndex: 'userAgentJsonStr', width: 90, render: (text) => {
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

        render: (dom, entity) => [

            <a key="1" className={"red3"} onClick={() => {

            }}>下线</a>,

        ],

    },

];

export default TableColumnList
