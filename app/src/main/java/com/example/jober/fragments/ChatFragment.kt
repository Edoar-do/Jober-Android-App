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
import com.example.jober.adapters.ChatAdapter
import com.example.jober.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ChatFragment : Fragment() {

    lateinit var chats_recycler_view : RecyclerView
    lateinit var search_view : SearchView

    lateinit var chat_list : ArrayList<String>
    lateinit var other_pics: ArrayList<Bitmap>
    lateinit var other_names : ArrayList<String>
    lateinit var position_list : ArrayList<String>

    lateinit var chat_adapter : ChatAdapter

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase

    lateinit var valueEventListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chat_list = ArrayList()
        other_pics = ArrayList()
        other_names = ArrayList()
        position_list = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        (activity as MainActivity).supportActionBar!!.title = "Jober - Chats"

        chat_adapter = ChatAdapter(view.context, chat_list, other_pics, other_names, position_list)

        chats_recycler_view = view.findViewById(R.id.recyclerview)
        chats_recycler_view.layoutManager = LinearLayoutManager(view.context)
        chats_recycler_view.adapter = chat_adapter

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
                    chat_adapter.setFilteredLists(chat_list, other_names, other_pics, position_list)
                } else {
                    filterList(p0)
                }
                chat_adapter.notifyDataSetChanged()
                return true
            }

        })

        valueEventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chat_list.clear()
                other_names.clear()
                other_pics.clear()
                position_list.clear()
                chat_adapter.setFilteredLists(chat_list, other_names, other_pics, position_list)
                chat_adapter.notifyDataSetChanged()

                for (chatSnapshot in snapshot.children) {
                    val current_chat = chatSnapshot.getValue(Chat::class.java)

                    if (current_chat?.chat_id!!.contains(user_id)) {

                        m_db_ref.child("userSettings").child(user_id).get().addOnSuccessListener {
                            val type = it.getValue(UserSettings::class.java)?.user_type
                            if (type.equals("worker")) { //sono una worker e vedo le mie chat con le company
                                var position: String? = null
                                var company_name: String? = null
                                var company_logo_url: String? = null
                                var company_logo: Bitmap? = null

                                val chat_id = current_chat?.chat_id
                                val offer_id = chat_id!!.split("_", ignoreCase = false, limit = 2)[1]
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
                                                            chat_list.add(chat_id)
                                                            other_names.add(company_name!!)
                                                            other_pics.add(company_logo!!)
                                                            position_list.add(position!!)
                                                            chat_adapter.notifyItemInserted(
                                                                chat_list.size - 1
                                                            )
                                                        }
                                                } else {
                                                    company_logo = BitmapFactory.decodeResource(
                                                        resources,
                                                        R.drawable.user_profile_placeholder
                                                    )
                                                    chat_list.add(chat_id)
                                                    other_names.add(company_name!!)
                                                    other_pics.add(company_logo!!)
                                                    position_list.add(position!!)
                                                    chat_adapter.notifyItemInserted(chat_list.size - 1)
                                                }
                                            }
                                    }
                            } else { //sono una company e vedo le mie chat coi worker
                                var position: String? = null
                                var worker_name: String? = null
                                var worker_pic_url: String? = null
                                var worker_logo: Bitmap? = null

                                val chat_id = current_chat?.chat_id
                                val offer_id = chat_id!!.split("_", ignoreCase = false, limit = 2)[1]
                                m_db_ref.child("offers").child(offer_id).get()
                                    .addOnSuccessListener {
                                        val offer = it.getValue(Offer::class.java)
                                        position = offer?.position

                                        val worker_id = chat_id.split("_")[0]
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
                                                            chat_list.add(chat_id)
                                                            other_names.add(worker_name!!)
                                                            other_pics.add(worker_logo!!)
                                                            position_list.add(position!!)
                                                            chat_adapter.notifyItemInserted(
                                                                chat_list.size - 1
                                                            )
                                                        }
                                                } else {
                                                    worker_logo = BitmapFactory.decodeResource(
                                                        resources,
                                                        R.drawable.user_profile_placeholder
                                                    )
                                                    chat_list.add(chat_id)
                                                    other_names.add(worker_name!!)
                                                    other_pics.add(worker_logo!!)
                                                    position_list.add(position!!)
                                                    chat_adapter.notifyItemInserted(chat_list.size - 1)
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

        m_db_ref.child("chats").addValueEventListener(valueEventListener)
    }

    override fun onDestroyView() {
        m_db_ref.child("chats").removeEventListener(valueEventListener)
        super.onDestroyView()
    }

    private fun filterList(search_text : String?) {
        var chats_filtered_list = ArrayList<String>()
        var other_names_filtered_list = ArrayList<String>()
        var other_pics_filtered_list = ArrayList<Bitmap>()
        var positions_filtered_list = ArrayList<String>()
        val list_of_words = search_text!!.split(" ")

        for (i in chat_list.indices) {
            var to_get = true
            for (word in list_of_words) {
                if (!other_names.get(i).contains(word, true) && !position_list.get(i).contains(word, true)) {
                    to_get = false
                }
            }
            if (to_get) {
                chats_filtered_list.add(chat_list.get(i))
                other_names_filtered_list.add(other_names.get(i))
                other_pics_filtered_list.add(other_pics.get(i))
                positions_filtered_list.add(position_list.get(i))
            }
        }

        chat_adapter.setFilteredLists(chats_filtered_list, other_names_filtered_list, other_pics_filtered_list, positions_filtered_list)
    }

    override fun onResume() {
        super.onResume()
        search_view.setQuery("", false)
    }
}
