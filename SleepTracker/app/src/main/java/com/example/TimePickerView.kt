package com.example

import android.content.Context
import android.util.AttributeSet
import android.widget.TimePicker
import android.view.ViewParent
import android.view.MotionEvent

class TimePickerView(context: Context,attrs: AttributeSet ):TimePicker(context, attrs){

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            val p = parent
            p?.requestDisallowInterceptTouchEvent(true)
        }
        return false
    }

}