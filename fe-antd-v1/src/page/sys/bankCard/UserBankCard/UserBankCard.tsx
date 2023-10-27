import React, {useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, FormInstance, ProTable} from "@ant-design/pro-components";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/DocumentUtil";
import {
    SysUserBankCardDO,
    SysUserBankCardInfoById,
    SysUserBankCardPage,
    SysUserBankCardPageDTO
} from "@/api/http/SysUserBankCard";
import SchemaFormColumnList from "./SchemaFormColumnList";

// 用户银行卡
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysUserBankCardDO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserBankCardDO>({} as SysUserBankCardDO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysUserBankCardDO, SysUserBankCardPageDTO>

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

                expandable={{

                    expandedRowKeys,

                    onExpandedRowsChange: (expandedRows) => {

                        setExpandedRowKeys(expandedRows as string[])

                    },

                }}

                revalidateOnFocus={false}

                columns={TableColumnList(currentForm, actionRef, setFormOpen)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysUserBankCardPage({...params, sort})

                }}

            />

            <BetaSchemaForm<SysUserBankCardDO>

                title={"查看银行卡"}
                layoutType={"ModalForm"}
                grid

                readonly={true}

                rowProps={{
                    gutter: 16
                }}

                colProps={{
                    span: 8
                }}

                modalProps={{
                    maskClosable: false,
                }}

                formRef={formRef}

                isKeyPressSubmit

                params={new Date()} // 目的：为了打开页面时，执行 request方法

                request={async () => {

                    formRef.current?.resetFields()

                    if (currentForm.current.id) {

                        await SysUserBankCardInfoById({value: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysUserBankCardDO

                            setTimeout(() => {

                                formRef.current?.setFieldsValue(currentForm.current)

                            }, CommonConstant.SHORT_DELAY)

                        })

                    }

                    return {}

                }}

                open={formOpen}
                onOpenChange={setFormOpen}

                columns={SchemaFormColumnList()}

                submitter={{

                    submitButtonProps: {style: {display: 'none',}},

                }}

            />

        </>

    )

}
