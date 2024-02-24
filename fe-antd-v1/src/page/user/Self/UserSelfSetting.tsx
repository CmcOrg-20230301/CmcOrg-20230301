import React, {useEffect, useMemo, useState} from "react";
import {useAppSelector} from "@/store";
import {List, Modal} from "antd";
import {ColumnsState, ProTable} from "@ant-design/pro-components";
import {USER_CENTER_KEY_TWO} from "@/page/user/Self/Self";
import Title from "antd/es/typography/Title";
import {SysRequestDO, SysRequestPageDTO, SysRequestSelfLoginRecord} from "@/api/http/SysRequest";
import {HandlerRegion} from "@/util/StrUtil";
import {UserSelfInfoVO} from "@/api/http/UserSelf";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {SysRequestCategoryEnumDict} from "@/model/enum/SysRequestCategoryEnum.ts";
import {SysSignConfigurationVO, SysTenantGetConfigurationById} from "@/api/http/SysTenant.ts";
import {MyUseState} from "@/util/HookUtil.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {IEnum} from "@/model/enum/CommonEnum.ts";
import {
    DEFAULT_SYS_USER_ACCOUNT_LEVEL_ENUM,
    GetSysUserAccountLevelEnum,
    SysUserAccountLevelEnum
} from "@/model/enum/SysUserAccountLevelEnum.ts";
import UserSelfDeleteByEmailModalForm from "@/page/user/Self/userSelfSetting/email/UserSelfDeleteByEmailModalForm.tsx";
import UpdatePasswordByWxModalForm from "@/page/user/Self/userSelfSetting/wx/UpdatePasswordByWxModalForm.tsx";
import SetPasswordByWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetPasswordByWxModalForm.tsx";
import UpdateSignNameByWxModalForm from "@/page/user/Self/userSelfSetting/wx/UpdateSignNameByWxModalForm.tsx";
import SetSignNameByWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetSignNameByWxModalForm.tsx";
import UpdateEmailByWxModalForm from "@/page/user/Self/userSelfSetting/wx/UpdateEmailByWxModalForm.tsx";
import SetEmailByWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetEmailByWxModalForm.tsx";
import SetPhoneByWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetPhoneByWxModalForm.tsx";
import UpdateWxByWxModalForm from "@/page/user/Self/userSelfSetting/wx/UpdateWxByWxModalForm.tsx";
import UserSelfDeleteByWxModalForm from "@/page/user/Self/userSelfSetting/wx/UserSelfDeleteByWxModalForm.tsx";
import SetSignNameByEmailModalForm from "@/page/user/Self/userSelfSetting/email/SetSignNameByEmailModalForm.tsx";
import SetPhoneByEmailModalForm from "@/page/user/Self/userSelfSetting/email/SetPhoneByEmailModalForm.tsx";
import SetWxByEmailModalForm from "@/page/user/Self/userSelfSetting/email/SetWxByEmailModalForm.tsx";
import UpdatePasswordByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/UpdatePasswordByPhoneModalForm.tsx";
import UpdateSignNameByEmailModalForm from "@/page/user/Self/userSelfSetting/email/UpdateSignNameByEmailModalForm.tsx";
import UpdateEmailByEmailModalForm from "@/page/user/Self/userSelfSetting/email/UpdateEmailByEmailModalForm.tsx";
import UpdatePasswordBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/UpdatePasswordBySignInNameModalForm.tsx";
import SetEmailBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/SetEmailBySignInNameModalForm.tsx";
import UserSelfDeleteBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/UserSelfDeleteBySignInNameModalForm.tsx";
import UpdateSignNameBySignInNameModalForm from "./userSelfSetting/signInName/UpdateSignNameBySignInNameModalForm";
import SetPhoneBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/SetPhoneBySignInNameModalForm.tsx";
import UpdatePasswordByEmailModalForm from "@/page/user/Self/userSelfSetting/email/UpdatePasswordByEmailModalForm.tsx";
import UserSelfDeleteByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/UserSelfDeleteByPhoneModalForm.tsx";
import SetPasswordByPhoneModalForm from "./userSelfSetting/phone/SetPasswordByPhoneModalForm";
import UpdateSignNameByPhoneModalForm from "./userSelfSetting/phone/UpdateSignNameByPhoneModalForm";
import SetSignNameByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/SetSignNameByPhoneModalForm.tsx";
import UpdateEmailByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/UpdateEmailByPhoneModalForm.tsx";
import SetEmailByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/SetEmailByPhoneModalForm.tsx";
import UpdatePhoneByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/UpdatePhoneByPhoneModalForm.tsx";
import UpdateWxByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/UpdateWxByPhoneModalForm.tsx";
import SetWxByPhoneModalForm from "@/page/user/Self/userSelfSetting/phone/SetWxByPhoneModalForm.tsx";
import SetWxBySignInNameModalForm from "@/page/user/Self/userSelfSetting/signInName/SetWxBySignInNameModalForm.tsx";
import SetSingleSignInWxByPhoneModalForm
    from "@/page/user/Self/userSelfSetting/phone/SetSingleSignInWxByPhoneModalForm.tsx";
import SetSingleSignInWxBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/SetSingleSignInWxBySignInNameModalForm.tsx";
import SetSingleSignInWxByEmailModalForm
    from "@/page/user/Self/userSelfSetting/email/SetSingleSignInWxByEmailModalForm.tsx";
import SetSingleSignInPhoneByWxModalForm
    from "@/page/user/Self/userSelfSetting/wx/SetSingleSignInPhoneByWxModalForm.tsx";
import SetSingleSignInWxByWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetSingleSignInWxByWxModalForm.tsx";
import SetSingleSignInPhoneByEmailModalForm
    from "@/page/user/Self/userSelfSetting/email/SetSingleSignInPhoneByEmailModalForm.tsx";
import SetSingleSignInPhoneByPhoneModalForm
    from "@/page/user/Self/userSelfSetting/phone/SetSingleSignInPhoneByPhoneModalForm.tsx";
import SetSingleSignInPhoneBySignInNameModalForm
    from "@/page/user/Self/userSelfSetting/signInName/SetSingleSignInPhoneBySignInNameModalForm.tsx";
import {MyLocalStorage} from "@/util/StorageUtil.ts";

interface IUserSelfSetting {

    title: string
    description?: string
    actions: (JSX.Element | null)[];

}

export const UserSelfSetPasswordTitle = "设置密码"
export const UserSelfUpdatePasswordTitle = "修改密码"

export const UserSelfSetSignInNameModalTitle = "设置登录名"
export const UserSelfUpdateSignInNameModalTitle = "修改登录名"

export const UserSelfSetEmailModalTitle = "绑定邮箱"
export const UserSelfUpdateEmailModalTitle = "修改邮箱"

export const UserSelfSetPhoneModalTitle = "绑定手机号"
export const UserSelfUpdatePhoneModalTitle = "修改手机号"

export const UserSelfSetWxModalTitle = "绑定微信"
export const UserSelfUpdateWxModalTitle = "修改微信"

export const UserSelfSetSingleSignInWxModalTitle = "设置微信"
export const UserSelfUpdateSingleSignInWxModalTitle = "修改微信"

export const UserSelfSetSingleSignInPhoneModalTitle = "设置手机号"
export const UserSelfUpdateSingleSignInPhoneModalTitle = "修改手机号"

export const RequestSelfLoginRecordModalTitle = "登录记录"

export const UserSelfDeleteModalTitle = "账号注销"
export const UserSelfDeleteModalTargetName = "立即注销"

/**
 * 获取：dataSourceMap
 */
