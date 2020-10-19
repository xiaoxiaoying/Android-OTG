package com.xiaoxiaoying.otg.devices

import java.io.BufferedInputStream
import java.io.BufferedOutputStream

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
object UsbFileStreamFactory {
    @JvmStatic
    fun createBufferedOutputStream(file: UsbFile?): BufferedOutputStream? {
        val output = file?.getOutputStream() ?: return null
        return BufferedOutputStream(output)
    }

    @JvmStatic
    fun createBufferedInputStream(file: UsbFile?): BufferedInputStream? {
        val input = file?.getInputStream() ?: return null
        return BufferedInputStream(input)
    }
}