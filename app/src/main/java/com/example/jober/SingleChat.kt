package com.example.jober

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.adapters.MessageAdapter
import com.example.jober.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SingleChat : AppCompatActivity() {

    private lateinit var chat_recycler_view : RecyclerView
    private lateinit var message_box : EditText
    private lateinit var send_button : ImageView
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

        message_list = ArrayList()
        message_adapter = MessageAdapter(this, message_list)

        m_auth = FirebaseAuth.getInstance()
        storage_ref = FirebaseStorage.getInstance().getReference()
        database = Firebase.database("https://jober-290f2-default-rtdb.europe-west1.firebasedatabase.app")
        m_db_ref = database.getReference()

        val user_id =  m_auth.currentUser?.uid!!

        chat_recycler_view.layoutManager = LinearLayoutManager(this)
        chat_recycler_view.adapter = message_adapter

        val chat_id = intent.getStringExtra("chat_id").toString()

        m_db_ref.child("chats").child(chat_id).child("messages").addValueEventListener(object : ValueEventListener{
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
                TODO("Not yet implemented")
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
                m_db_ref.child("chats").child(chat_id).child("messages").push().setValue(message_object)
                message_box.setText("")
            }
        }
    }
}