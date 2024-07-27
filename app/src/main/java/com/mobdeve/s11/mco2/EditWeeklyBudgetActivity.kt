package com.mobdeve.s11.mco2

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import android.view.View
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditWeeklyBudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_weekly_budget)


        val saveButton: Button = findViewById(R.id.saveButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        saveButton.setOnClickListener{
            callingActivity()
        }


        exitButton.setOnClickListener {
            finish()
        }
    }
    private fun callingActivity() {
        val amountbudgetTv: EditText = findViewById(R.id.amountbudgetTv)
        val message = amountbudgetTv.text.toString()
        val budget = message.toDoubleOrNull()
        val user = FirebaseAuth.getInstance().currentUser?.uid

        if(message.isNotEmpty()){
            if(user != null)
            {
                val dbRef = FirebaseDatabase.getInstance().getReference("users").child(user).child("wbudget")
                dbRef.child("wbudget").setValue(budget)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Weekly budget saved successfully", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent().apply {
                            putExtra("EXTRA_MESSAGE", message)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving weekly budget: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            else{
                Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show()
            }

        }
    }

}