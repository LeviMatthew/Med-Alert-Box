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

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEt)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEt)
        val usernameEditText = findViewById<EditText>(R.id.usernameEt)
        val emailEditText = findViewById<EditText>(R.id.emailEt)
        val passwordEditText = findViewById<EditText>(R.id.passET)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val signUpButton = findViewById<Button>(R.id.button)
        val alreadySignedInTextView = findViewById<TextView>(R.id.textView)

        // Role selection dropdown
        val roles = arrayOf("Admin", "Senior", "Caregiver")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val selectedRole = roleSpinner.selectedItem.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(
                    this,
                    "Password must be:\n• Between 8 to 25 characters\n• Include at least one uppercase letter\n• Include at least one lowercase letter\n• Include at least one number",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val userMap = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username,
                            "email" to email,
                            "role" to selectedRole
                        )

                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sign-up successful! Please log in.", Toast.LENGTH_SHORT).show()
                                auth.signOut()
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
        }

        alreadySignedInTextView.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,25}$"
        return password.matches(regex.toRegex())
    }
}
