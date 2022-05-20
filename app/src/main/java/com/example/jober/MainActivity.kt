package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.jober.fragments.ViewPagerAdapter
import com.example.tablayout.fragments.CalendarFragment
import com.example.tablayout.fragments.ChatFragment
import com.example.tablayout.fragments.OffersFragment
import com.example.tablayout.fragments.OptionsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var vpViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        vpViewPager = findViewById(R.id.vpViewPager)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(OffersFragment(), "Offers")
        adapter.addFragment(CalendarFragment(), "Calendar")
        adapter.addFragment(ChatFragment(), "Chats")
        adapter.addFragment(OptionsFragment(), "Options")
        vpViewPager.adapter = adapter

//        setCurrentFragment(adapter.getItemByTitle("Offers"))
        vpViewPager.setCurrentItem(0)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.offers_item->vpViewPager.setCurrentItem(0)
                R.id.calendar_item->vpViewPager.setCurrentItem(1)
                R.id.chat_item->vpViewPager.setCurrentItem(2)
                R.id.options_item->vpViewPager.setCurrentItem(3)
            }
            true
        }
    }

/*    private fun setCurrentFragment(fragment:Fragment?)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.vpViewPager, fragment!!)
            commit()
        }*/

}