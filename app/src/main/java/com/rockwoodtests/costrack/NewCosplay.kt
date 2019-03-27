package com.rockwoodtests.costrack

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_new_cosplay.*
import kotlinx.android.synthetic.main.content_new_cosplay.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class NewCosplay : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_cosplay)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun saveNewCosplay(v: View) {
        // Verify all fields have content
        if (inputCosplayName.text.isEmpty() || inputCosplaySeries.text.isEmpty() || lblCosplayDueDate.text.isEmpty() || lblCosplayStartDate.text.isEmpty() || inputCosplayBudget.text.isEmpty()) {
            Snackbar.make(v, "Please fill all fields", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        } else {
            val cosplayData = HashMap<String, Any?>()

            // Parse information from activity
            try {
                val parsedStartDate = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(lblCosplayStartDate.text.toString())
                val parsedDueDate = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(lblCosplayDueDate.text.toString())

                cosplayData["name"] = inputCosplayName.text.toString()
                cosplayData["owner"] = user.uid
                cosplayData["budget"] = Integer.parseInt(inputCosplayBudget.text.toString())
                cosplayData["money_spent"] = 0
                cosplayData["cover_image"] = "gs://costrack.appspot.com/defaults/new-cosplay.png"
                cosplayData["due_date"] = Integer.parseInt(parsedDueDate.time.toString())       // Storing time in milliseconds from epoch
                cosplayData["start_date"] = Integer.parseInt(parsedStartDate.time.toString())
                cosplayData["components"] = ArrayList<String>()
            } catch (e: Exception) {
                Snackbar.make(v, "Could not parse fields. Please ensure contents are accurate.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                Log.e(TAG, "Error Parsing Data: ", e)
            }

            // Save Parsed information into FireStore
            try {
                db.collection("cosplays")
                    .add(cosplayData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot Written with ID: " + it.id)
                        this.finish()
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error adding document", it)
                    }
            } catch (e: Exception) {
                Snackbar.make(v, "Could not upload data.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                Log.e(TAG, "Error Uploading Data: ", e)
            }
        }
    }

    fun showStartDatePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, calendarYear, monthOfYear, dayOfMonth ->
            val text = "${monthOfYear+1}/$dayOfMonth/$calendarYear"     // Add 1 to monthOfYear because it starts at 0
            lblCosplayStartDate.text = text
        }, year, month, day)

        dialog.show()
    }

    fun showDueDatePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, calendarYear, monthOfYear, dayOfMonth ->
            val text = "${monthOfYear+1}/$dayOfMonth/$calendarYear"     // Add 1 to monthOfYear because it starts at 0
            lblCosplayDueDate.text = text
        }, year, month, day)

        dialog.show()
    }

    companion object {
        private const val TAG = "NewCosplay"
    }
}