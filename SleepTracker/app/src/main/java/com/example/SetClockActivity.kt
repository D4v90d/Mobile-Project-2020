package com.example

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.R.*
import kotlinx.android.synthetic.main.content_set_clock.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.widget.EditText
import com.example.sleeptracker.R
import com.example.tools.AlarmTools
import com.example.tools.FileTools
import com.example.xutils.db.DbTool
import com.example.xutils.db.T_ALARM_CLOCK
import java.text.SimpleDateFormat
import java.util.*

class SetClockActivity :BasicActivity() {

    private val TYPE_ADD :String = "ADD"

    private val TYPE_UPDATE :String = "UPDATE"
    //判断是新增还是修改,默认新增ADD
    private var TYPE : String = TYPE_ADD;

    private var model = T_ALARM_CLOCK();

    //用于选择铃声后作相应的判断标记
    private val REQUEST_CODE_PICK_RINGTONE = 1
    //保存铃声的Uri的字符串形式
    private var mRingtoneUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_clock);
        initViews();
    }

    fun initViews(){
        toolbar.title = "Time Setting";
        setHasBack();
        timepicker.setIs24HourView(true);
        card_repeat.setOnClickListener { alertDaySelect(); }
        card_note.setOnClickListener { alert_edit(); }
        card_sound.setOnClickListener { doPickPingtone(); }
        repeat_text.text = model.REPEAT_DAY;
        note_text.text = model.NOTE;
        sound_text.text =
            FileTools.getFileName(FileTools.getRealPath(this,Uri.parse(model.SOUND)),"No Default Ringtone");
        val bundle = intent.extras;
        if (bundle != null) {
            TYPE = bundle.getString("TYPE").toString()
        }
        if(TYPE==TYPE_UPDATE){
            model = bundle!!.getParcelable("MODEL")!!;
            timepicker.currentHour = model.TIME.substring(0,model.TIME.indexOf(":")).toInt()
            timepicker.currentMinute = model.TIME.substring(model.TIME.indexOf(":")+1).toInt();
            repeat_text.text = model.REPEAT_DAY;
            note_text.text = model.NOTE;
            sound_text.text= FileTools.getFileName(FileTools.getRealPath(this, Uri.parse(model.SOUND)));
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_set_clock, menu)
        if (TYPE==TYPE_ADD){
            menu.findItem(R.id.action_delete).setVisible(false);
            model.ID = UUID.randomUUID().toString();
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_confirm -> menuSaveClock();
            R.id.action_delete -> menuDeleteClock();
        }

        return true;
    }

    /***
     * 展示日期选择对话框
     */
    private fun alertDaySelect(){
        val dayList = arrayOf<CharSequence>(
            WeekDAY.Monday.dayName,WeekDAY.Tuesday.dayName,WeekDAY.Wednesday.dayName,WeekDAY.Thursday.dayName,
            WeekDAY.Friday.dayName,WeekDAY.Saturday.dayName,WeekDAY.Sunday.dayName);

        var daySelected = booleanArrayOf(false,false,false,false,false,false,false);
        if (!model.REPEAT_DAY.isNullOrBlank()){
            daySelected = booleanArrayOf(model.REPEAT_DAY.contains(WeekDAY.Monday.dayName),
                model.REPEAT_DAY.contains(WeekDAY.Tuesday.dayName),model.REPEAT_DAY.contains(WeekDAY.Wednesday.dayName),
                model.REPEAT_DAY.contains(WeekDAY.Thursday.dayName),model.REPEAT_DAY.contains(WeekDAY.Friday.dayName),
                model.REPEAT_DAY.contains(WeekDAY.Saturday.dayName),model.REPEAT_DAY.contains(WeekDAY.Sunday.dayName));
        }

        var newSelected :String = "" ;
        val daySelectDialog = AlertDialog.Builder(this).setTitle("Please Select Repeat Alarm Date")
            .setMultiChoiceItems(dayList,daySelected,
                DialogInterface.OnMultiChoiceClickListener {
                        dialog, which, isChecked ->
                })
            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                    dialog, which ->
                for (i in daySelected.indices){
                    newSelected += if (daySelected[i]) dayList.get(i) else "";
                }
                model.REPEAT_DAY = if(newSelected.isNullOrBlank()) WeekDAY.Never.name else newSelected;
                repeat_text.text  = model.REPEAT_DAY;
            })
            .setNegativeButton("Cancel",null);
        daySelectDialog.show();
    }

    public enum class WeekDAY(val dayName:String){
        Never("Never"),
        Monday("Every Monday"),
        Tuesday("Every Tuesday"),
        Wednesday("Every Wednesday"),
        Thursday("Every Thursday"),
        Friday("Every Friday"),
        Saturday("Every Saturday"),
        Sunday("Every Sunday")
    }

    private fun alert_edit() {
        val et = EditText(this)
        et.setSingleLine(true);
        et.setText(model.NOTE)
        AlertDialog.Builder(this).setTitle("Please Enter a Label")
            .setView(et)
            .setPositiveButton("Confirm") { dialogInterface, i ->
                model.NOTE = et.text.toString();
                note_text.text = model.NOTE;
            }.setNegativeButton("Cancel", null).show()
    }

    private fun doPickPingtone(){
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Choose Ringtone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
            RingtoneManager.TYPE_RINGTONE);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        val ringtoneUri: Uri;
        ringtoneUri = mRingtoneUri?:RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // Put checkmark next to the current ringtone for this contact
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri)
        // Launch!
        // startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
        startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?:return;
        try {
            val pickedUri = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            mRingtoneUri = pickedUri;
            model.SOUND=mRingtoneUri.toString();
            sound_text.text= FileTools.getFileName(FileTools.getRealPath(this, Uri.parse(model.SOUND)))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun menuSaveClock(){
        var selectTime = (if(timepicker.currentHour<10) "0"+timepicker.currentHour else
            ""+timepicker.currentHour)+":"+ (if(timepicker.currentMinute<10) "0"+timepicker.currentMinute else
            ""+timepicker.currentMinute)
        model.TIME = selectTime;
        model.ACTIVE = "1";
        model.UPDATE_TIME = SimpleDateFormat("yyyy-MM-dd HH:mm:ss" ).format(Date());
        DbTool.saveOrUpdate(model);
        setAlarmClock();
        finish();
    }


    private fun menuDeleteClock(){
        DbTool.delete(model as Object);
        finish();
    }


    private fun setAlarmClock(){
        AlarmTools.setAlarm(this,model);


    }
}