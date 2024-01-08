import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {
    SysFileStorageConfigurationDeleteByIdSet,
    SysFileStorageConfigurationDO,
    SysFileStorageConfigurationInsertOrUpdateDTO
} from "@/api/http/SysFileStorageConfiguration";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {TreeSelect} from "antd";
import {SearchTransform} from "@/util/CommonUtil";
import {SysFileStorageTypeEnumDict} from "@/model/enum/SysFileStorageTypeEnum.ts";

const TableColumnList = (currentForm: React.MutableRefObject<SysFileStorageConfigurationInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysFileStorageConfigurationDO>[] => [

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

    {title: '文件存储名', dataIndex: 'name', ellipsis: true, width: 90,},

    {
        title: '存储类型', dataIndex: 'type', ellipsis: true, width: 90,
        valueEnum: SysFileStorageTypeEnumDict,
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: '钥匙', dataIndex: 'accessKey', ellipsis: true, width: 90, hideInTable: true},

    {title: '上传端点', dataIndex: 'uploadEndpoint', ellipsis: true, width: 90, hideInTable: true},

    {title: '公开下载端点', dataIndex: 'publicDownloadEndpoint', ellipsis: true, width: 90, hideInTable: true},

    {title: '公开类型桶', dataIndex: 'bucketPublicName', ellipsis: true, width: 90, hideInTable: true},

    {title: '私有类型桶', dataIndex: 'bucketPrivateName', ellipsis: true, width: 90, hideInTable: true},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '是否默认',
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

                currentForm.current = {id: entity.id} as SysFileStorageConfigurationInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(async () => {

                    await SysFileStorageConfigurationDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
