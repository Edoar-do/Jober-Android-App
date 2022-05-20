package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var bottomNavigationView: BottomNavigationView? = null

    private val offersFragment: Fragment? = null
    private val calendarFragment: Fragment? = null
    private val chatFragment: Fragment? = null
    private val optionsFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        offersFragment = OffersFragment()
        calendarFragment = CalendarFragment()
        chatFragment = ChatFragment()
        optionsFragment = OptionsFragment()

        setCurrentFragment(offersFragment)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.offers_item->setCurrentFragment(offersFragment)
                R.id.calendar_item->setCurrentFragment(calendarFragment)
                R.id.chat_item->setCurrentFragment(chatFragment)
                R.id.options_item->setCurrentFragment(optionsFragment)

            }
            true
        }

    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

}