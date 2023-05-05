package com.cmcorg20230301.engine.be.security.model.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.util.util.MyFileTypeUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 文件上传：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileUploadTypeEnum {

    // 头像
    AVATAR(101, "avatar", CollUtil.newHashSet("jpeg", "png", "jpg"), 1024 * 1024 * 2, true);

    @EnumValue
    @JsonValue
    private final int code; // 类型编码
    private final String folderName; // 文件夹名，备注：前后后不要加斜杠
    private final Set<String> acceptFileTypeSet; // 支持上传的文件类型（字母必须全小写），为 null则表示支持所有文件，为 空集合则表示不支持所有文件
    private final long maxFileSize; // 最大的文件大小 byte（包含），-1 则表示不限制大小 0 表示最大文件大小为0，则只能上传空文件
    private final boolean publicFlag; // 是否公开访问

    /**
     * 检查：文件类型，并返回文件类型（不含点），返回 null，则表示不支持此文件类型
     */
    @SneakyThrows
    @Nullable
    public String checkFileType(MultipartFile file) {

        // 获取文件类型
        String fileType = MyFileTypeUtil.getType(file.getInputStream(), file.getOriginalFilename());

        if (StrUtil.isBlank(fileType)) {
            return null;
        }

        // 获取：支持上传的文件类型
        Set<String> acceptFileTypeSet = getAcceptFileTypeSet();

        if (acceptFileTypeSet == null) {
            return fileType;
        }

        if (acceptFileTypeSet.size() == 0) {
            return null;
        }

        if (acceptFileTypeSet.contains(fileType.toLowerCase())) { // 转换为，小写，进行匹配
            return fileType;
        }

        return null;

    }

    /**
     * 检查：文件大小
     */
    public void checkFileSize(MultipartFile file) {

        long maxFileSize = getMaxFileSize();

        if (maxFileSize == -1) {
            return;
        }

        if (file.getSize() > maxFileSize) {
            ApiResultVO.error("操作失败：文件大小超过：【" + DataSizeUtil.format(maxFileSize) + "】，请重新选择");
        }

    }

}
