package com.example.xutils.db

import android.media.RingtoneManager
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.AlockApp
import kotlinx.android.parcel.Parcelize
import org.xutils.db.annotation.Column
import org.xutils.db.annotation.Table


@Parcelize
@Entity(tableName = "alarm_clock_table")
data class AlarmClock(
    @PrimaryKey( autoGenerate = true)
    var alarmId:String="",

    @Column(name = "TIME")
    var time:String="",

    @Column(name = "REPEAT_DAY")
    var repeatDay:String="Never",

    @Column(name = "NOTE")
    var note:String="",

    @Column(name = "SOUND")
    var sound:String=RingtoneManager.
    getActualDefaultRingtoneUri(
        AlockApp.instance(),RingtoneManager.TYPE_RINGTONE).toString(),

    @Column(name = "UPDATE_TIME")
    var updateTime:String="",

    @Column(name = "ACTIVE")
    var active:String="1"

) : Parcelable {

    /* @Column(name = "ID",isId = true)
     private var id:String=ID;
     @Column(name = "TIME")
     private var time:String=TIME;
     @Column(name = "REPEAT_DAY")
     private var repeat_day:String=REPEAT_DAY;
     @Column(name = "NOTE")
     private var note:String=NOTE;
     @Column(name = "SOUND")
     private var sound:String=SOUND;
     @Column(name = "UPDATE_TIME")
     private var update_time:String=UPDATE_TIME;
 */
}