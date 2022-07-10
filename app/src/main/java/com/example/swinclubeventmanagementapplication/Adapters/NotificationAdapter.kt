package com.example.swinclubeventmanagementapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swinclubeventmanagementapplication.JSONResponse.Event
import com.example.swinclubeventmanagementapplication.JSONResponse.StudentNotification
import com.example.swinclubeventmanagementapplication.R

class NotificationAdapter(
    private val arrayList:ArrayList<StudentNotification>,
    val listener: NotificationAdapter.itemOnClickListener
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

    private var currDate = ""
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textViewNotifReceiveDate: TextView = itemView.findViewById(R.id.textViewNotifReceiveDate)
        val imageViewNotif: ImageView = itemView.findViewById(R.id.imageViewNotif)
        val textViewNotifTitle: TextView = itemView.findViewById(R.id.textViewNotifTitle)
        val textViewNotifContent: TextView = itemView.findViewById(R.id.textViewNotifContent)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if(arrayList[position].type == "event"){
                    listener.OnClickDisplayEvent(arrayList[position].title!!)
                }else{
                    listener.OnClickDisplayClub(arrayList[position].title!!)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        val n = arrayList.get(position)


        holder.textViewNotifReceiveDate.text = n.date
        if(currDate != n.date){
            currDate = n.date!!
            holder.textViewNotifReceiveDate.visibility = View.VISIBLE
        }else{
            holder.textViewNotifReceiveDate.visibility = View.GONE
        }

        if(n.type == "event"){
            holder.imageViewNotif.setImageResource(R.drawable.ic_baseline_event_available_24);
        }else{
            holder.imageViewNotif.setImageResource(R.drawable.ic_baseline_house_siding_24);
        }
        holder.textViewNotifTitle.text = n.title
        holder.textViewNotifContent.text = n.msg
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface itemOnClickListener {
        fun OnClickDisplayEvent(notifTitle:String)
        fun OnClickDisplayClub(notifTitle:String)
    }
}