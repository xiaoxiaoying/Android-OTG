package com.xiaoxiaoying.otg.adapter

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import com.orhanobut.logger.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class LoggerAdapter(private val context: Context) : LogAdapter {
    private val formatStrategy: FormatStrategy

    init {
        formatStrategy = if (true) {
            PrettyFormatStrategy.newBuilder().build()
        } else {
            val diskPath =
                context.externalCacheDir?.absolutePath ?: Environment.getExternalStorageDirectory()
                    .absolutePath
            val folder = diskPath + File.separatorChar + "logger"

            val ht = HandlerThread("AndroidFileLogger.$folder")
            ht.start()
            val handler: Handler = LoggerWriteHandler(
                ht.looper,
                folder,
                500 * 1024 // 500K averages to a 4000 lines per file
            )
            val logStrategy = DiskLogStrategy(handler)
            CsvFormatStrategy.newBuilder().logStrategy(logStrategy)
                .dateFormat(SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK))
                .build()

        }

    }

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return true
    }

    override fun log(priority: Int, tag: String?, message: String) {
        formatStrategy.log(priority, tag, message)
    }

}