package com.xiaoxiaoying.otg

import android.app.Application
import com.orhanobut.logger.Logger
import com.xiaoxiaoying.otg.adapter.LoggerAdapter

/**
 * @author xiaoxiaoying
 * @date 2020/10/19
 */
class OtgApplication : Application()
{
    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(LoggerAdapter(this))

    }
}