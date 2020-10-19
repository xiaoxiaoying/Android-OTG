package com.xiaoxiaoying.otg.devices

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.Serializable

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class UsbDevices() : Serializable {

    companion object {
        fun String.toUsbDevices(): UsbDevices {
            val devices = UsbDevices()

            this.split(" ").filter {
                !TextUtils.isEmpty(it)
            }.forEach {
                val values = it.split("=").filter { item -> !TextUtils.isEmpty(item) }
                if (values.size >= 2) {
                    val key = values[0]
                    val value = values[1]
                    when (key) {
                        "mDescription" -> devices.description = value
                        "mRemovable" -> try {
                            devices.removable = value.toBoolean()
                        } catch (e: Exception) {
                            com.orhanobut.logger.Logger.e(e.toString())
                        }

                        "mFsUuid" -> devices.fsUuid = value
                        "mState" -> devices.state = value
                        "mId" -> devices.id = value

                    }
                }
            }

            return devices
        }
    }

    var id: String = ""
    var diskInfo: DiskInfo = DiskInfo()
    var volumeInfo: VolumeInfo = VolumeInfo()
    var removable = false
    var fsUuid: String = ""
    var description: String = ""
    var state: String = ""
    var path: String = ""
    var totalSpace: Long = 0
    var freeSpace: Long = 0
    var occupiedSpace: Long = 0

    /**
     * 如果
     * @sample [rwPath] == [VolumeInfo.path] val file = File(rwPath)
     * 否则使用
     * @sample [DocumentFile.fromTreeUri]
     */
    var rwPath: String = ""
    var hasPermission = !TextUtils.isEmpty(rwPath)

    private var usbFile: UsbFile? = null
    fun isUsb(): Boolean = diskInfo.flags.contains("USB", ignoreCase = true)


    private fun getDocumentFile(context: Context): DocumentFile? {
        return if (!TextUtils.isEmpty(rwPath) && rwPath.startsWith("content://")) {
            DocumentFile.fromTreeUri(context, Uri.parse(rwPath))
        } else null
    }

    private fun getFile(): File? {
        val file = File(rwPath)
        return if (file.exists() && rwPath == path)
            file else null
    }

    fun getUsbFile(context: Context): UsbFile = usbFile ?: synchronized(this) {
        usbFile ?: UsbFile(context, getFile(), getDocumentFile(context)).also {
            usbFile = it
        }
    }

    override fun toString(): String {
        return "UsbDevices(id='$id', diskInfo=${diskInfo.toString()}, " +
                "volumeInfo=${volumeInfo.toString()}, removable=$removable, fsUuid='$fsUuid', " +
                "description='$description', state='$state', path='$path', totalSpace=$totalSpace," +
                " freeSpace=$freeSpace, rwPath='$rwPath', hasPermission=$hasPermission)"
    }


}