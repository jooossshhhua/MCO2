package com.mobdeve.s11.mco2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Locale
import com.mobdeve.s11.mco2.ui.theme.MCO2_S11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val amountbudgetTv = findViewById<TextView>(R.id.amountbudgetTv)
        val wbudgetTv = findViewById<TextView>(R.id.wbudgetTv)
        val expTv = findViewById<TextView>(R.id.expTv)
        val currentDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        dateTextView.text = currentDate

        val spendingButton = findViewById<Button>(R.id.spendingFormBtn)
        spendingButton.setOnClickListener{
            val intent = Intent(this, SpendingFormActivity::class.java)
            startActivity(intent)
        }
        try {
            val wbudget = wbudgetTv.text.toString().toDoubleOrNull() ?: 0.0
            val exp = expTv.text.toString().toDoubleOrNull() ?: 0.0
            val amountBudget = wbudget - exp
            amountbudgetTv.text = String.format("%.2f", amountBudget)
        } catch (e: NumberFormatException) {
            // Handle the case where the text could not be converted to a number
            amountbudgetTv.text = "Error"
        }
    }

}
