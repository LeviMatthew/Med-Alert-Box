package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEt)
        val passwordEditText = findViewById<EditText>(R.id.passET)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val signUpButton = findViewById<Button>(R.id.button)
        val alreadySignedInTextView = findViewById<TextView>(R.id.textView) // 🔥 Added TextView

        // Role selection dropdown
        val roles = arrayOf("Admin", "Senior", "Caregiver")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val selectedRole = roleSpinner.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid
                            val userMap = hashMapOf(
                                "email" to email,
                                "role" to selectedRole
                            )

                            // Store user role in Firestore
                            db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sign-up successful! Please log in.", Toast.LENGTH_SHORT).show()

                                    // Sign out the user to force login
                                    auth.signOut()

                                    // Redirect to LogInActivity
                                    val intent = Intent(this, LogInActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Sign-up failed! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Fill in all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔥 Redirect to LogInActivity when "Already signed in?" is clicked
        alreadySignedInTextView.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
