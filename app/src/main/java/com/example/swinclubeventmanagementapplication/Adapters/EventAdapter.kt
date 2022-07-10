package com.example.swinclubeventmanagementapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.JSONResponse.Event
import com.example.swinclubeventmanagementapplication.JSONResponse.SimplifiedClub
import com.example.swinclubeventmanagementapplication.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(
    private val arrayList:ArrayList<Event>,
    val listener: itemOnClickListener,
    val upcoming: Boolean
): RecyclerView.Adapter<EventAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textViewEventTitle: TextView = itemView.findViewById(R.id.textViewEventTitle)
        val imageViewEventPoster: ImageView = itemView.findViewById(R.id.imageViewEventPoster)
        val textViewEventStartDate: TextView = itemView.findViewById(R.id.textViewEventStartDate)
        val textViewEventParticipationFee: TextView = itemView.findViewById(R.id.textViewEventParticipationFee)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                listener.OnClickDisplayEvent(arrayList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_event_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {
        val e = arrayList.get(position)

        var t = SimpleDateFormat("HH:mm:ss").format(SimpleDateFormat("hh:mm a").parse(e.EventTime))
        var eStartDateTime = "${e.EventDate} ${t}"

        // if show upcoming -> past = false
        // if past -> past true
        if(upcoming){
            if(isEventPast(eStartDateTime) == false){
                Glide.with(holder.itemView)
                    .load(e.EventPosterIMG)
                    .into(holder.imageViewEventPoster)
                holder.textViewEventTitle.setText(e.EventTitle)
                holder.textViewEventStartDate.text = "${e.EventDate} ${e.EventTime}"
                holder.textViewEventParticipationFee.setText(e.EventParticipationFee)
            }else{
                holder.imageViewEventPoster.setVisibility(View.GONE)
                holder.textViewEventTitle.setVisibility(View.GONE)
                holder.textViewEventStartDate.setVisibility(View.GONE)
                holder.textViewEventParticipationFee.setVisibility(View.GONE)
            }
        }else{
            if(isEventPast(eStartDateTime) == true){
                Glide.with(holder.itemView)
                    .load(e.EventPosterIMG)
                    .into(holder.imageViewEventPoster)
                holder.textViewEventTitle.setText(e.EventTitle)
                holder.textViewEventStartDate.text = "${e.EventDate} ${e.EventTime}"
                holder.textViewEventParticipationFee.setText(e.EventParticipationFee)
            }else{
                holder.imageViewEventPoster.setVisibility(View.GONE)
                holder.textViewEventTitle.setVisibility(View.GONE)
                holder.textViewEventStartDate.setVisibility(View.GONE)
                holder.textViewEventParticipationFee.setVisibility(View.GONE)
            }
        }



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface itemOnClickListener {
        fun OnClickDisplayEvent(e: Event)
    }

    private fun isEventPast(eStartDateTime: String):Boolean{

        try {
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val d2 = sdf.format(Date())
            val d1 = eStartDateTime

            val date1: Date = sdf.parse(d1)
            val date2: Date = sdf.parse(d2)
//            System.out.println("Date1 " + sdf.format(date1))
//            System.out.println("Date2 " + sdf.format(date2))

            if (date1.after(date2)) {
//                println("Date1 is after Date2")
                return false
            }
            // before() will return true if and only if date1 is before date2
            if (date1.before(date2)) {
//                println("Date1 is before Date2")
                return true
            }

            //equals() returns true if both the dates are equal
            if (date1.equals(date2)) {
//                println("Date1 is equal Date2")
                return false
            }
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return false
    }

}
