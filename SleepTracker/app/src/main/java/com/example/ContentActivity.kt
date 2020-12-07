package com.example

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.annotation.UiThread
import android.view.View
import com.example.tools.AlarmTools
import com.example.xutils.db.DbTool
import com.example.xutils.db.AlarmClock
import kotlinx.android.synthetic.main.layout_content.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.R.layout
import com.example.sleeptracker.R

class ContentActivity : BasicActivity() {
    var  mMediaPlayer = MediaPlayer();
    private var model = AlarmClock();
    private var id :String ="";
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_content)
        initViews();
    }

    private fun initViews(){
        id = intent.getStringExtra("ID").toString()
        toolbar.title="It's time "
        doAsync {
            model = DbTool.getDbManager().selector(AlarmClock::class.java).
            where("ID","=",id).findFirst();
            uiThread {
                if (model==null) return@uiThread
                note_text.text = model.note;
                stop_rt.visibility= View.VISIBLE;
                if (model.active == "1"){
                    initMediaPlayer();
                    stop_rt.setOnClickListener {
                        if (mMediaPlayer.isPlaying){
                            mMediaPlayer.stop()
                        }
                        finish();
                    }
                }
            }
        }
    }

    private fun initMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(this@ContentActivity, Uri.parse(model.sound));
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener {
                mMediaPlayer.start()
                mMediaPlayer.isLooping =true;
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        resetActiveType();
        mMediaPlayer.release();
    }

    private fun resetActiveType(){
        if(model!=null && model.repeatDay== SetClockActivity.WeekDAY.Never.name){
            model.active="0"
            DbTool.saveOrUpdate(model);
            AlarmTools.cancelAlarm(this,model);
        }
    }
}