import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {
    SysSmsConfigurationDeleteByIdSet,
    SysSmsConfigurationDO,
    SysSmsConfigurationInsertOrUpdateDTO
} from "@/api/http/SysSmsConfiguration";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysSmsTypeEnumDict} from "@/model/enum/SysSmsTypeEnum.ts";
import {SysTenantDictList} from "@/api/http/SysTenant.ts";
import {TreeSelect} from "antd";
import {SearchTransform} from "@/util/CommonUtil.ts";

const TableColumnList = (currentForm: React.MutableRefObject<SysSmsConfigurationInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysSmsConfigurationDO>[] => [

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

    {title: '短信类型', dataIndex: 'type', ellipsis: true, width: 90, valueEnum: SysSmsTypeEnumDict,},

    {title: '短信名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '钥匙', dataIndex: 'secretId', ellipsis: true, width: 90,},

    {title: '秘钥', dataIndex: 'secretKey', ellipsis: true, width: 90,},

    {title: '短信应用 id', dataIndex: 'sdkAppId', ellipsis: true, width: 90,},

    {title: '签名内容', dataIndex: 'signName', ellipsis: true, width: 90,},

    {title: '账号注销', dataIndex: 'sendDelete', ellipsis: true, width: 90,},

    {title: '绑定手机', dataIndex: 'sendBind', ellipsis: true, width: 90,},

    {title: '修改手机', dataIndex: 'sendUpdate', ellipsis: true, width: 90,},

    {title: '修改密码', dataIndex: 'sendUpdatePassword', ellipsis: true, width: 90,},

    {title: '忘记密码', dataIndex: 'sendForgetPassword', ellipsis: true, width: 90,},

    {title: '登录短信', dataIndex: 'sendSignIn', ellipsis: true, width: 90,},

    {title: '注册短信', dataIndex: 'sendSignUp', ellipsis: true, width: 90,},

    {title: '通用短信', dataIndex: 'sendCommon', ellipsis: true, width: 90,},

    {
        title: '是否是默认短信发送',
        dataIndex: 'defaultFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 120,

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysSmsConfigurationInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(async () => {

                    await SysSmsConfigurationDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
