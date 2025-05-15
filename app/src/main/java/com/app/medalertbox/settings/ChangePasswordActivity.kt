package com.app.medalertbox.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.AdminDashboardActivity
import com.app.medalertbox.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Handle password reset
        binding.resetPasswordBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your registered email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.HomeBtn.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
