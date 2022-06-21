package com.example.jober.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.jober.OfferDescription
import com.example.jober.R
import com.example.jober.model.Offer
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(val context: Context, var chat_ids: ArrayList<String>, var other_pics: ArrayList<Bitmap>,
                  var other_names: ArrayList<String>, var positions: ArrayList<String>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_user_name = itemView.findViewById<TextView>(R.id.tv_user_name)
        val tv_position = itemView.findViewById<TextView>(R.id.tv_position)
        val iv_user_profile = itemView.findViewById<ImageView>(R.id.iv_user_profile)
    }

    fun setFilteredLists(chats_filtered_list : ArrayList<String>, other_names_filtered_list : ArrayList<String>,
                         other_pics_filtered_list : ArrayList<Bitmap>, positions_filtered_list : ArrayList<String>) {
        this.chat_ids = chats_filtered_list
        this.other_names = other_names_filtered_list
        this.other_pics = other_pics_filtered_list
        this.positions = positions_filtered_list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.chat_row, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val current_chat_id = chat_ids[position]
        val current_other_pic = other_pics[position]
        val current_other_name = other_names[position]
        val current_position = positions[position]

        holder.tv_user_name.text = current_other_name
        holder.tv_position.text = current_position
        holder.iv_user_profile.setImageBitmap(current_other_pic)

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, OfferDescription()::class.java)
//            intent.putExtra("offer_id", current_offer.id)
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chat_ids.size
    }

}