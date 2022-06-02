package com.example.jober.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager): FragmentPagerAdapter(supportFragmentManager,
BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

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

    fun getIndexByTitle(title : String) : Int? {
        for(i in mFragmentList.indices){
            println("index $i     , title: " + mFragmentTitleList[i])
        }
        for (i in mFragmentTitleList.indices) {
            if (mFragmentTitleList[i] == title) {
                return i
            }
        }
        return 0
    }

}