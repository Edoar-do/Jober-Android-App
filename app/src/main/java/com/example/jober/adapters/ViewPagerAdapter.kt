package com.example.jober.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter() {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    fun addFragment(fragments: Fragment, title: String) {
        mFragmentList.add(fragments)
        mFragmentTitleList.add(title)
    }

    fun getItemByTitle(title: String) : Fragment? {
        for (i in mFragmentTitleList.indices) {
            if (mFragmentTitleList[i] == title) {
                return mFragmentList[i]
            }
        }
        return null
    }
}