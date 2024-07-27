package com.mobdeve.s11.mco2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var logout: TextView
    private lateinit var email: TextView

    private lateinit var dbref: DatabaseReference
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionArrayList: ArrayList<Transaction>

    private lateinit var wbudgetTv: TextView
    private lateinit var amountbudgetTv: TextView
    private lateinit var expTv: TextView
    private lateinit var alertTv: TextView
    private lateinit var linearLayout2: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //USER AUTH
        auth = FirebaseAuth.getInstance()
        logout = findViewById(R.id.logout)
        email = findViewById(R.id.email)
        val user = auth.currentUser

        if(user == null){
            val i = Intent(this, SigninActivity::class.java)
            startActivity(i)
            finish()
        }
        else{
            email.setText(user.email.toString())
        }

        logout.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, SigninActivity::class.java)
            startActivity(i)
            finish()
        }



        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        amountbudgetTv = findViewById<TextView>(R.id.amountbudgetTv)
        wbudgetTv = findViewById<TextView>(R.id.wbudgetTv)
        expTv = findViewById<TextView>(R.id.expTv)
        alertTv = findViewById<TextView>(R.id.alertTv)
        linearLayout2 = findViewById<ConstraintLayout>(R.id.linearLayout2)

        val currentDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        dateTextView.text = currentDate

        transactionRecyclerView = findViewById(R.id.transactionList)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionRecyclerView.setHasFixedSize(true)

        val weeklyB = findViewById<Button>(R.id.weeklyB)
        val todayB = findViewById<Button>(R.id.todayB)
        weeklyB.setOnClickListener{
            weeklyB.background = ContextCompat.getDrawable(this, R.drawable.home_button)
            todayB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
        }
        todayB.setOnClickListener{
            todayB.background = ContextCompat.getDrawable(this, R.drawable.home_button)
            weeklyB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
        }

        transactionArrayList = arrayListOf()
        getTransactionsData()


        val spendingButton = findViewById<Button>(R.id.spendingFormBtn)
        spendingButton.setOnClickListener {
            val i = Intent(this, SpendingFormActivity::class.java)
            startActivity(i)
        }

        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            val i = Intent(this, EditWeeklyBudgetActivity::class.java)
            startActivityForResult(i, EDIT_REQUEST_CODE)
        }
        updateBudgetDisplay()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val message = data?.getStringExtra("EXTRA_MESSAGE")
            wbudgetTv.text = message
            updateBudgetDisplay()
        }
    }

    private fun updateBudgetDisplay() {
        try {
            val wbudget = wbudgetTv.text.toString().toDoubleOrNull() ?: 0.0
            val exp = expTv.text.toString().toDoubleOrNull() ?: 0.0
            val amountBudget = wbudget - exp
            amountbudgetTv.text = String.format("%.2f", amountBudget)
            if (amountBudget < 0) {
                alertTv.text = "Exceed Budget"
                linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.darkred_radius)
            } else {
                alertTv.text = "On Budget"
                linearLayout2.background = ContextCompat.getDrawable(this, R.drawable.darkgreen_radius)
            }
        } catch (e: NumberFormatException) {
            amountbudgetTv.text = "Error"
        }
    }

    private fun getTransactionsData() {
        dbref = FirebaseDatabase.getInstance().getReference("transactions")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    transactionArrayList.clear()
                    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    for (transactionSnapshot in snapshot.children) {
                        val transaction = transactionSnapshot.getValue(Transaction::class.java)
                        if (transaction != null) {
                            transactionArrayList.add(transaction)
                        }
                    }
                    transactionArrayList.sortByDescending { transaction ->
                        dateFormat.parse(transaction.date)?.time ?: 0L
                    }
                    transactionRecyclerView.adapter = MyAdapter(transactionArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
    companion object {
        const val EDIT_REQUEST_CODE = 1
    }
}
