package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.FavoriteItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteAdapter(val a: Activity, val favorites: ArrayList<Article>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoritesViewHolder>() {
    class FavoritesViewHolder(val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.FavoritesViewHolder {
        val binding =
            FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoritesViewHolder, position: Int) {
        holder.binding.articleText.text = favorites[position].title
        holder.binding.saveFab.setImageResource(R.drawable.star_favorite)

        Glide
            .with(holder.binding.articleImage.context)
            .load(favorites[position].urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        val url = favorites[position].url

        holder.binding.articleContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(intent)
        }

        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with: ")
                .setText(url)
                .startChooser()
        }

        holder.binding.saveFab.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                .document(userId)
                .collection("favorites")
                .whereEqualTo("url", url)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        // 🔥 Remove from Firestore
                        for (doc in snapshot.documents) {
                            db.collection("users")
                                .document(userId)
                                .collection("favorites")
                                .document(doc.id)
                                .delete()
                                .addOnSuccessListener {
                                    holder.binding.saveFab.setImageResource(R.drawable.star)
                                    favorites.removeAt(position)
                                    notifyItemRemoved(position)
                                    Toast.makeText(holder.itemView.context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(holder.itemView.context, "Failed to remove", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
        }


    }

    override fun getItemCount() = favorites.size

}