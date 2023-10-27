import {DoGetDictList, GetDictList, GetDictListByKey, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {SysRequestDO, SysRequestPageDTO} from "@/api/http/SysRequest";
import {HandlerRegion} from "@/util/StrUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {TreeSelect, Typography} from "antd";
import {ProColumns} from "@ant-design/pro-components";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";
import CommonConstant from "@/model/constant/CommonConstant";

const TableColumnList = (): ProColumns<SysRequestDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {
        title: '租户', dataIndex: 'tenantId', ellipsis: true, width: 90, hideInSearch: true, valueType: 'select',
        request: () => {
            return GetDictList(SysTenantDictList)
        }
    },

    {
        title: '租户', dataIndex: 'tenantIdSet', ellipsis: true, width: 90, hideInTable: true, valueType: 'treeSelect',
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

    {
        title: 'uri', dataIndex: 'uri', ellipsis: true, width: 90, copyable: true, render: (text) => {
            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 90}}>{text}</Typography.Text>
        }
    },

    {
        title: '接口名', dataIndex: 'name', width: 90, render: (text) => {
            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 90}}>{text}</Typography.Text>
        }
    },

    {
        title: '是否成功',
        dataIndex: 'successFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '失败信息', dataIndex: 'errorMsg', hideInSearch: true, width: 100, render: (text, entity) => {

            const subText = entity.errorMsg!.substring(0, CommonConstant.TOOLTIP_STR_LENGTH)

            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 100}}>{subText}</Typography.Text>

        }
    },

    {
        title: '耗时',
        dataIndex: 'costMs',
        ellipsis: true,
        width: 90,
        sorter: true,
        hideInSearch: true,
        render: (text, entity) => {
            return entity.costMsStr
        }
    },

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
        sorter: true,
        defaultSortOrder: 'descend',
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
        title: '创建人', dataIndex: 'createId', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserDictList({addAdminFlag: true}))
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {
        title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_request_category')
        },
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

];

export default TableColumnList
