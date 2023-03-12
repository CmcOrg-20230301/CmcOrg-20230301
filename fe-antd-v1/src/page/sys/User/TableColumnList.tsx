import {YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysUserDeleteByIdSet, SysUserInsertOrUpdateDTO, SysUserPageVO} from "@/api/admin/SysUserController";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {EyeOutlined} from "@ant-design/icons/lib";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysUserPageVO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {
        title: '头像', dataIndex: 'avatarUri', valueType: 'image',
        fieldProps: {
            preview: {
                mask: <EyeOutlined title={"预览"}/>,
            }
        },
        renderText: (text) => {
            return text || CommonConstant.FIXED_AVATAR_URL
        }
    },

    {title: '昵称', dataIndex: 'nickname', ellipsis: true,},

    {title: '登录名', dataIndex: 'signInName', ellipsis: true, width: 70,},

    {title: '邮箱', dataIndex: 'email', ellipsis: true, width: 50,},

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
    },

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

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

                }, undefined, `确定删除【${entity.nickname}】吗？`)

            }}>删除</a>,

        ],
    },

];

export default TableColumnList
