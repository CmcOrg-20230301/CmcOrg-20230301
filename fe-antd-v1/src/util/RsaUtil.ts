import {SHA256, SHA512} from 'crypto-js';
import JsEncrypt from 'jsencrypt';

const RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB"

// 统一的 password加密
export function PasswordRSAEncrypt(
    password: string,
    rsaPublicKey: string = RSA_PUBLIC_KEY
) {

    // 备注：512太长了，所以就 256/512混合
    return RSAEncrypt(
        SHA256(SHA512(password).toString()).toString(),
        rsaPublicKey
    )

}

// 非对称加密
export function RSAEncrypt(
    word: string,
    rsaPublicKey: string = RSA_PUBLIC_KEY
) {

    const jse = new JsEncrypt()

    jse.setPublicKey(rsaPublicKey) // 设置公钥

    const rsaEncrypt = jse.encrypt(word); // 进行非对称加密

    return rsaEncrypt || ''

}
