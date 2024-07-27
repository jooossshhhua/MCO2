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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
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
        val allButton = findViewById<Button>(R.id.allButton)
        weeklyB.setOnClickListener{
            weeklyB.background = ContextCompat.getDrawable(this, R.drawable.home_button)
            todayB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
            allButton.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
            filterTransactionsByWeek()
        }
        todayB.setOnClickListener{
            todayB.background = ContextCompat.getDrawable(this, R.drawable.home_button)
            weeklyB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
            allButton.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)

            val currentDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(System.currentTimeMillis())
            filterTransactionsByDate(currentDate)
        }
        allButton.setOnClickListener{
            allButton.background = ContextCompat.getDrawable(this, R.drawable.home_button)
            weeklyB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
            todayB.background = ContextCompat.getDrawable(this, R.drawable.home_button_unclicked)
            transactionArrayList = arrayListOf()
            getTransactionsData()
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
        val user = FirebaseAuth.getInstance().currentUser?.uid

        if(user != null){
            dbref = FirebaseDatabase.getInstance().getReference("users").child(user).child("transactions")

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
                        updateWeeklyExpense()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }
        else{
            //Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }

    }

    private fun filterTransactionsByDate(date: String) {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val filteredList = transactionArrayList.filter { transaction ->
            dateFormat.format(dateFormat.parse(transaction.date)).equals(dateFormat.format(dateFormat.parse(date)))
        }
        // Convert List to ArrayList
        val filteredArrayList = ArrayList(filteredList)
        transactionRecyclerView.adapter = MyAdapter(filteredArrayList)
    }

    private fun updateWeeklyExpense() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        // Get start and end date of the current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = calendar.time

        val weeklyExpenses = transactionArrayList
            .filter { transaction ->
                val transactionDate = dateFormat.parse(transaction.date)
                transactionDate != null && transactionDate.after(startOfWeek) && transactionDate.before(endOfWeek)
            }
            .sumByDouble { transaction ->
                transaction.amount?.toDoubleOrNull() ?: 0.0
            }

        expTv.text = String.format("%.2f", weeklyExpenses)
        updateBudgetDisplay()
    }

    private fun filterTransactionsByWeek() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        // Get start and end date of the current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.time
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endOfWeek = calendar.time

        val filteredList = transactionArrayList.filter { transaction ->
            val transactionDate = dateFormat.parse(transaction.date)
            transactionDate != null && transactionDate.after(startOfWeek) && transactionDate.before(endOfWeek)
        }
        // Convert List to ArrayList
        val filteredArrayList = ArrayList(filteredList)
        transactionRecyclerView.adapter = MyAdapter(filteredArrayList)
    }


    companion object {
        const val EDIT_REQUEST_CODE = 1
    }
}
