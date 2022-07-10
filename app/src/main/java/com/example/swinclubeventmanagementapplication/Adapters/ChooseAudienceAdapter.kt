package com.example.swinclubeventmanagementapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swinclubeventmanagementapplication.ChooseAudienceActivity
import com.example.swinclubeventmanagementapplication.JSONResponse.SimplifiedClub
import com.example.swinclubeventmanagementapplication.R

class ChooseAudienceAdapter(
    private val arrayListClub:ArrayList<SimplifiedClub>,
    val listener: ChooseAudienceActivity
): RecyclerView.Adapter<ChooseAudienceAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textViewClubName: TextView = itemView.findViewById(R.id.textViewClubName)
        val imageViewClubLogo: ImageView = itemView.findViewById(R.id.imageViewClubLogo)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                listener.OnClick(arrayListClub[position].clubName!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAudienceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_club_audience_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseAudienceAdapter.ViewHolder, position: Int) {
        val sClub = arrayListClub.get(position)
        Glide.with(holder.itemView)
            .load(sClub.clubLogoUrl)
            .into(holder.imageViewClubLogo)
        holder.textViewClubName.setText(sClub.clubName)
    }

    override fun getItemCount(): Int {
        return arrayListClub.size
    }

    interface onClickListener{
        fun OnClick(audience: String)
    }
}