package com.example.jober.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.R
import com.example.jober.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context : Context, val message_list : ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.message_received, parent, false)
            return ReceivedViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current_message = message_list[position]
        if(holder.javaClass == SentViewHolder::class.java){
            var viewHolder = holder as SentViewHolder
            holder.sent_message.text = current_message.message
        }else{
            var viewHolder = holder as ReceivedViewHolder
            holder.received_message.text = current_message.message
        }
    }


    override fun getItemCount(): Int {
        return message_list.size
    }

    class SentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val sent_message = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceivedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val received_message = itemView.findViewById<TextView>(R.id.txt_received_message)
    }

    override fun getItemViewType(position: Int): Int {
        val current_message = message_list[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(current_message.sender_id)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVED
        }
    }


}