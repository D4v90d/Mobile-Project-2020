package com.example

import androidx.appcompat.app.AppCompatActivity
import com.example.sleeptracker.R
import kotlinx.android.synthetic.main.layout_toolbar.*

open class BasicActivity : AppCompatActivity() {

    open protected fun setHasBack(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener { finish(); }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }
}