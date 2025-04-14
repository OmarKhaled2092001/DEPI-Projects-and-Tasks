package com.example.whatnow

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatnow.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)

        val savedCountry = sharedPreferences.getString("country", "us")

        when (savedCountry) {
            "us" -> binding.countryRadioGroup.check(binding.radioUsa.id)
            "de" -> binding.countryRadioGroup.check(binding.radioGermany.id)
            "eg" -> binding.countryRadioGroup.check(binding.radioEgypt.id)
        }


        binding.saveBtn.setOnClickListener {
            val selectedCountry = when (binding.countryRadioGroup.checkedRadioButtonId) {
                binding.radioUsa.id -> "us"
                binding.radioGermany.id -> "de"
                binding.radioEgypt.id -> "eg"
                else -> "us"
            }

            val editor = sharedPreferences.edit().putString("country", selectedCountry).apply()

            Toast.makeText(this, "Country Saved Successfully", Toast.LENGTH_SHORT).show();

            finish()
        }

    }
}