import {useRef, useState} from "react";
import {ActionType, ColumnsState, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {ChangeBigDecimalNumberDTO, SysUserWalletDO, SysUserWalletPageDTO,} from "@/api/http/SysUserWallet";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ExecConfirmPromise, ToastSuccess} from "@/util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/DocumentUtil";
import {
    SysTenantWalletAddWithdrawableMoneyBackground,
    SysTenantWalletFrozenByIdSet,
    SysTenantWalletThawByIdSet,
    SysTenantWalletTree
} from "@/api/http/SysTenantWallet";
import {GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import {IMyTree} from "@/util/DictUtil";
import {ColumnHeightOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons";

// 租户钱包-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const currentForm = useRef<SysUserWalletDO>({} as SysUserWalletDO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const hasChildrenIdList = useRef<string[]>([]); // 有子节点的 idList

    const treeListRef = useRef<IMyTree[]>([]) // table的数据

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysUserWalletDO, SysUserWalletPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}
                actionRef={actionRef}
                rowKey={"id"}

                pagination={false}

                columnEmptyText={false}

                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}

                rowSelection={{}}

                expandable={{

                    expandedRowKeys,

                    onExpandedRowsChange: (expandedRows) => {

                        setExpandedRowKeys(expandedRows as string[])

                    },

                }}

                revalidateOnFocus={false}

                columns={TableColumnList(currentForm, actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysTenantWalletTree({...params, sort})

                }}

                postData={(data: any) => {

                    treeListRef.current = data

                    hasChildrenIdList.current = GetIdListForHasChildrenNode(data)

                    return data

                }}

                toolbar={{

                    title:

                        <Space size={16}>

                            <Button

                                onClick={() => {

                                    setExpandedRowKeys(hasChildrenIdList.current)

                                }}

                                icon={<ColumnHeightOutlined/>}

                            >

                                展开

                            </Button>

                            <Button

                                onClick={() => {

                                    setExpandedRowKeys([])

                                }}

                                icon={<VerticalAlignMiddleOutlined/>}

                            >

                                收起

                            </Button>

                        </Space>,

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <ModalForm<ChangeBigDecimalNumberDTO>

                            modalProps={{
                                maskClosable: false
                            }}

                            isKeyPressSubmit

                            width={CommonConstant.MODAL_FORM_WIDTH}
                            title={CommonConstant.ADD_WITHDRAWABLE_MONEY}
                            trigger={<a className={"green3"}>{CommonConstant.ADD_WITHDRAWABLE_MONEY}</a>}

                            onFinish={async (form) => {

                                await ExecConfirmPromise(() => {

                                    return SysTenantWalletAddWithdrawableMoneyBackground({

                                        idSet: selectedRowKeys as string[],
                                        number: form.number

                                    }).then(res => {

                                        ToastSuccess(res.msg)
                                        actionRef.current?.reload()

                                    })

                                }, undefined, `确定要累加选中【${selectedRowKeys.length}】项的可提现吗？`).catch(() => {

                                    return true

                                })

                                return true

                            }}

                        >

                            <ProFormDigit label={CommonConstant.ADD_VALUE} tooltip={"可以为负数"} name="number"
                                          min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                          rules={[{required: true}]} fieldProps={{precision: 2}}/>

                        </ModalForm>

                        <a className={"green2"} onClick={() => {

                            ExecConfirm(() => {

                                return SysTenantWalletThawByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定解冻选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量解冻</a>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysTenantWalletFrozenByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定解冻选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量冻结</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            >

            </ProTable>

        </>

    )

}
