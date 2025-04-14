package com.example.whatnow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.forgotPasswordTv.setOnClickListener {
            binding.progressBar.isVisible = true
            val email = binding.emailEt.text.toString()
            if (email.isBlank()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            } else {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show();
                            binding.progressBar.isVisible = false
                        }
                    }
            }
        }

        binding.loginBtn.setOnClickListener {
            if (binding.emailEt.text.toString().isBlank() || binding.passwordEt.text.toString().isBlank()) {
                Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.isVisible = true
                // Login Code
                login(binding.emailEt.text.toString(), binding.passwordEt.text.toString())
            }
        }
    }


    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser!!.isEmailVerified) {
                        val editor = sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Snackbar.make(binding.root, "Email not verified!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Resend") {
                                verfiyEmail()
                            }
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.orange))
                            .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()
                        binding.progressBar.isVisible = false
                    }
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    binding.progressBar.isVisible = false
                }
            }
    }

    private fun verfiyEmail() {
        val user = Firebase.auth.currentUser

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent! ", Toast.LENGTH_SHORT).show();
                }
            }
    }


}