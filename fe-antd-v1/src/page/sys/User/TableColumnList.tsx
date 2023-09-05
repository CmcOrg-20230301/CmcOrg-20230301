import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ModalForm, ProColumns, ProFormText} from "@ant-design/pro-components";
import {
    SysUserDeleteByIdSet,
    SysUserInsertOrUpdateDTO,
    SysUserPageDTO,
    SysUserPageVO,
    SysUserRefreshJwtSecretSuf,
    SysUserResetAvatar,
    SysUserUpdatePassword,
    SysUserUpdatePasswordDTO
} from "@/api/http/SysUser";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {EllipsisOutlined, EyeOutlined} from "@ant-design/icons";
import {ValidatorUtil} from "@/util/ValidatorUtil";
import {PasswordRSAEncrypt, RSAEncryptPro} from "@/util/RsaUtil";
import {Dropdown, TreeSelect} from "antd";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>, userAvatarUrlObj: Record<string, string>): ProColumns<SysUserPageVO>[] => [

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
        title: '头像', dataIndex: 'avatarFileId', valueType: 'image',
        fieldProps: {
            preview: {
                mask: <EyeOutlined title={"预览"}/>,
            }
        },
        renderText: (text) => {

            return userAvatarUrlObj[text] || CommonConstant.FIXED_AVATAR_URL

        }
    },

    {title: '昵称', dataIndex: 'nickname', ellipsis: true, width: 120,},

    {title: '登录名', dataIndex: 'signInName', ellipsis: true, width: 120,},

    {title: '邮箱', dataIndex: 'email', ellipsis: true, width: 120,},

    {title: '手机号', dataIndex: 'phone', ellipsis: true, width: 120,},

    {
        title: '是否正常',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },

    {
        title: '是否有密码',
        dataIndex: 'passwordFlag',
        valueEnum: YesNoDict,
        width: 100,
    },

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
    },

    {
        title: '创建时间', dataIndex: 'createTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {

            transform: (value) => {

                return {
                    beginCreateTime: value[0],
                    endCreateTime: value[1],
                } as SysUserPageDTO

            }

        }
    },

    {
        title: '最近活跃',
        dataIndex: 'lastActiveTime',
        hideInSearch: true,
        valueType: 'fromNow',
        sorter: true,
        defaultSortOrder: 'descend',
    },

    {
        title: '最近活跃', dataIndex: 'lastActiveTimeRange', hideInTable: true, valueType: 'dateTimeRange', search: {

            transform: (value) => {

                return {
                    beginLastActiveTime: value[0],
                    endLastActiveTime: value[1],
                } as SysUserPageDTO

            }

        }
    },

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysUserInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysUserDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.nickname}】吗？`)

            }}>删除</a>,

            <Dropdown

                destroyPopupOnHide

                key="3"

                menu={{

                    items: [

                        {
                            key: '1',
                            label: <a onClick={() => {

                                ExecConfirm(() => {

                                    return SysUserResetAvatar({idSet: [entity.id!]}).then(res => {

                                        ToastSuccess(res.msg)
                                        actionRef.current?.reload()

                                    })

                                }, undefined, `确定重置【${entity.nickname}】的头像吗？`)

                            }}>重置头像</a>,
                        },

                        {
                            key: '2',
                            label: <SysUserUpdatePasswordModalForm idSet={[entity.id!]} actionRef={actionRef}/>
                        },

                        {
                            key: '3',
                            label: <a onClick={() => {

                                ExecConfirm(() => {

                                    return SysUserRefreshJwtSecretSuf({idSet: [entity.id!]}).then(res => {

                                        ToastSuccess(res.msg)
                                        actionRef.current?.reload()

                                    })

                                }, undefined, `确定刷新【${entity.nickname}】的令牌吗？`)

                            }}>刷新令牌</a>

                        },

                    ]

                }}

            >

                <a><EllipsisOutlined/></a>

            </Dropdown>,

        ],

    },

];

export default TableColumnList

const SysUserUpdatePasswordTitle = "修改密码"

interface ISysUserUpdatePasswordModalForm {

    idSet: string[]

    actionRef: React.RefObject<ActionType | undefined>

}

export function SysUserUpdatePasswordModalForm(props: ISysUserUpdatePasswordModalForm) {

    return <ModalForm<SysUserUpdatePasswordDTO>

        modalProps={{
            maskClosable: false
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}

        title={SysUserUpdatePasswordTitle}

        trigger={<a>{SysUserUpdatePasswordTitle}</a>}

        onFinish={async (form) => {

            const formTemp = {...form}

            if (formTemp.newPassword) {

                const date = new Date()

                formTemp.newOriginPassword = RSAEncryptPro(formTemp.newPassword, date)
                formTemp.newPassword = PasswordRSAEncrypt(formTemp.newPassword, date)

            }

            await SysUserUpdatePassword({

                ...formTemp,
                idSet: props.idSet

            }).then(res => {

                ToastSuccess(res.msg)
                props.actionRef.current?.reload()

            })

            return true

        }}
    >

        <ProFormText label="新密码" tooltip={"可以为空"} name="newPassword"
                     rules={[{validator: ValidatorUtil.passwordCanNullValidate}]}/>

    </ModalForm>

}

