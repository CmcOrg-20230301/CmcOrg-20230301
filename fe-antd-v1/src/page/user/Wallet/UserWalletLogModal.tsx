import React, {useState} from "react";
import {Modal} from "antd";
import {UserWalletLogModalTitle} from "@/page/user/Wallet/UserWallet";
import WalletLog, {IUserWalletLog} from "@/page/sys/wallet/WalletLog/WalletLog.tsx";

interface IUserWalletLogModal extends IUserWalletLog {

    title?: string

}

// 钱包日志
export default function (props: IUserWalletLogModal) {

    const [open, setOpen] = useState(false);

    return (

        <>

            <a className={"m-l-20 f-14"} onClick={() => {
                setOpen(true)
            }}>{props.title || UserWalletLogModalTitle}</a>

            <Modal

                width={1300}

                title={props.title || UserWalletLogModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={null}

                className={"noFooterModal"}

                destroyOnClose={true}

            >

                <WalletLog {...props} allPageFlag={false}/>

            </Modal>

        </>

    )

}
