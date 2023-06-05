import {useRef, useState} from "react";
import {UserSelfInfo, UserSelfInfoVO, UserSelfUpdateInfo, UserSelfUpdateInfoDTO} from "@/api/UserSelf";
import {setUserSelfInfo} from "@/store/userSlice";
import {getAppDispatch} from "@/MyApp";
import {ActionType, ProDescriptions} from "@ant-design/pro-components";
import {ExecConfirm, ToastError, ToastSuccess} from "@/util/ToastUtil";
import {USER_CENTER_KEY_ONE} from "@/page/user/Self/Self";
import {ValidatorUtil} from "@/util/ValidatorUtil";
import {Avatar, Image, Space, Upload, UploadFile} from "antd";
import {DeleteOutlined, EyeOutlined} from "@ant-design/icons";
import CommonConstant from "@/model/constant/CommonConstant";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {CheckAvatarFileType, CheckFileSize, SysFileUploadPro} from "@/util/FileUtil";
import {SysFileGetPublicUrl} from "@/api/SysFile";

// 个人资料
export default function () {

    const appDispatch = getAppDispatch();

    const actionRef = useRef<ActionType>()

    const currentForm = useRef<UserSelfInfoVO>({})

    const [fileList, setFileList] = useState<UploadFile[]>([]);

    const [fileLoading, setFileLoading] = useState<boolean>(false)

    const [avatarUrl, setAvatarUrl] = useState<String>('')

    // 执行：更新用户基本信息
    function doSysUserSelfUpdateInfo(form: UserSelfUpdateInfoDTO) {

        return UserSelfUpdateInfo(form).then(res => {

            actionRef.current?.reload()
            ToastSuccess(res.msg)

        })

    }

    return (

        <ProDescriptions<UserSelfInfoVO>

            title={USER_CENTER_KEY_ONE}

            actionRef={actionRef}

            request={() => {

                return new Promise((resolve) => {

                    UserSelfInfo().then(res => {

                        currentForm.current = res.data

                        appDispatch(setUserSelfInfo(res.data))

                        const avatarFileId = res.data.avatarFileId;

                        if (avatarFileId as any !== -1) {

                            SysFileGetPublicUrl({idSet: [avatarFileId!]}).then(res => {

                                setAvatarUrl(res.data.map![avatarFileId] || '')

                            })

                        }

                        resolve({

                            success: true,

                            data: res.data

                        })

                    })

                })

            }}

            editable={{

                onSave: async (key, record) => {

                    if (record.nickname === '') {
                        record.nickname = undefined
                    }

                    await doSysUserSelfUpdateInfo({...record})

                    return true;

                },

            }}

            column={1}

            columns={[

                {

                    title: '头像',
                    dataIndex: 'avatarUrl',
                    editable: false,

                    render: (dom: React.ReactNode, entity) => {

                        return <Space size={16}>

                            <Avatar

                                src={

                                    <Image

                                        src={
                                            avatarUrl ? avatarUrl : CommonConstant.FIXED_AVATAR_URL
                                        }

                                        height={32}

                                        preview={{mask: <EyeOutlined title={"预览"}/>}}

                                    />

                                }

                            />

                            <Upload

                                disabled={fileLoading}

                                accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}

                                fileList={fileList}

                                maxCount={1}

                                showUploadList={false}

                                beforeUpload={(file) => {

                                    if (!CheckAvatarFileType(file.type)) {

                                        ToastError("暂不支持此文件类型：" + file.type + "，请重新选择")

                                        return false

                                    }

                                    if (!CheckFileSize(file.size!, 2097152)) {

                                        ToastError("图片大于 2MB，请重新选择")

                                        return false

                                    }

                                    return true

                                }}

                                customRequest={(options) => {

                                    setFileLoading(true)

                                    SysFileUploadPro(options.file, 'AVATAR').then(res => {

                                        actionRef.current?.reload()
                                        ToastSuccess(res.msg)
                                        setFileLoading(false)

                                    }).catch(() => {

                                        setFileLoading(false)

                                    })

                                }}

                                onChange={(info) => {
                                    setFileList(info.fileList)
                                }}

                            >

                                <a>
                                    <MyIcon

                                        title={fileLoading ? '上传中' : '上传头像'}

                                        icon={fileLoading ? 'LoadingOutlined' : 'UploadOutlined'}

                                    />
                                </a>

                            </Upload>

                            {entity.avatarUrl &&

                            <a onClick={() => {

                                if (fileLoading) {
                                    return
                                }

                                ExecConfirm(async () => {

                                    actionRef.current?.reload()
                                    ToastSuccess('操作成功')
                                    setFileLoading(false)

                                }, undefined, '确定移除头像吗？')

                            }}>

                                <DeleteOutlined title={"移除头像"} className={"red3"}/>

                            </a>

                            }

                        </Space>

                    }

                },

                {
                    title: '昵称',
                    dataIndex: 'nickname',
                    fieldProps: {
                        maxLength: 20,
                        allowClear: true,
                    },
                    formItemProps: {
                        required: true,
                        rules: [
                            {
                                validator: ValidatorUtil.nicknameCanNullValidate
                            }
                        ],
                    },
                },

                {
                    title: '个人简介',
                    dataIndex: 'bio',
                    valueType: 'textarea',
                    fieldProps: {
                        showCount: true,
                        maxLength: 100,
                        allowClear: true,
                    },
                },

            ]}

        />

    )

}
