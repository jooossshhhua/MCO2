package com.mobdeve.s11.mco2

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditWeeklyBudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.edit_weekly_budget)

        val amountbudgetTv: EditText = findViewById(R.id.amountbudgetTv)
        val saveButton: Button = findViewById(R.id.saveButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        saveButton.setOnClickListener {
            val newBudget = amountbudgetTv.text.toString()
            val resultIntent = Intent()
            resultIntent.putExtra("newBudget", newBudget)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}