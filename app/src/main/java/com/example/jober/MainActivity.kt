package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.jober.fragments.*
import com.example.jober.model.UserSettings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var vpViewPager: ViewPager

    private lateinit var mAuth : FirebaseAuth
    private lateinit var m_db_ref: DatabaseReference
    private lateinit var storage_ref : StorageReference
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        vpViewPager = findViewById(R.id.vpViewPager)

        mAuth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        var user_type : String? = null

        m_db_ref.child("userSettings").child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            val user_settings = it.getValue(UserSettings::class.java)
            user_type = user_settings?.user_type!!
            val starting_fragment = intent.getStringExtra("fragment")

            val adapter = ViewPagerAdapter(supportFragmentManager)
            adapter.addFragment(OffersFragment(), "Offers")
            adapter.addFragment(CalendarFragment(), "Calendar")
            adapter.addFragment(ChatFragment(), "Chats")

            if(user_type.equals("worker")) {
                //println("SONO UN WORKER ########################################################")
                adapter.addFragment(WorkerOptionsFragment(), "Options")
                adapter.addFragment(WorkerProfileFragment(), "WorkerProfile")
            } else if(user_type.equals("company")) {
                //println("SONO UNA COMPANY ########################################################")
                adapter.addFragment(CompanyOptionsFragment(), "Options")
                adapter.addFragment(CompanyProfileFragment(), "CompanyProfile")
            }

            vpViewPager.adapter = adapter

//        setCurrentFragment(adapter.getItemByTitle("Offers"))
            //println("starting fragment:" + adapter.getIndexByTitle(starting_fragment!!)!!)
            vpViewPager.setCurrentItem(adapter.getIndexByTitle(starting_fragment!!)!!)

            bottomNavigationView.setSelectedItemId(R.id.options_item)

            bottomNavigationView.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.offers_item->vpViewPager.setCurrentItem(0)
                    R.id.calendar_item->vpViewPager.setCurrentItem(1)
                    R.id.chat_item->vpViewPager.setCurrentItem(2)
                    R.id.options_item->vpViewPager.setCurrentItem(3)
                }
                true
            }


        }.addOnFailureListener{
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG)
            finish()
        }
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