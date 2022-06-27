package com.example.jober.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.MainActivity
import com.example.jober.R
import com.example.jober.adapters.EventAdapter
import com.example.jober.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class CalendarFragment : Fragment() {

    lateinit var events_recycler_view : RecyclerView
    lateinit var search_view : SearchView

    lateinit var event_list : ArrayList<Event>
    lateinit var other_pics: ArrayList<Bitmap>
    lateinit var other_names : ArrayList<String>
    lateinit var position_list : ArrayList<String>

    lateinit var event_adapter : EventAdapter

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var valueEventListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        event_list = ArrayList()
        other_pics = ArrayList()
        other_names = ArrayList()
        position_list = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        (activity as MainActivity).supportActionBar!!.title = "Jober - Events"

        event_adapter = EventAdapter(view.context, event_list, other_pics, other_names, position_list)

        events_recycler_view = view.findViewById(R.id.recyclerview)
        events_recycler_view.layoutManager = LinearLayoutManager(view.context)
        events_recycler_view.adapter = event_adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        val user_id = (m_auth.currentUser?.uid!!)

        search_view = view.findViewById(R.id.searchView)
        search_view.clearFocus()
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    event_adapter.setFilteredLists(event_list, other_names, other_pics, position_list)
                } else {
                    filterList(p0)
                }
                event_adapter.notifyDataSetChanged()
                return true
            }

        })

        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                event_list.clear()
                other_names.clear()
                other_pics.clear()
                position_list.clear()
                event_adapter.setFilteredLists(event_list, other_names, other_pics, position_list)
                event_adapter.notifyDataSetChanged()

                for (eventSnapshot in snapshot.children) {
                    val current_event = eventSnapshot.getValue(Event::class.java)

                    if (current_event?.event_id!!.contains(user_id)) {

                        m_db_ref.child("userSettings").child(user_id).get().addOnSuccessListener {
                            val type = it.getValue(UserSettings::class.java)?.user_type
                            if (type.equals("worker")) { //sono una worker e vedo i miei eventi con le company
                                var position: String? = null
                                var company_name: String? = null
                                var company_logo_url: String? = null
                                var company_logo: Bitmap? = null

                                val event_id = current_event?.event_id
//                                event_id = application_id + timestamp = worker_id + company_id + timestamp + timestamp
                                val offer_id = event_id!!.split("_")[1] + "_" + event_id!!.split("_")[2]
                                m_db_ref.child("offers").child(offer_id).get()
                                    .addOnSuccessListener {
                                        val offer = it.getValue(Offer::class.java)
                                        position = offer?.position

                                        val company_id = offer_id.split("_")[0]
                                        m_db_ref.child("companies").child(company_id).get()
                                            .addOnSuccessListener {
                                                val company = it.getValue(Company::class.java)
                                                company_logo_url = company?.img_profile_url
                                                company_name = company?.company_name

                                                if (company_logo_url != null) {
                                                    var profile_image_ref =
                                                        storage_ref.child(company_logo_url!!)

                                                    var local_file =
                                                        File.createTempFile("tempImage", "jpg")
                                                    profile_image_ref.getFile(local_file)
                                                        .addOnSuccessListener {
                                                            company_logo =
                                                                BitmapFactory.decodeFile(local_file.absolutePath)

                                                            var i = 0
                                                            while (i < event_list.size && event_list.get(i).inverted_date_millis!! > current_event.inverted_date_millis!!) {
                                                                i ++
                                                            }

                                                            event_list.add(i,current_event)
                                                            other_names.add(i,company_name!!)
                                                            other_pics.add(i,company_logo!!)
                                                            position_list.add(i,position!!)
                                                            event_adapter.notifyItemInserted(i)
                                                        }
                                                } else {
                                                    company_logo = BitmapFactory.decodeResource(
                                                        resources,
                                                        R.drawable.user_profile_placeholder
                                                    )
                                                    var i = 0
                                                    while (i < event_list.size && event_list.get(i).inverted_date_millis!! > current_event.inverted_date_millis!!) {
                                                        i ++
                                                    }

                                                    event_list.add(i,current_event)
                                                    other_names.add(i,company_name!!)
                                                    other_pics.add(i,company_logo!!)
                                                    position_list.add(i,position!!)
                                                    event_adapter.notifyItemInserted(i)
                                                }
                                            }
                                    }
                            } else { //sono una company e vedo i miei eventi con i worker
                                var position: String? = null
                                var worker_name: String? = null
                                var worker_pic_url: String? = null
                                var worker_logo: Bitmap? = null

                                val event_id = current_event?.event_id
                                val offer_id = event_id!!.split("_")[1] + "_" + event_id!!.split("_")[2]
                                m_db_ref.child("offers").child(offer_id).get()
                                    .addOnSuccessListener {
                                        val offer = it.getValue(Offer::class.java)
                                        position = offer?.position

                                        val worker_id = event_id.split("_")[0]
                                        m_db_ref.child("workers").child(worker_id).get()
                                            .addOnSuccessListener {
                                                val worker = it.getValue(Worker::class.java)
                                                worker_pic_url = worker?.img_profile_url
                                                worker_name = worker?.name

                                                if (worker_pic_url != null) {
                                                    var profile_image_ref =
                                                        storage_ref.child(worker_pic_url!!)

                                                    var local_file =
                                                        File.createTempFile("tempImage", "jpg")
                                                    profile_image_ref.getFile(local_file)
                                                        .addOnSuccessListener {
                                                            worker_logo =
                                                                BitmapFactory.decodeFile(local_file.absolutePath)

                                                            var i = 0
                                                            while (i < event_list.size && event_list.get(i).inverted_date_millis!! > current_event.inverted_date_millis!!) {
                                                                i ++
                                                            }

                                                            event_list.add(i,current_event)
                                                            other_names.add(i,worker_name!!)
                                                            other_pics.add(i,worker_logo!!)
                                                            position_list.add(i,position!!)
                                                            event_adapter.notifyItemInserted(i)
                                                        }
                                                } else {
                                                    worker_logo = BitmapFactory.decodeResource(
                                                        resources,
                                                        R.drawable.user_profile_placeholder
                                                    )

                                                    var i = 0
                                                    while (i < event_list.size && event_list.get(i).inverted_date_millis!! > current_event.inverted_date_millis!!) {
                                                        i ++
                                                    }

                                                    event_list.add(i,current_event)
                                                    other_names.add(i,worker_name!!)
                                                    other_pics.add(i,worker_logo!!)
                                                    position_list.add(i,position!!)
                                                    event_adapter.notifyItemInserted(i)
                                                }
                                            }
                                    }
                            }
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        m_db_ref.child("events").addValueEventListener(valueEventListener)
    }

    override fun onDestroyView() {
        m_db_ref.child("events").removeEventListener(valueEventListener)
        super.onDestroyView()
    }

    private fun filterList(search_text : String?) {
        var events_filtered_list = ArrayList<Event>()
        var other_names_filtered_list = ArrayList<String>()
        var other_pics_filtered_list = ArrayList<Bitmap>()
        var positions_filtered_list = ArrayList<String>()
        val list_of_words = search_text!!.split(" ")

        for (i in event_list.indices) {
            var to_get = true
            for (word in list_of_words) {
                if (!other_names.get(i).contains(word, true) && !position_list.get(i).contains(word, true)) {
                    to_get = false
                }
            }
            if (to_get) {
                events_filtered_list.add(event_list.get(i))
                other_names_filtered_list.add(other_names.get(i))
                other_pics_filtered_list.add(other_pics.get(i))
                positions_filtered_list.add(position_list.get(i))
            }
        }

        event_adapter.setFilteredLists(events_filtered_list, other_names_filtered_list, other_pics_filtered_list, positions_filtered_list)
    }

    override fun onResume() {
        super.onResume()
        search_view.setQuery("", false)
    }
}
