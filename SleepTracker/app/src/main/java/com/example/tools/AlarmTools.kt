package com.example.tools

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.AlockApp
import com.example.AlarmReceiver
import com.example.SetClockActivity
import com.example.xutils.db.AlarmClock
import java.util.*

class AlarmTools {

    companion object {

        private var alarmManager = AlockApp.instance().getSystemService(Context.ALARM_SERVICE) as AlarmManager;

        fun setAlarm(context :Context , model: AlarmClock){
            var triggerAtTime = System.currentTimeMillis();
            if (model.repeatDay== SetClockActivity.WeekDAY.Never.name){
                var calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, model.time.substring(0,model.time.indexOf(":")).toInt());
                calendar.set(Calendar.MINUTE,model.time.substring(model.time.indexOf(":")+1).toInt());
                calendar.set(Calendar.SECOND,0);
                var intent = Intent(context, AlarmReceiver::class.java);
                intent.action = AlockApp.instance().packageName;
                intent.putExtra("ID",model.alarmId);
                var pi = PendingIntent.getBroadcast(context,0,intent,0);
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pi);
            }
        }

        fun cancelAlarm(context :Context , model: AlarmClock){
            if (model.repeatDay== SetClockActivity.WeekDAY.Never.name){
                var intent = Intent(context, AlarmReceiver::class.java);
                intent.action = AlockApp.instance().packageName;
                intent.putExtra("ID",model.alarmId);
                var pi = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE);
                if(pi !=null){
                    alarmManager.cancel(pi)
                }
            }
        }
    }
}