package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.ArticleListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewsAdapter(val a: Activity, val articles: ArrayList<Article>) :
    Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ArticleListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        val url = article.url
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore

        holder.binding.articleText.text = article.title

        Glide
            .with(holder.binding.articleImage.context)
            .load(article.urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        holder.binding.articleContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(intent)
        }

        holder.binding.shareFab.setOnClickListener {
            ShareCompat.IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with: ")
                .setText(url)
                .startChooser()
        }

        // ⭐ Check if already in favorites
        db.collection("users")
            .document(userId)
            .collection("favorites")
            .whereEqualTo("url", url)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    holder.binding.saveFab.setImageResource(R.drawable.star_favorite)
                } else {
                    holder.binding.saveFab.setImageResource(R.drawable.star)
                }
            }

        holder.binding.saveFab.setOnClickListener {
            db.collection("users")
                .document(userId)
                .collection("favorites")
                .whereEqualTo("url", url)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        Toast.makeText(holder.itemView.context, "Already in favorites", Toast.LENGTH_SHORT).show()
                    } else {

                        val favorite = hashMapOf(
                            "title" to article.title,
                            "url" to article.url,
                            "imageUrl" to article.urlToImage
                        )

                        db.collection("users")
                            .document(userId)
                            .collection("favorites")
                            .add(favorite)
                            .addOnSuccessListener {
                                holder.binding.saveFab.setImageResource(R.drawable.star_favorite)
                                Toast.makeText(holder.itemView.context, "Added to favorites", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Failed to add", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }


    }

}