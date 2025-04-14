package com.example.emergencyapp

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class EmergencyAdapter(val activity: Activity, val emergencies: ArrayList<Emergency>) :
    RecyclerView.Adapter<EmergencyAdapter.MVH>() {
    class MVH(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.parent)
        val text: TextView = view.findViewById(R.id.tv)
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyAdapter.MVH {
        val view = activity.layoutInflater.inflate(R.layout.emergency_list_item, parent, false)
        return MVH(view)
    }

    override fun onBindViewHolder(holder: EmergencyAdapter.MVH, position: Int) {
        holder.text.text = emergencies[position].name
        holder.image.setImageResource(emergencies[position].pic)
        holder.cardView.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL, "tel:${emergencies[position].number}".toUri())
            activity.startActivity(i)
        }
    }

    override fun getItemCount() = emergencies.size

}