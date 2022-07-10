package com.example.swinclubeventmanagementapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.JSONResponse.Club
import com.example.swinclubeventmanagementapplication.JSONResponse.Event
import com.example.swinclubeventmanagementapplication.JSONResponse.SimplifiedClub
import com.example.swinclubeventmanagementapplication.R

class JoinedClubAdapter(
    private val arrayList:ArrayList<Club>,
    val listener: JoinedClubAdapter.itemOnClickListener
): RecyclerView.Adapter<JoinedClubAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textViewClubName: TextView = itemView.findViewById(R.id.textViewClubName)
        val imageViewClubLogo: ImageView = itemView.findViewById(R.id.imageViewClubLogo)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                listener.OnClickDisplayClub(arrayList[position])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JoinedClubAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.staggered_club_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: JoinedClubAdapter.ViewHolder, position: Int) {
        val c = arrayList.get(position)
        Glide.with(holder.itemView)
            .load(c.ClubLogoIMG)
            .into(holder.imageViewClubLogo)
        holder.textViewClubName.setText(c.ClubName)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    interface itemOnClickListener {
        fun OnClickDisplayClub(c: Club)
    }
}