function GetDataSourceMap(userSelfInfo: UserSelfInfoVO) {

    const dataSourceMap: Map<number, IUserSelfSetting[]> = new Map();

    Object.keys(SysUserAccountLevelEnum).forEach(key => {

        const item = SysUserAccountLevelEnum[key] as IEnum<number>;

        let dataSourceArr: IUserSelfSetting[]

        if (item.code === SysUserAccountLevelEnum.WX.code) { // 微信

            dataSourceArr = [

                {
                    title: '密码',
                    actions: [
                        userSelfInfo.passwordFlag ? <UpdatePasswordByWxModalForm key={"1"}/> :
                            <SetPasswordByWxModalForm key={"1"}/>
                    ]
                },

                {
                    title: '登录名',
                    description: userSelfInfo.signInName || '暂无',
                    actions: [
                        userSelfInfo.signInName ? <UpdateSignNameByWxModalForm key={"1"}/> :
                            <SetSignNameByWxModalForm key={"1"}/>
                    ]
                },

                {
                    title: '邮箱',
                    description: userSelfInfo.email || '暂无',
                    actions: [
                        userSelfInfo.email ? <UpdateEmailByWxModalForm key={"1"}/> :
                            <SetEmailByWxModalForm key={"1"}/>
                    ]
                },

                {
                    title: '手机号',
                    description: userSelfInfo.phone || '暂无',
                    actions: [
                        userSelfInfo.phone ? null : <SetPhoneByWxModalForm key={"1"}/>
                    ]
                },

                {
                    title: '微信',
                    description: userSelfInfo.wxOpenId || '暂无',
                    actions: [
                        userSelfInfo.wxOpenId ? <UpdateWxByWxModalForm key={"1"}/> : null
                    ]
                },

                {
                    title: '统一登录',
                    description: '便捷快速登录账号',
                    actions: [
                        <SetSingleSignInWxByWxModalForm key={"1"} userSelfInfo={userSelfInfo}/>,
                        <SetSingleSignInPhoneByWxModalForm key={"2"} userSelfInfo={userSelfInfo}/>
                    ]
                },

                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },

                {
                    title: UserSelfDeleteModalTitle,
                    description: userSelfInfo.createTime ? ('注册时间：' + userSelfInfo.createTime) : undefined,
                    actions: [
                        <UserSelfDeleteByWxModalForm key={"1"}/>
                    ]
                },

            ]

        } else if (item.code === SysUserAccountLevelEnum.EMAIL.code) { // 邮箱

            dataSourceArr = [

                {
                    title: '密码',
                    actions: [
                        userSelfInfo.passwordFlag ? <UpdatePasswordByEmailModalForm key={"1"}/> : null
                    ]
                },

                {
                    title: '登录名',
                    description: userSelfInfo.signInName || '暂无',
                    actions: [
                        userSelfInfo.signInName ? <UpdateSignNameByEmailModalForm key={"1"}/> :
                            <SetSignNameByEmailModalForm key={"1"}/>
                    ]
                },

                {
                    title: '邮箱',
                    description: userSelfInfo.email || '暂无',
                    actions: [
                        userSelfInfo.email ? <UpdateEmailByEmailModalForm key={"1"}/> : null
                    ]
                },

                {
                    title: '手机号',
                    description: userSelfInfo.phone || '暂无',
                    actions: [
                        userSelfInfo.phone ? null : <SetPhoneByEmailModalForm key={"1"}/>
                    ]
                },

                {
                    title: '微信',
                    description: userSelfInfo.wxOpenId || '暂无',
                    actions: [
                        userSelfInfo.wxOpenId ? null : <SetWxByEmailModalForm key={"1"}/>
                    ]
                },

                {
                    title: '统一登录',
                    description: '便捷快速登录账号',
                    actions: [
                        <SetSingleSignInWxByEmailModalForm key={"1"} userSelfInfo={userSelfInfo}/>,
                        <SetSingleSignInPhoneByEmailModalForm key={"2"} userSelfInfo={userSelfInfo}/>
                    ]
                },

                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },

                {
                    title: UserSelfDeleteModalTitle,
                    description: userSelfInfo.createTime ? ('注册时间：' + userSelfInfo.createTime) : undefined,
                    actions: [
                        <UserSelfDeleteByEmailModalForm key={"1"}/>
                    ]
                },

            ]

        } else if (item.code === SysUserAccountLevelEnum.PHONE.code) { // 手机

            dataSourceArr = [

                {
                    title: '密码',
                    actions: [
                        userSelfInfo.passwordFlag ? <UpdatePasswordByPhoneModalForm key={"1"}/> :
                            <SetPasswordByPhoneModalForm key={"1"}/>
                    ]
                },

                {
                    title: '登录名',
                    description: userSelfInfo.signInName || '暂无',
                    actions: [
                        userSelfInfo.signInName ? <UpdateSignNameByPhoneModalForm key={"1"}/> :
                            <SetSignNameByPhoneModalForm key={"1"}/>
                    ]
                },

                {
                    title: '邮箱',
                    description: userSelfInfo.email || '暂无',
                    actions: [
                        userSelfInfo.email ? <UpdateEmailByPhoneModalForm key={"1"}/> :
                            <SetEmailByPhoneModalForm key={"1"}/>
                    ]
                },

                {
                    title: '手机号',
                    description: userSelfInfo.phone || '暂无',
                    actions: [
                        userSelfInfo.phone ? <UpdatePhoneByPhoneModalForm key={"1"}/> : null
                    ]
                },

                {
                    title: '微信',
                    description: userSelfInfo.wxOpenId || '暂无',
                    actions: [
                        userSelfInfo.wxOpenId ? <UpdateWxByPhoneModalForm key={"1"}/> :
                            <SetWxByPhoneModalForm key={"1"}/>
                    ]
                },

                {
                    title: '统一登录',
                    description: '便捷快速登录账号',
                    actions: [
                        <SetSingleSignInWxByPhoneModalForm key={"1"} userSelfInfo={userSelfInfo}/>,
                        <SetSingleSignInPhoneByPhoneModalForm key={"2"} userSelfInfo={userSelfInfo}/>
                    ]
                },

                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },

                {
                    title: UserSelfDeleteModalTitle,
                    description: userSelfInfo.createTime ? ('注册时间：' + userSelfInfo.createTime) : undefined,
                    actions: [
                        <UserSelfDeleteByPhoneModalForm key={"1"}/>
                    ]
                },

            ]

        } else { // 默认是：登录名

            dataSourceArr = [

                {
                    title: '密码',
                    actions: [
                        userSelfInfo.passwordFlag ? <UpdatePasswordBySignInNameModalForm key={"1"}/> : null
                    ]
                },

                {
                    title: '登录名',
                    description: userSelfInfo.signInName || '暂无',
                    actions: [
                        userSelfInfo.signInName ? <UpdateSignNameBySignInNameModalForm key={"1"}/> :
                            null
                    ]
                },

                {
                    title: '邮箱',
                    description: userSelfInfo.email || '暂无',
                    actions: [
                        userSelfInfo.email ? null : <SetEmailBySignInNameModalForm key={"1"}/>
                    ]
                },

                {
                    title: '手机号',
                    description: userSelfInfo.phone || '暂无',
                    actions: [
                        userSelfInfo.phone ? null : <SetPhoneBySignInNameModalForm key={"1"}/>
                    ]
                },

                {
                    title: '微信',
                    description: userSelfInfo.wxOpenId || '暂无',
                    actions: [
                        userSelfInfo.wxOpenId ? null : <SetWxBySignInNameModalForm key={"1"}/>
                    ]
                },

                {
                    title: '统一登录',
                    description: '便捷快速登录账号',
                    actions: [
                        <SetSingleSignInWxBySignInNameModalForm key={"1"} userSelfInfo={userSelfInfo}/>,
                        <SetSingleSignInPhoneBySignInNameModalForm key={"2"} userSelfInfo={userSelfInfo}/>
                    ]
                },

                {
                    title: RequestSelfLoginRecordModalTitle,
                    actions: [
                        <RequestSelfLoginRecordModal key={"1"}/>
                    ]
                },

                {
                    title: UserSelfDeleteModalTitle,
                    description: userSelfInfo.createTime ? ('注册时间：' + userSelfInfo.createTime) : undefined,
                    actions: [
                        <UserSelfDeleteBySignInNameModalForm key={"1"}/>
                    ]
                },

            ]

        }

        dataSourceMap.set(item.code!, dataSourceArr)

    })

    return dataSourceMap;

}

