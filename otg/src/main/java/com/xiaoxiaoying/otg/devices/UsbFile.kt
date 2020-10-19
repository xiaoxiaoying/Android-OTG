package com.xiaoxiaoying.otg.devices

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
import com.orhanobut.logger.Logger
import com.xiaoxiaoying.otg.utils.MimeTypeUtil
import java.io.*

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class UsbFile(
    private val context: Context,
    private val file: File? = null,
    private val documentFile: DocumentFile? = null
) :
    Serializable {


    val name = if (file != null) file.name else documentFile?.name

    val isDirectory = file?.isDirectory ?: (documentFile?.isDirectory ?: false)
    val isFile = file?.isFile ?: (documentFile?.isFile ?: false)

    val length = file?.length() ?: (documentFile?.length() ?: 0)
    val lastModified = file?.lastModified() ?: (documentFile?.lastModified() ?: 0)

    val absolutePath = if (file != null)
        file.absolutePath else documentFile?.uri?.toString()

    val uri = documentFile?.uri
    val URI = file?.toURI()

    fun getParentUsbFile(): UsbFile? {
        return when {
            file != null -> {
                UsbFile(context, file.parentFile, null)
            }

            documentFile != null -> UsbFile(context, null, documentFile.parentFile)

            else -> null
        }
    }

    fun getInputStream(): InputStream? {
        return when {
            file != null -> {
                FileInputStream(file)
            }
            documentFile != null -> {
                context.contentResolver.openInputStream(documentFile.uri)
            }
            else -> null
        }
    }

    fun getOutputStream(): OutputStream? {
        return when {
            file != null -> {
                FileOutputStream(file)
            }
            documentFile != null -> {
                context.contentResolver.openOutputStream(documentFile.uri)
            }
            else -> null
        }
    }


    fun search(displayName: String): UsbFile? {
        return if (file != null) {
            val searchFile = file.search(displayName)
            if (searchFile == null)
                null
            else UsbFile(context, searchFile, null)
        } else if (documentFile != null) {
            val d = documentFile.findFile(displayName)
            if (d == null)
                null
            else UsbFile(context, null, d)
        } else null
    }

    fun match(displayName: String): UsbFile? {
        return when {
            file != null -> {
                val f = file.match(displayName)
                if (f == null)
                    null
                else UsbFile(context, f)
            }

            documentFile != null -> {
                val f = documentFile.match(displayName)
                if (f != null)
                    UsbFile(context, documentFile = f)
                else null
            }

            else -> null
        }
    }

    fun matchName(displayName: String): UsbFile? {
        return when {
            file != null -> {
                val f = file.match(displayName)
                if (f == null)
                    null
                else UsbFile(context, f)
            }

            documentFile != null -> {
                val f = documentFile.matchName(displayName)
                if (f != null)
                    UsbFile(context, documentFile = f)
                else null
            }

            else -> null
        }
    }

    fun delete(): Boolean {
        return file?.delete() ?: (documentFile?.delete() ?: false)
    }

    fun createDirectory(displayName: String): UsbFile? {
        return when {
            file != null -> {
                val createFile = File(file, displayName)
                if (!createFile.exists()) {
                    createFile.mkdirs()
                }
                UsbFile(context, createFile, null)
            }
            documentFile != null -> {
                UsbFile(context, null, documentFile.createDirectory(displayName))
            }
            else -> null
        }
    }

    fun createFile(displayName: String): UsbFile? {

        return when {
            file != null -> {
                val createFile = File(file, displayName)
                if (!createFile.exists()) {
                    createFile.createNewFile()
                }
                UsbFile(context, createFile, null)
            }
            documentFile != null -> {
                val createDocumentFile =
                    documentFile.createFile(displayName.getMimeType(), displayName)
                UsbFile(context, null, createDocumentFile)
            }
            else -> null
        }

    }

    private fun String.getMimeType(): String {
        val extension = MimeTypeUtil.getExtension(this)
        val hasExtension = MimeTypeUtil.hasExtension(extension)
        return if (hasExtension)
            MimeTypeUtil.guessMimeTypeFromExtension(extension)
        else MimeTypeUtil.MIME_TYPE_DEFAULT
    }

    fun listFiles(): Array<UsbFile>? {
        return if (file != null) {
            Logger.i("usb file list files file is null false path = ${file.absolutePath}")
            file.listUsbFile()
        } else documentFile?.listUsbFile()

    }


    private fun File.listUsbFile(): Array<UsbFile>? {
        return listFiles()?.map {
            Logger.i("it path = ${it.absolutePath}")
            return@map UsbFile(context, it, null)
        }?.toTypedArray()
    }

    private fun DocumentFile.listUsbFile(): Array<UsbFile>? {

        return listFiles().filter {
            !TextUtils.isEmpty(it.name)
        }.map {
            return@map UsbFile(context, null, it)
        }.toTypedArray()
    }

    private fun File.search(path: String): File? {
        listFiles()?.forEach {
            if (it.name == path)
                return it
        }

        return null
    }

    private fun File.match(name: String): File? {
        listFiles()?.forEach {
            val f = it.contains(name)
            if (f != null)
                return f
            else it.match(name)
        }

        return null
    }

    private fun File.contains(name: String): File? {
        listFiles()?.forEach {
            if (it.name.contains(name, ignoreCase = true))
                return it
        }
        return null
    }

    private fun DocumentFile.match(name: String): DocumentFile? {
        Logger.i("name === $name")
        name.split("/").filter { !TextUtils.isEmpty(it) }.forEach {
            Logger.i("it  === $it")
            listFiles().forEach { file ->
                Logger.i("file name = ${file.name} $it    ${file.name == it}")

                if (file.name == it) {
                    val n = name.replace(if (name.contains("/")) "$it/" else it, "")
                    return if (!TextUtils.isEmpty(n))
                        file.match(n)
                    else file
                }
            }
        }
        return null
    }

    private fun DocumentFile.matchName(name: String): DocumentFile? {
        Logger.i("document file name === $name")
        name.split("/").filter { !TextUtils.isEmpty(it) }.forEach {
            Logger.i("document it  === $it")
            listFiles().forEach { file ->
                Logger.i(
                    "document file name = ${file.name} $it    ${file.name?.contains(
                        it,
                        ignoreCase = true
                    )}"
                )
                if (file.isFile) {
                    if (file.name?.contains(it, ignoreCase = true) == true) {
                        return file
                    }
                } else if (file.isDirectory) {
                    val document = file.matchName(name)
                    if (document != null)
                        return document
                }

            }
        }
        return null
    }


    private fun DocumentFile.contains(name: String): DocumentFile? {
        listFiles().forEach {
            if (it.name?.contains(name, ignoreCase = true) == true)
                return it
        }
        return null
    }

    fun exist(path: String): UsbFile? {
        return when {
            file != null -> {
                val exist = File(file.absolutePath + File.separator + path)
                if (exist.exists()) {
                    UsbFile(context, exist, null)
                } else null
            }
            documentFile != null -> {
                val existDocument = documentFile.match(path)
                if (existDocument?.exists() == true) {
                    UsbFile(context, null, existDocument)
                } else null
            }
            else -> null
        }

    }

    fun contains(name: String): UsbFile? {
        return when {
            file != null -> {
                val contains = file.contains(name)
                if (contains != null)
                    UsbFile(context, file)
                else null
            }
            documentFile != null -> {
                val contains = documentFile.contains(name)
                if (contains != null)
                    UsbFile(context, documentFile = contains)
                else null
            }
            else -> null
        }
    }

    override fun toString(): String {
        return "UsbFile( file=${file.toString()}, documentFile=${documentFile.toString()}, name=$name, isDirectory=$isDirectory, isFile=$isFile, length=$length, lastModified=$lastModified, absolutePath=$absolutePath, uri=${uri.toString()
        }, URI=${URI.toString()})"
    }


}