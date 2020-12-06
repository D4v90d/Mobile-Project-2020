package com.example

import android.app.Application
import com.example.xutils.db.DbTool
import com.example.xutils.db.T_ALARM_CLOCK
import org.xutils.x
import kotlin.properties.Delegates

class AlockApp :Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this;
        x.Ext.init(instance);
        x.Ext.setDebug(false);
    }

    companion object {
        private var instance : AlockApp by Delegates.notNull();
        fun instance() = instance;
    }
}