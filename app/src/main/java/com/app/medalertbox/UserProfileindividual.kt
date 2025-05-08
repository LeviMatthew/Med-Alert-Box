package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityUserProfileindividualBinding

class UserProfileindividual : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileindividualBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileindividualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Home button click event
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, SeniorDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
