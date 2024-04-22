import {DoGetDictList, GetDictList, NoFormGetDictTreeList} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {
    SysSocketRefUserChangeConsoleFlagByIdSet,
    SysSocketRefUserDO,
    SysSocketRefUserOfflineByIdSet
} from "@/api/http/SysSocketRefUser";
import {HandlerRegion} from "@/util/StrUtil";
import {TreeSelect} from "antd";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";
import {SysRequestCategoryEnumDict} from "@/model/enum/SysRequestCategoryEnum.ts";
import {SysSocketTypeEnumDict} from "@/model/enum/SysSocketTypeEnum.ts";
import {SysSocketOnlineTypeEnumDict} from "@/model/enum/SysSocketOnlineTypeEnum.ts";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysSocketRefUserDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {
        title: '租户',
        dataIndex: 'tenantId',
        ellipsis: true,
        width: 90,
        hideInSearch: true,
        valueType: 'select',
        request: () => {
            return GetDictList(SysTenantDictList)
        }
    },

    {
        title: '租户',
        dataIndex: 'tenantIdSet',
        ellipsis: true,
        width: 90,
        hideInTable: true,
        valueType: 'treeSelect',
        fieldProps: {
            placeholder: '请选择',
            allowClear: true,
            treeNodeFilterProp: 'title',
            maxTagCount: 'responsive',
            treeCheckable: true,
            showCheckedStrategy: TreeSelect.SHOW_ALL,
            treeCheckStrictly: true,
        },
        request: () => {
            return NoFormGetDictTreeList(SysTenantDictList, true, '-1')
        },
        search: {
            transform: (valueArr: { label: string, value: string }[]) =>
                SearchTransform(valueArr, 'tenantIdSet')
        }
    },

    {title: 'id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '用户', dataIndex: 'userId', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserDictList({addAdminFlag: true}))
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: 'socketId', dataIndex: 'socketId', ellipsis: true, width: 90,},

    {
        title: '类型', dataIndex: 'type', ellipsis: true, width: 90,
        valueEnum: SysSocketTypeEnumDict,
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: '协议', dataIndex: 'scheme', ellipsis: true, width: 90,},

    {title: '主机', dataIndex: 'host', ellipsis: true, width: 90,},

    {title: '端口', dataIndex: 'port', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90, hideInSearch: true,},

    {
        title: '在线状态', dataIndex: 'onlineType', ellipsis: true, width: 90,
        valueEnum: SysSocketOnlineTypeEnumDict,
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: 'ip', dataIndex: 'ip', ellipsis: true, width: 90,},

    {
        title: 'ip位置', dataIndex: 'region', ellipsis: true, width: 90, renderText: (text) => {
            return HandlerRegion(text)
        }
    },

    {
        title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90,
        valueEnum: SysRequestCategoryEnumDict,
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
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
        width: 120,

        render: (dom, entity) => [

            <a key="1" className={"red3"} onClick={() => {

                ExecConfirm(async () => {

                    await SysSocketRefUserOfflineByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定下线【${entity.id}】吗？`)

            }}>下线</a>,

            <a key="2" onClick={() => {

                ExecConfirm(async () => {

                    await SysSocketRefUserChangeConsoleFlagByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定打开或者关闭【${entity.id}】的控制台吗？`)

            }}>控制台</a>,

        ],

    },

];

export default TableColumnList
