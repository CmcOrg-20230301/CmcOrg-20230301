import React, {Dispatch, useRef} from "react";

/**
 * 使用方法，例如：const [signInType, setSignInType, signInTypeRef] = MyUseState(useState<string>(''));
 * 用于：在定时任务中，无法获取 useState最新的值，那么就可以从：ref.current中获取
 */
export function MyUseState<S>(useStateTemp: [S, Dispatch<S>], callBack?: (newState: S) => void, deepCopyFun?: (newState: S) => S): [S, Dispatch<S>, React.MutableRefObject<S>] {

    const ref = useRef<S>(useStateTemp[0]);

    return [useStateTemp[0], (newState) => {

        if (deepCopyFun) { // 深度拷贝

            ref.current = deepCopyFun(newState)

        } else {

            ref.current = newState

        }

        useStateTemp[1](newState)

        if (callBack) { // 回调

            callBack(newState)

        }

    }, ref];

}