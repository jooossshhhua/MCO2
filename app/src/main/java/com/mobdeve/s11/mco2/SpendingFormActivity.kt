package com.mobdeve.s11.mco2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SpendingFormActivity : ComponentActivity() {
    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spending_form)

        setupSpinner()
        setupCloseButton()
        setupPhotoButton()
        setupButton2ClickListener() // Moved inside onCreate
    }
    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.catSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupCloseButton() {
        val closeButton: Button = findViewById(R.id.button3)
        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun setupPhotoButton() {
        val photoButton: Button = findViewById(R.id.photoB)
        photoButton.setOnClickListener {
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView: ImageView = findViewById(R.id.imageView3)
            imageView.setImageBitmap(imageBitmap)
        }
    }

    private fun collectData(): Map<String, Any> {
        val name = findViewById<EditText>(R.id.editTextText).text.toString()
        val amount = findViewById<EditText>(R.id.amountbudgetTv).text.toString()
        val category = findViewById<Spinner>(R.id.catSpinner).selectedItem.toString()


        return mapOf(
            "name" to name,
            "amount" to amount,
            "category" to category,
        )
    }

    // Updated to use Firebase Realtime Database
    private fun addDataToFirebase(data: Map<String, Any>) {
        val dbRef = FirebaseDatabase.getInstance().getReference("transactions")
        dbRef.push().setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding transaction: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
    private fun setupButton2ClickListener() {
        val button2: Button = findViewById(R.id.button2)
        button2.setOnClickListener {
            val data = collectData()
            addDataToFirebase(data)
            finish()
        }
    }
}