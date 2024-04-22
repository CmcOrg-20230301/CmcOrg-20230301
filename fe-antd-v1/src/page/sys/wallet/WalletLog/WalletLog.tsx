import {useMemo, useState} from "react";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil.ts";
import {
    SysUserWalletLogDO,
    SysUserWalletLogPage,
    SysUserWalletLogPageTenant,
    SysUserWalletLogPageUserSelf,
    SysUserWalletLogUserSelfPageDTO
} from "@/api/http/SysUserWalletLog.ts";
import {SysUserWalletLogTypeEnumDict} from "@/model/enum/SysUserWalletLogTypeEnum.ts";
import {GetTextType} from "@/util/StrUtil.ts";
import {DoGetDictList, GetDictList, NoFormGetDictTreeList} from "@/util/DictUtil.ts";
import {ColumnsState, ProTable} from "@ant-design/pro-components";
import {SysUserDictList} from "@/api/http/SysUser.ts";
import {TreeSelect, Typography} from "antd";
import {SysTenantDictList} from "@/api/http/SysTenant.ts";
import {SearchTransform} from "@/util/CommonUtil.ts";
import {SysUserTenantEnum, SysUserTenantEnumDict} from "@/model/enum/SysUserTenantEnum.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";

export interface IUserWalletLog {

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

    type?: number // 记录类型：1开头 增加 2开头 减少

    allPageFlag?: boolean // 是否查询：所有的数据，默认：true

}

/**
 * 钱包日志：table
 */
export default function (props: IUserWalletLog) {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    const allPageFlag = useMemo(() => {

        return (props.allPageFlag === undefined) || props.allPageFlag

    }, [props.allPageFlag]);

    return (

        <ProTable<SysUserWalletLogDO, SysUserWalletLogUserSelfPageDTO>

            rowKey={"id"}

            pagination={{
                showQuickJumper: true,
                showSizeChanger: true,
            }}

            columnEmptyText={false}

            columnsState={{
                value: columnsStateMap,
                onChange: setColumnsStateMap,
            }}

            revalidateOnFocus={false}

            scroll={fullScreenFlag ? undefined : {y: 440}}

            columns={[

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

                {
                    title: '用户类型',
                    dataIndex: 'sysUserTenantEnum',
                    valueEnum: SysUserTenantEnumDict,
                    width: 90,
                    renderText: (text, record) => {
                        return record.userId === CommonConstant.TENANT_USER_ID_STR ? SysUserTenantEnum.TENANT.code : SysUserTenantEnum.USER.code
                    },
                },

                {
                    title: '类型', dataIndex: 'type', ellipsis: true, width: 90, valueType: 'select',
                    valueEnum: SysUserWalletLogTypeEnumDict,
                    fieldProps: {
                        allowClear: true,
                        showSearch: true,
                    },
                    hideInSearch: Boolean(props.type),
                    hideInTable: Boolean(props.type)
                },

                {
                    title: '可提现（前）',
                    dataIndex: 'withdrawableMoneyPre',
                    ellipsis: true,
                    width: 120,
                    hideInSearch: true,
                    valueType: 'money',
                    fieldProps: {
                        precision: 2, // 小数点精度
                    },
                },

                {
                    title: '可提现（变）',
                    dataIndex: 'withdrawableMoneyChange',
                    width: 120,
                    hideInSearch: true,
                    valueType: 'money',
                    fieldProps: {
                        precision: 2, // 小数点精度
                    },
                    render: (dom, entity: SysUserWalletLogDO) => {

                        const type = GetTextType(entity.withdrawableMoneyChange)

                        return <Typography.Text

                            ellipsis={{tooltip: true}}
                            type={type}
                            style={{width: 120}}

                        >
                            {dom}
                        </Typography.Text>

                    }
                },

                {
                    title: '可提现（后）',
                    dataIndex: 'withdrawableMoneySuf',
                    ellipsis: true,
                    width: 120,
                    hideInSearch: true,
                    valueType: 'money',
                    fieldProps: {
                        precision: 2, // 小数点精度
                    },
                },

                {
                    title: '创建人',
                    dataIndex: 'createId',
                    ellipsis: true,
                    width: 90,
                    valueType: 'select',
                    hideInSearch: true,
                    request: () => {
                        return DoGetDictList(SysUserDictList({addAdminFlag: true}))
                    },
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
                    width: 90,
                    sorter: true,
                    defaultSortOrder: 'descend',
                },

                {
                    title: '创建时间',
                    dataIndex: 'createTimeRange',
                    hideInTable: true,
                    valueType: 'dateTimeRange',
                    search: {

                        transform: (value) => {

                            return {

                                ctBeginTime: value[0],
                                ctEndTime: value[1],

                            } as SysUserWalletLogUserSelfPageDTO

                        }

                    }
                },

                {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90},

            ]}

            options={{
                fullScreen: true,
            }}

            request={(params, sort, filter) => {

                if (props.tenantId) {

                    return SysUserWalletLogPageTenant({
                        type: props.type,
                        ...params,
                        sort,
                        tenantIdSet: [props.tenantId]
                    })

                } else if (props.userId || allPageFlag) {

                    return SysUserWalletLogPage({type: props.type, ...params, sort, userId: props.userId})

                } else {

                    return SysUserWalletLogPageUserSelf({type: props.type, ...params, sort})

                }

            }}

        />

    )

}