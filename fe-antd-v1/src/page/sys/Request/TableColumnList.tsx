import {YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {AdminDeleteByIdSetApi, AdminInsertOrUpdateDTO, SysRequestDO} from "@/api/SysRequest";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";

const TableColumnList = (currentForm: React.MutableRefObject<AdminInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysRequestDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },


    {title: 'ip', dataIndex: 'ip', ellipsis: true, width: 90,},

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

    {title: '请求的参数', dataIndex: 'requestParam', ellipsis: true, width: 90,},

    {title: '请求类型', dataIndex: 'type', ellipsis: true, width: 90,},

    {title: '乐观锁', dataIndex: 'version', ellipsis: true, width: 90,},

    {title: '请求的 uri', dataIndex: 'uri', ellipsis: true, width: 90,},

    {
        title: '请求是否成功',
        dataIndex: 'successFlag',
        valueEnum: YesNoDict
    },

    {title: '失败信息', dataIndex: 'errorMsg', ellipsis: true, width: 90,},

    {title: '修改人id', dataIndex: 'updateId', ellipsis: true, width: 90,},

    {title: '耗时', dataIndex: 'costMs', ellipsis: true, width: 90,},

    {title: '耗时', dataIndex: 'costMsStr', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '创建人id', dataIndex: 'createId', ellipsis: true, width: 90,},

    {title: '接口名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '主键id', dataIndex: 'id', ellipsis: true, width: 90,},

    {title: '请求类别', dataIndex: 'category', ellipsis: true, width: 90,},

    {title: 'Ip2RegionUtil.getRegion() 获取到的 ip所处区域', dataIndex: 'region', ellipsis: true, width: 90,},

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

                currentForm.current = {id: entity.id} as AdminInsertOrUpdateDTO
                setFormVisible(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return AdminDeleteByIdSetApi({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
