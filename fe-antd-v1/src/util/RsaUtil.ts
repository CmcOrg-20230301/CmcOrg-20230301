import {SHA256, SHA512} from 'crypto-js';
import JsEncrypt from 'jsencrypt';

const RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB"

// 统一的 password加密
export function PasswordRSAEncrypt(
    password: string,
    date: Date = new Date(),
    rsaPublicKey: string = RSA_PUBLIC_KEY
) {

    // 备注：SHA512方法，入参太长了会报错，所以就先 512，然后再 256
    return RSAEncryptPro(
        SHA256(SHA512(password).toString()).toString(),
        date,
        rsaPublicKey
    )

}

// 非对称加密
function RSAEncrypt(
    word: string,
    rsaPublicKey: string = RSA_PUBLIC_KEY
) {

    const jse = new JsEncrypt()

    jse.setPublicKey(rsaPublicKey) // 设置公钥

    const rsaEncrypt = jse.encrypt(word); // 进行非对称加密

    return rsaEncrypt || ''

}

// 非对称加密：增强版，加入时间戳
export function RSAEncryptPro(
    word: string,
    date: Date = new Date(),
    rsaPublicKey: string = RSA_PUBLIC_KEY,
) {

    const timestamp = ';' + date.getTime() // 加入：时间戳
    return RSAEncrypt(word + timestamp, rsaPublicKey) // 加入时间戳，进行非对称加密

}
