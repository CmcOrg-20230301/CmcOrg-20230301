package com.cmcorg20230301.be.engine.security.model.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ISysFileUploadType {

    int getCode(); // 建议从：10001（包含）开始

    String getFolderName();

    Set<String> getAcceptFileTypeSet();

    long getMaxFileSize();

    boolean isPublicFlag();

    String checkFileType(MultipartFile file);

    void checkFileSize(MultipartFile file);

}
