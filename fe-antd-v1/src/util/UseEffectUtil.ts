import {useEffect} from "react";

// 监听是否：全屏
export function UseEffectFullScreenChange(setFullScreenFlag: (value: (((prevState: boolean) => boolean) | boolean)) => void) {

    useEffect(() => {

        const handleFullScreenChange = () => {
            setFullScreenFlag(document.fullscreenElement !== null)
        }

        document.addEventListener('fullscreenchange', handleFullScreenChange);

        return () => {

            document.removeEventListener('fullscreenchange', handleFullScreenChange);

        }

    }, [])

}
