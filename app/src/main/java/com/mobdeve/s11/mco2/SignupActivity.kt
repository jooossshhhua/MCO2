package com.mobdeve.s11.mco2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User

class SignupActivity: ComponentActivity() {

    private lateinit var signupuserTv: EditText
    private lateinit var signupemailTv: EditText
    private lateinit var signuppassTv: EditText
    private lateinit var signupconfirmTv: EditText
    private lateinit var signButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var signinnow: TextView

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup)

        auth = FirebaseAuth.getInstance()
        signupuserTv = findViewById(R.id.signupuserTv)
        signupemailTv = findViewById(R.id.signupemailTv)
        signuppassTv = findViewById(R.id.signuppassTv)
        signupconfirmTv = findViewById(R.id.signupconfirmTv)
        signButton = findViewById(R.id.signButton)

        signinnow = findViewById(R.id.signinnow)
        signinnow.setOnClickListener(){
            val i = Intent(this, SigninActivity::class.java)
            startActivity(i)
        }

        signButton.setOnClickListener(){
            val username = signupuserTv.text.toString()
            val email = signupemailTv.text.toString()
            val password = signuppassTv.text.toString()
            val confirmpassword = signupconfirmTv.text.toString()

            if(username.isBlank() || email.isBlank() || password.isBlank() || confirmpassword.isBlank())
                Toast.makeText(this, "Text field is empty", Toast.LENGTH_SHORT).show()

            if(password != confirmpassword)
                Toast.makeText(this, "Make sure the password matches", Toast.LENGTH_SHORT).show()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        val userId = user?.uid

                        if (userId != null) {
                            val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                            val userMap = mapOf(
                                "username" to username,
                                "email" to email
                            )

                            databaseReference.setValue(userMap).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(
                                        baseContext,
                                        "Account created and username saved.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    // Redirect to another activity or update UI
                                } else {
                                    Toast.makeText(
                                        baseContext,
                                        "Failed to save username.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "User ID is null.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Account creation failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

    }


}