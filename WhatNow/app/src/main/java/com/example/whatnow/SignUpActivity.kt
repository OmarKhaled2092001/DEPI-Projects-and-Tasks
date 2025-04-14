package com.example.whatnow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.whatnow.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        binding.loginTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.signUpBtn.setOnClickListener {
            if (binding.emailEt.text.toString().isBlank() || binding.passwordEt.text.toString()
                    .isBlank() || binding.confirmPasswordEt.text.toString().isBlank()
            ) {
                Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show();
            } else if (binding.passwordEt.text.toString() != binding.confirmPasswordEt.text.toString()) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.isVisible = true
                // Sign Up Code
                addNewUser(binding.emailEt.text.toString(), binding.passwordEt.text.toString())
            }

        }
    }

    private fun addNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verfiyEmail()
                } else if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                    binding.progressBar.isVisible = false
                } else if (task.exception is FirebaseAuthWeakPasswordException) {
                    Toast.makeText(
                        this,
                        "Weak Password , try entering a strong password",
                        Toast.LENGTH_SHORT
                    ).show();
                    binding.progressBar.isVisible = false
                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    binding.progressBar.isVisible = false
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                }
            }
    }

    private fun verfiyEmail() {
        val user = Firebase.auth.currentUser

        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email ", Toast.LENGTH_SHORT).show();
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
    }
}