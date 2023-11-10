package com.cmcorg20230301.be.engine.file.base.model.configuration;

import java.util.Set;

public interface ISysFileRemove {

    /**
     * 当文件进行移除时
     */
    void handle(Set<Long> removeFileIdSet);

}
