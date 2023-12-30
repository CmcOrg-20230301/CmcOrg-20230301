import {useRef, useState} from "react";
import {ActionType, ColumnsState, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Space} from "antd";
import {
    ChangeBigDecimalNumberIdSetDTO,
    SysUserWalletAddWithdrawableMoneyBackground,
    SysUserWalletDO,
    SysUserWalletFrozenByIdSet,
    SysUserWalletPage,
    SysUserWalletPageDTO,
    SysUserWalletThawByIdSet
} from "@/api/http/SysUserWallet";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ExecConfirmPromise, ToastSuccess} from "@/util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";

// 用户钱包-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const currentForm = useRef<SysUserWalletDO>({} as SysUserWalletDO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysUserWalletDO, SysUserWalletPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}
                actionRef={actionRef}
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

                    return SysUserWalletPage({...params, sort})

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <ModalForm<ChangeBigDecimalNumberIdSetDTO>

                            modalProps={{
                                maskClosable: false
                            }}

                            isKeyPressSubmit

                            width={CommonConstant.MODAL_FORM_WIDTH}
                            title={CommonConstant.ADD_WITHDRAWABLE_MONEY}
                            trigger={<a className={"green3"}>{CommonConstant.ADD_WITHDRAWABLE_MONEY}</a>}

                            onFinish={async (form) => {

                                await ExecConfirmPromise(async () => {

                                    await SysUserWalletAddWithdrawableMoneyBackground({

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

                            <ProFormDigit label={CommonConstant.ADD_VALUE} name="number" tooltip={"可以为负数"}
                                          min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                          rules={[{required: true}]} fieldProps={{precision: 2}}/>

                        </ModalForm>

                        <a className={"green2"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysUserWalletThawByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定解冻选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量解冻</a>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysUserWalletFrozenByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定冻结选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量冻结</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            />

        </>

    )

}
