package com.mobdeve.s11.mco2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log
class SigninActivity : ComponentActivity() {

    private lateinit var signuserTv: EditText
    private lateinit var signpassTv: EditText
    private lateinit var logButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var signupnow: TextView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signin)

        auth = FirebaseAuth.getInstance()
        signuserTv = findViewById(R.id.signuserTv)
        signpassTv = findViewById(R.id.signpassTv)
        logButton = findViewById(R.id.logButton)
        signupnow = findViewById(R.id.signupnow)

        signupnow.setOnClickListener(){
            val i = Intent(this, SignupActivity::class.java)
            startActivity(i)
            finish()
        }

        logButton.setOnClickListener(){
            val email = signuserTv.text.toString()
            val password = signpassTv.text.toString()

            if(email.isBlank() || password.isBlank())
                Toast.makeText(this, "Text field is empty", Toast.LENGTH_SHORT).show()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Log-in Successful!",
                            Toast.LENGTH_SHORT,
                        ).show()

                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Log-in Failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }

    }
}