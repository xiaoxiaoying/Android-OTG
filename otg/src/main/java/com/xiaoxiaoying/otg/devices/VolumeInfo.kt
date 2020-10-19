package com.xiaoxiaoying.otg.devices

import android.text.TextUtils
import java.io.Serializable

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class VolumeInfo(
    var path: String = "",
    var fsType: String = "",
    var fsLabel: String = "",
    var state: String = "MOUNTED",
    var fsUuid: String = "",
    var internalPath: String = "",
    var diskId: String = ""
) : Serializable {

    companion object {

        fun String.toVolumeInfo(): VolumeInfo {
            val volumeInfo = VolumeInfo()
            this.split(" ").filter {
                !TextUtils.isEmpty(it)
            }.forEach {
                if (it.contains("}:")) {
                    volumeInfo.id = it.replace("VolumeInfo{", "").replace("}:", "").trim()
                }
                val values = it.split("=").filter { item -> !TextUtils.isEmpty(item) }
                if (values.size >= 2) {
                    val key = values[0]
                    val value = values[1]
                    when (key) {
                        "path" -> volumeInfo.path = value
                        "internalPath" -> volumeInfo.internalPath = value
                        "fsType" -> volumeInfo.fsType = value
                        "fsUuid" -> volumeInfo.fsUuid = value
                        "fsLabel" -> volumeInfo.fsLabel = value
                        "state" -> volumeInfo.state = value
                        "diskId" -> volumeInfo.diskId = value
                    }
                }
            }

            return volumeInfo
        }

    }

    var id: String = ""

    override fun toString(): String {
        return "VolumeInfo(path='$path', fsType='$fsType', fsLabel='$fsLabel', state='$state', fsUuid='$fsUuid', internalPath='$internalPath')"
    }


}