package com.example.jober

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.jober.adapters.CustomViewPager
import com.example.jober.fragments.*
import com.example.jober.model.Application
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
//    private lateinit var vpViewPager: CustomViewPager
    private lateinit var flWrapper : FrameLayout

    private lateinit var mAuth : FirebaseAuth
    private lateinit var m_db_ref: DatabaseReference
    private lateinit var storage_ref : StorageReference
    private lateinit var database : FirebaseDatabase
    private lateinit var adapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        vpViewPager = findViewById(R.id.vpViewPager)
        flWrapper = findViewById(R.id.flWrapper)

        mAuth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        var user_type : String? = null

        m_db_ref.child("userSettings").child(mAuth.currentUser?.uid!!).get().addOnSuccessListener {
            val user_settings = it.getValue(UserSettings::class.java)
            user_type = user_settings?.user_type!!
            val starting_fragment = intent.getStringExtra("fragment")

            adapter = ViewPagerAdapter()
            adapter.addFragment(OffersFragment(), "Offers")
            adapter.addFragment(CalendarFragment(), "Calendar")
            adapter.addFragment(ChatFragment(), "Chats")

            if(user_type.equals("worker")) {
                adapter.addFragment(WorkerOptionsFragment(), "Options")
                adapter.addFragment(WorkerProfileFragment(), "WorkerProfile")
                adapter.addFragment(WorkerApplicationsFragment(), "WorkerApplications")
            } else if(user_type.equals("company")) {
                adapter.addFragment(CompanyOptionsFragment(), "Options")
                adapter.addFragment(CompanyProfileFragment(), "CompanyProfile")
                adapter.addFragment(CompanyOffersFragment(), "CompanyOffers")
            }

            bottomNavigationView.setSelectedItemId(R.id.options_item)
            print("############################# CHIAMATO ONCREATE DELLA MAIN ACTIVITY")


            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.offers_item -> {
                        changeFragment(adapter.getItemByTitle("Offers")!!, null)
                    }
                    R.id.calendar_item -> {
                        changeFragment(adapter.getItemByTitle("Calendar")!!, null)
                    }
                    R.id.chat_item -> {
                        changeFragment(adapter.getItemByTitle("Chats")!!, null)
                    }
                    R.id.options_item -> {
                        changeFragment(adapter.getItemByTitle("Options")!!, null)
                    }
                }
                true
            }

            setFragmentByTitle("Options", null)
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


    fun setFragmentByTitle(fragment_name : String, bundle : Bundle?) {
        val fragment = adapter.getItemByTitle(fragment_name)
        when (fragment_name) {
            "Options" -> bottomNavigationView.selectedItemId = R.id.options_item
            "Chats" -> bottomNavigationView.selectedItemId = R.id.chat_item
            "Calendar" -> bottomNavigationView.selectedItemId = R.id.calendar_item
            "Offers" -> bottomNavigationView.selectedItemId = R.id.offers_item
            else -> changeFragment(fragment!!, bundle)
        }
    }

    fun changeFragment(fragment : Fragment, bundle : Bundle?) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flWrapper, fragment!!)
            setReorderingAllowed(true)
            addToBackStack("fragment_change_transaction") // name can be null
            commit()
        }
    }

    fun clearFragmentsBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val entry = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.flWrapper)
        if (currentFragment is WorkerOptionsFragment || currentFragment is CompanyOptionsFragment) {
            clearFragmentsBackStack()
            super.onBackPressed()
        } else if (currentFragment is ChatFragment || currentFragment is CalendarFragment || currentFragment is OffersFragment){
            clearFragmentsBackStack()
            bottomNavigationView.selectedItemId = R.id.options_item
        } else {
            super.onBackPressed()
        }
    }

    fun setBottomNavigationItem(item : Int) {
        bottomNavigationView.setSelectedItemId(item)
    }

}