// 账号设置
export default function () {

    const userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    const [sysSignConfigurationVO, setSysSignConfigurationVO, sysSignConfigurationVORef] = MyUseState(useState<SysSignConfigurationVO>({}))

    useEffect(() => {

        setSysSignConfigurationVO(JSON.parse(MyLocalStorage.getItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO) || "{}"))

    }, [])

    const [dataSource, setDataSource] = useState<IUserSelfSetting[]>([]);

    const dataSourceMap = useMemo(() => {

        return GetDataSourceMap(userSelfInfo);

    }, [userSelfInfo]);

    const [sysUserAccountLevelEnum, setSysUserAccountLevelEnum] = MyUseState(useState<IEnum<number>>(DEFAULT_SYS_USER_ACCOUNT_LEVEL_ENUM), newState => {

        const dataSourceTemp = dataSourceMap.get(newState.code!)!;

        // 更新：页面显示
        setDataSource(dataSourceTemp)

    });

    useEffect(() => {

        if (userSelfInfo.tenantId) {

            // 租户相关配置
            SysTenantGetConfigurationById({value: userSelfInfo.tenantId}).then(res => {

                setSysSignConfigurationVO(res.data)

                MyLocalStorage.setItem(LocalStorageKey.SYS_SIGN_CONFIGURATION_VO, JSON.stringify(res.data))

            })

            // 设置：用户账户等级
            setSysUserAccountLevelEnum(GetSysUserAccountLevelEnum(userSelfInfo))

        }

    }, [userSelfInfo])

    return (

        <List<IUserSelfSetting>

            header={<Title level={5}>{USER_CENTER_KEY_TWO}</Title>}

            rowKey={"title"}

            dataSource={dataSource}

            renderItem={item => (

                <List.Item actions={item.actions}>

                    <List.Item.Meta
                        title={item.title}
                        description={item.description}
                    />

                </List.Item>

            )}

        />

    )

}

