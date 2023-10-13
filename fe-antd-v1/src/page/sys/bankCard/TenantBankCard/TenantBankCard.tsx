import React, {useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, FormInstance, ProTable} from "@ant-design/pro-components";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/DocumentUtil";
import {SysUserBankCardDO, SysUserBankCardPageDTO} from "@/api/http/SysUserBankCard";
import SchemaFormColumnList from "@/page/sys/bankCard/UserBankCard/SchemaFormColumnList";
import {IMyTree} from "@/util/DictUtil";
import {GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import {Button, Space} from "antd";
import {ColumnHeightOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons";
import {SysTenantBankCardInfoById, SysTenantBankCardTree} from "@/api/http/SysTenantBankCard";

// 用户银行卡
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysUserBankCardDO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserBankCardDO>({} as SysUserBankCardDO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const hasChildrenIdList = useRef<string[]>([]); // 有子节点的 idList

    const treeListRef = useRef<IMyTree[]>([]) // table的数据

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

                    return SysTenantBankCardTree({...params, sort})

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

            >

            </ProTable>

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

                        await SysTenantBankCardInfoById({value: currentForm.current.id}).then(res => {

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
