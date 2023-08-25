package com.cmcorg20230301.be.engine.util.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBack<T> {

    private T value;

}
