package com.xiaoxiaoying.otg.devices

import android.text.TextUtils
import java.io.Serializable

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class DiskInfo(
    var label: String = "",
    var totalSpace: Long = 0,
    var freeSpace: Long = 0,
    var flags: String = "",
    var diskId: String = ""

) : Serializable {

    companion object {

        fun String.toDiskInfo(): DiskInfo {
            val diskInfo = DiskInfo()
            this.split(" ").filter {
                !TextUtils.isEmpty(it)
            }.forEach {

                if (it.contains("}:")) {
                    diskInfo.diskId = it.replace("DiskInfo{", "").replace("}:", "").trim()
                }

                val values = it.split("=").filter { item -> !TextUtils.isEmpty(item) }
                if (values.size >= 2) {
                    val key = values[0]
                    val value = values[1]
                    when (key) {
                        "flags" -> diskInfo.flags = value
                        "size" -> try {
                            diskInfo.totalSpace = value.toLong()
                        } catch (e: Exception) {
                        }

                        "label" -> diskInfo.label = value

                    }
                }
            }
            return diskInfo
        }
    }



}