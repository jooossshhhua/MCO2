package com.mobdeve.s11.mco2

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide

class TransactionDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        val amountTextView: TextView = findViewById(R.id.amountTextView)
        val categoryTextView: TextView = findViewById(R.id.categoryTextView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val imageView: ImageView = findViewById(R.id.transactionImageView)

        val exitButton: Button = findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            finish()
        }

        val intent = intent
        amountTextView.text = intent.getStringExtra("amount")
        categoryTextView.text = intent.getStringExtra("category")
        nameTextView.text = intent.getStringExtra("name")
        dateTextView.text = intent.getStringExtra("date")

        val photoUrl = intent.getStringExtra("photoUrl")
        if (photoUrl != null) {
            // Use your preferred image loading library (e.g., Glide or Picasso) to load the image
            Glide.with(this)
                .load(photoUrl)
                .into(imageView)
        }
    }
}
