export interface UserSelfUpdateInfoDTO {
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$
    bio?: string // 个人简介
}
