package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.whatnow.databinding.CategoryItemBinding

class CategoryAdapter(val activity: Activity, val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.binding.categoryName.text = categories[position].name
        holder.binding.categoryContainer.setOnClickListener {
            activity.startActivity(
                Intent(activity, MainActivity::class.java).putExtra(
                    "category",
                    categories[position].name.lowercase()
                )
            )
        }
    }

    override fun getItemCount() = categories.size

}