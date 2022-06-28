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
import com.example.jober.EventDetails
import com.example.jober.OfferDescription
import com.example.jober.R
import com.example.jober.SingleChat
import com.example.jober.model.Event
import com.example.jober.model.Offer
import com.google.android.material.timepicker.TimeFormat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(val context: Context, var events: ArrayList<Event>, var other_pics: ArrayList<Bitmap>,
                   var other_names: ArrayList<String>, var positions: ArrayList<String>): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_position = itemView.findViewById<TextView>(R.id.tv_position)
        val tv_date_time = itemView.findViewById<TextView>(R.id.tv_date_time)
        val iv_user_profile = itemView.findViewById<ImageView>(R.id.iv_pic)
    }

    fun setFilteredLists(events_filtered_list : ArrayList<Event>, other_names_filtered_list : ArrayList<String>,
                         other_pics_filtered_list : ArrayList<Bitmap>, positions_filtered_list : ArrayList<String>) {
        this.events = events_filtered_list
        this.other_names = other_names_filtered_list
        this.other_pics = other_pics_filtered_list
        this.positions = positions_filtered_list
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current_event = events[position]
        val current_other_pic = other_pics[position]
        val current_other_name = other_names[position]
        val current_position = positions[position]

        holder.tv_name.text = current_other_name
        holder.tv_position.text = current_position

        var df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        holder.tv_date_time.text = df.format(current_event.date_millis!!)

        holder.iv_user_profile.setImageBitmap(current_other_pic)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, EventDetails::class.java)
            intent.putExtra("event", current_event)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

}