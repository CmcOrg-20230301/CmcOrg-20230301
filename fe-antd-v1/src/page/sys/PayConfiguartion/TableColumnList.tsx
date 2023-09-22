import {YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {
    SysPayConfigurationDeleteByIdSet,
    SysPayConfigurationDO,
    SysPayConfigurationInsertOrUpdateDTO
} from "@/api/http/SysPayConfiguration";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysPayConfigurationInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysPayConfigurationDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: '支付平台', dataIndex: 'apiV3Key', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'platformPublicKey', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'merchantSerialNumber', ellipsis: true, width: 90,},

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {
        title: '是否逻辑删除',
        dataIndex: 'delFlag',
        valueEnum: YesNoDict
    },

    {title: '支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌', dataIndex: 'type', ellipsis: true, width: 90,},

    {title: '乐观锁', dataIndex: 'version', ellipsis: true, width: 90,},

    {title: '修改人id', dataIndex: 'updateId', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'privateKey', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '支付平台', dataIndex: 'merchantId', ellipsis: true, width: 90,},

    {title: '创建人id', dataIndex: 'createId', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'serverUrl', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'appId', ellipsis: true, width: 90,},

    {title: '租户 id', dataIndex: 'tenantId', ellipsis: true, width: 90,},

    {title: '支付名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '支付平台', dataIndex: 'notifyUrl', ellipsis: true, width: 90,},

    {title: '主键id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysPayConfigurationInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysPayConfigurationDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
