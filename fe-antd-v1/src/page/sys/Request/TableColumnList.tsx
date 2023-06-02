import {GetDictList, YesNoDict} from "@/util/DictUtil";
import {SysRequestDO, SysRequestPageDTO} from "@/api/SysRequest";
import {HandlerRegion} from "@/util/StrUtil";
import {SysUserDictList} from "@/api/SysUser";
import {Typography} from "antd";
import {ProColumns} from "@ant-design/pro-components";

const TableColumnList = (): ProColumns<SysRequestDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'uri', dataIndex: 'uri', ellipsis: true, width: 90, copyable: true},

    {title: '接口名', dataIndex: 'name', ellipsis: true, width: 90,},

    {
        title: '是否成功',
        dataIndex: 'successFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '失败信息', dataIndex: 'errorMsg', hideInSearch: true, width: 100, render: (text) => {
            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 100}}>{text}</Typography.Text>
        }
    },

    {title: '耗时', dataIndex: 'costMsStr', ellipsis: true, width: 90, sorter: true, hideInSearch: true,},

    {
        title: '耗时(ms)', dataIndex: 'costMsRange', hideInTable: true, valueType: 'digitRange', search: {
            transform: (value) => {
                return {
                    beginCostMs: value[0],
                    endCostMs: value[1],
                } as SysRequestPageDTO
            }
        }
    },

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
        sorter: true
    },

    {
        title: '创建时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {
            transform: (value) => {
                return {
                    ctBeginTime: value[0],
                    ctEndTime: value[1],
                } as SysRequestPageDTO
            }
        }
    },

    {
        title: '创建人id', dataIndex: 'createId', ellipsis: true, width: 90,
        valueType: 'select',
        request: () => {
            return GetDictList(SysUserDictList)
        }
    },

    {title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90,},

    {title: 'ip', dataIndex: 'ip', ellipsis: true, width: 90,},

    {
        title: 'ip位置', dataIndex: 'region', ellipsis: true, width: 90, renderText: (text) => {
            return HandlerRegion(text)
        }
    },

];

export default TableColumnList
