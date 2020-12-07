package com.example

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sleeptracker.R
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.xutils.db.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.sleeptracker.SleepTrackerFragment
import com.example.sleeptracker.databinding.FragmentSleepTrackerBinding
import kotlinx.android.synthetic.main.fragment_sleep_tracker.*
import kotlinx.android.synthetic.main.activity_set_clock.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {

    private var clockList = ArrayList<AlarmClock>();

    private lateinit var dialog: ProgressDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        

        recyclerView.layoutManager = LinearLayoutManager(this);

    }

    override fun onResume() {
        super.onResume()
        loadData();
    }

    internal inner class ClockListAdapter(val mDatas:List<AlarmClock>) :
        RecyclerView.Adapter<ClockListAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            var viewHolder= MyViewHolder(LayoutInflater.from(
                this@MainActivity).inflate(R.layout.layout_item_clolklist, parent,
                false))

            return viewHolder;
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder?.time_tv?.text = mDatas.get(position).time;
            holder?.day_tv?.text = mDatas.get(position).repeatDay;
            holder?.note_tv?.text = mDatas.get(position).note;
            holder?.clock_sw.isChecked =  if(mDatas.get(position).active =="1" )
                true else false;
            holder?.rootView.setOnClickListener {
                startActivity<SetClockActivity>(
                    Pair("TYPE" , "UPDATE"),
                    Pair("MODEL" , mDatas.get(position))
                );

            }
        }

        override fun getItemCount(): Int {
            return mDatas.size;
        }

        internal inner class MyViewHolder(view: View) : ViewHolder(view) {

            var time_tv: TextView
            var day_tv : TextView
            var note_tv :TextView
            var clock_sw :Switch
            var rootView :CardView
            init {
                time_tv = view.findViewById(R.id.time_text) as TextView
                day_tv = view.findViewById(R.id.day_text) as TextView
                note_tv = view.findViewById(R.id.note_text) as TextView
                clock_sw = view.findViewById(R.id.clock_switch) as Switch
                rootView = view.findViewById(R.id.rootView) as CardView
            }
        }


    }



    private fun loadData(){
        dialog = indeterminateProgressDialog("Loading");
        doAsync {
            clockList.clear();
            clockList.addAll(DbTool.getDbManager().selector(AlarmClock::class.java)
                .orderBy("UPDATE_TIME",true).findAll())
            uiThread {
                if(recyclerView.adapter == null)
                    recyclerView.adapter = ClockListAdapter(clockList);
                else
                    recyclerView.adapter!!.notifyDataSetChanged();

                if(dialog.isShowing) dialog.dismiss();
            }

        }
    }
}
