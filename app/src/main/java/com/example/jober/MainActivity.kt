package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.jober.fragments.ViewPagerAdapter
import com.example.tablayout.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var vpViewPager: ViewPager

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        vpViewPager = findViewById(R.id.vpViewPager)

        mAuth = FirebaseAuth.getInstance()

        val starting_fragment = intent.getStringExtra("fragment")

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(OffersFragment(), "Offers")
        adapter.addFragment(CalendarFragment(), "Calendar")
        adapter.addFragment(ChatFragment(), "Chats")
        adapter.addFragment(WorkerOptionsFragment(), "Options")
        adapter.addFragment(WorkerProfileFragment(), "WorkerProfile")
        vpViewPager.adapter = adapter

//        setCurrentFragment(adapter.getItemByTitle("Offers"))
        vpViewPager.setCurrentItem(adapter.getIndexByTitle(starting_fragment!!)!!)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.offers_item->vpViewPager.setCurrentItem(0)
                R.id.calendar_item->vpViewPager.setCurrentItem(1)
                R.id.chat_item->vpViewPager.setCurrentItem(2)
                R.id.options_item->vpViewPager.setCurrentItem(3)
            }
            true
        }

        bottomNavigationView.setSelectedItemId(R.id.options_item)
    }

    fun logout() {
        mAuth.signOut()
        val intent = Intent(this@MainActivity, Login::class.java)
        finish()
        startActivity(intent)
    }

    fun setFragmentByTitle(title: String) {
        vpViewPager.setCurrentItem((vpViewPager.adapter as ViewPagerAdapter).getIndexByTitle(title)!!)
    }

/*    private fun setCurrentFragment(fragment:Fragment?)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.vpViewPager, fragment!!)
            commit()
        }*/

}