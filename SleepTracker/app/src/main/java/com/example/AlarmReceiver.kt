package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.xutils.db.AlarmClock
import org.jetbrains.anko.startActivity

class AlarmReceiver :BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!!.action == AlockApp.instance().packageName){
            var intentContent = Intent(context,ContentActivity::class.java)
            intentContent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentContent.putExtra("ID",intent.getStringExtra("ID"))
            context!!.startActivity(intentContent);
        }
    }
}