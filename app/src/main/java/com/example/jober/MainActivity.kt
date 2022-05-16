package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val offersFragment=OffersFragment()
        val calendarFragment=CalendarFragment()
        val chatFragment=ChatFragment()
        val optionsFragment=OptionsFragment()

        setCurrentFragment(offersFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

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