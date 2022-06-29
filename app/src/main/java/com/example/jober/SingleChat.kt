package com.example.jober

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.adapters.MessageAdapter
import com.example.jober.model.Event
import com.example.jober.model.Message
import com.example.jober.model.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class SingleChat : AppCompatActivity() {

    private lateinit var chat_recycler_view : RecyclerView
    private lateinit var message_box : EditText
    private lateinit var send_button : ImageView
    private lateinit var btn_add_event : ImageView
    private lateinit var message_adapter : MessageAdapter
    private lateinit var message_list : ArrayList<Message>

    lateinit var m_db_ref: DatabaseReference
    lateinit var m_auth: FirebaseAuth
    lateinit var storage_ref : StorageReference
    lateinit var database : FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_recycler_view = findViewById(R.id.chatRecyclerView)
        message_box = findViewById(R.id.messageBox)
        send_button = findViewById(R.id.sendButton)
        btn_add_event = findViewById(R.id.btn_add_event)

        message_list = ArrayList()
        message_adapter = MessageAdapter(this, message_list)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        val user_id =  m_auth.currentUser?.uid!!

        m_db_ref.child("userSettings").child(user_id).get().addOnSuccessListener {
            val user_settings = it.getValue(UserSettings::class.java)
            val user_type = user_settings?.user_type!!

            if (user_type.equals("worker")) {
                btn_add_event.visibility = View.GONE
            }
        }

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        chat_recycler_view.adapter = message_adapter

        val chat_id = intent.getStringExtra("chat_id").toString()

        m_db_ref.child("messages").child(chat_id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                message_list.clear()

                for (messageSnapshot in snapshot.children){
                    val message = messageSnapshot.getValue(Message::class.java)
                    message_list.add(message!!)
                }

                message_adapter.notifyDataSetChanged()

                chat_recycler_view.scrollToPosition(message_list.size-1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SingleChat, "Sorry, there was a problem connecting to the database...", Toast.LENGTH_LONG).show()
            }

        })

        var user1 = chat_id.split("_")[0] //worker_id
        var user2 = chat_id.split("_")[1] //company_id
        var is_receiver_worker: Boolean
        val receiver_id : String

        if (user1.equals(user_id)){
            receiver_id = user2
            is_receiver_worker = false
        } else {
            receiver_id = user1
            is_receiver_worker = true
        }

        if(is_receiver_worker){
            m_db_ref.child("workers").child(receiver_id).child("name").get().addOnSuccessListener {
                supportActionBar!!.title = "Chat with ${it.value.toString()}"
            }
        }else{
            m_db_ref.child("companies").child(receiver_id).child("company_name").get().addOnSuccessListener {
                supportActionBar!!.title = "Chat with ${it.value.toString()}"
            }
        }

        m_db_ref.child("offers").child(chat_id.split("_", ignoreCase = false, limit=2)[1]).child("position").get().addOnSuccessListener {
            if(it.exists()){
                supportActionBar!!.subtitle = "for ${it.value.toString()} position"
            }
        }

        send_button.setOnClickListener{
            val message = message_box.text.toString()
            if (message.trim().isNotEmpty()) {
                val message_object = Message(System.currentTimeMillis().toString(), chat_id, user_id, receiver_id, message.trim())
                m_db_ref.child("messages").child(chat_id).push().setValue(message_object).addOnSuccessListener {
                    m_db_ref.child("chats").child(chat_id).child("last_update").setValue(System.currentTimeMillis())
                }
                message_box.setText("")
            }
        }

        btn_add_event.setOnClickListener {
            clickDatePicker(chat_id)
        }

    }

    fun clickDatePicker(chat_id : String) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = myCalendar.get(Calendar.HOUR)
        val minutes = myCalendar.get(Calendar.MINUTE)

        DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener {view, selectedyear, selectedmonth, selecteddayofmonth ->
                val selDate = "$selecteddayofmonth/${selectedmonth+1}/$selectedyear"

                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, selected_hour, selected_minutes ->
                    // creare l'evento
                    val event_id = chat_id + "_" + System.currentTimeMillis()
                    val worker_id = chat_id.split("_")[0]
                    val company_id = chat_id.split("_")[1]
                    val date = Calendar.getInstance()
                    date.set(selectedyear, selectedmonth, selecteddayofmonth, selected_hour, selected_minutes)
                    val event = Event(event_id, chat_id, worker_id, company_id, date.timeInMillis)
                    m_db_ref.child("events").child(event_id).setValue(event).addOnSuccessListener {
                        Toast.makeText(this, "The event has been successfully created", Toast.LENGTH_LONG).show()
                    }
                }, hour, minutes,false).show()
            },
            year,
            month,
            day
        ).show()
    }
}