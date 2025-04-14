package com.example.whatnow

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatnow.databinding.ActivityFavoritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val db = FirebaseFirestore.getInstance()
    private val favorites = arrayListOf<Article>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = FavoriteAdapter(this, favorites)
        binding.favoriteList.adapter = adapter

         auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {

            Log.e("FavoritesActivity", "User not logged in")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FirestoreDebug", "No favorites found.")
                } else {
                    for (document in documents) {
                        val title = document.getString("title") ?: ""
                        val url = document.getString("url") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""

                        favorites.add(Article(title, url, imageUrl))
                    }
                    adapter.notifyDataSetChanged()
                    Log.d("FirestoreDebug", "Favorites size: ${favorites.size}")
                }
            }
            .addOnFailureListener {
                Log.e("FirestoreDebug", "Failed to load favorites: ${it.message}")
            }
    }

}