// 登录记录
function RequestSelfLoginRecordModal() {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [open, setOpen] = useState(false);

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <a onClick={() => {
                setOpen(true)
            }}>查看记录</a>

            <Modal

                width={1200}

                title={RequestSelfLoginRecordModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={null}

                className={"noFooterModal"}

            >

                <ProTable<SysRequestDO, SysRequestPageDTO>

                    rowKey={"id"}

                    columnEmptyText={false}

                    columnsState={{
                        value: columnsStateMap,
                        onChange: setColumnsStateMap,
                    }}

                    revalidateOnFocus={false}

                    scroll={fullScreenFlag ? undefined : {y: 440}}

                    columns={[

                        {
                            title: '序号',
                            dataIndex: 'index',
                            valueType: 'index',
                            width: 50,
                        },

                        {
                            title: '创建时间',
                            dataIndex: 'createTime',
                            sorter: true,
                            valueType: 'fromNow',
                            ellipsis: true,
                            hideInSearch: true,
                            width: 90,
                        },

                        {title: 'ip', dataIndex: 'ip', width: 120, ellipsis: true,},

                        {
                            title: 'ip位置',
                            dataIndex: 'region',
                            ellipsis: true,
                            copyable: true,
                            width: 180,
                            renderText: (text) => {
                                return HandlerRegion(text)
                            }
                        },

                        {
                            title: '来源',
                            dataIndex: 'category',
                            width: 120,
                            ellipsis: true,
                            fieldProps: {
                                allowClear: true,
                                showSearch: true,
                            },
                            valueEnum: SysRequestCategoryEnumDict,
                        },

                    ]}

                    pagination={{
                        showQuickJumper: true,
                        showSizeChanger: true,
                    }}

                    options={{
                        fullScreen: true,
                    }}

                    request={(params, sort, filter) => {
                        return SysRequestSelfLoginRecord({...params, sort})
                    }}

                />

            </Modal>

        </>

    )

}
