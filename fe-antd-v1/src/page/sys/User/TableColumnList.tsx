import {YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysUserDeleteByIdSet, SysUserInsertOrUpdateDTO, SysUserPageVO} from "@/api/SysUser";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysUserPageVO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },


    {title: '头像uri', dataIndex: 'avatarUri', ellipsis: true, width: 90,},

    {
        title: '是否有密码',
        dataIndex: 'passwordFlag',
        valueEnum: YesNoDict
    },

    {title: '手机号码', dataIndex: 'phone', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '微信 openId', dataIndex: 'wxOpenId', ellipsis: true, width: 90,},

    {title: '登录名', dataIndex: 'signInName', ellipsis: true, width: 90,},

    {title: '昵称', dataIndex: 'nickname', ellipsis: true, width: 90,},

    {title: '角色 idSet', dataIndex: 'roleIdSet', ellipsis: true, width: 90,},

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '主键id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '正常/冻结',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },

    {title: '邮箱', dataIndex: 'email', ellipsis: true, width: 90,},


    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysUserInsertOrUpdateDTO
                setFormVisible(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysUserDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.signInName}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
