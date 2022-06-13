package com.example.jober.adapters

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context : Context, attr_set : AttributeSet) : ViewPager(context,attr_set) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}