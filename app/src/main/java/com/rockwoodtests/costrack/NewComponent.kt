package com.rockwoodtests.costrack

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import kotlinx.android.synthetic.main.activity_new_component.*
import kotlinx.android.synthetic.main.content_new_component.*

class NewComponent : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_component)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.extras?.getString("id")
    }

    fun saveNewComponent(v: View) {
        if (inputComponentName.text.isEmpty() || inputComponentAmountSpent.text.isEmpty()) {
            Snackbar.make(v, "Please fill all fields", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        } else {
            val componentData = HashMap<String, Any?>()

            try {
                var type = "None"

                if (inputComponentRadioBought.isChecked)
                    type = "Bought"
                if (inputComponentRadioFound.isChecked)
                    type = "Found"
                if (inputComponentRadioMade.isChecked)
                    type = "Made"

                componentData["name"] = inputComponentName.text.toString()
                componentData["owner"] = user.uid
                componentData["money_spent"] = Integer.parseInt(inputComponentAmountSpent.text.toString())
                componentData["type"] = type
                componentData["cover_image"] = "gs://costrack.appspot.com/defaults/new-component.png"
                componentData["references"] = ArrayList<String>()
                componentData["time_logged"] = 0

            } catch (e: Exception) {
                Snackbar.make(v, "Could not parse fields. Please ensure contents are accurate.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                Log.e(TAG, "Error Parsing Data: ", e)
            }

            try {
                db.collection("components")
                    .add(componentData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot Written with ID: " + it.id)

                        db.collection("cosplays").document(id as String).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    if (document.data?.get("components") != null) {
                                        @Suppress ("UNCHECKED_CAST")
                                        val componentKeys = document.data!!["components"] as ArrayList<String>
                                        componentKeys.add(it.id)

                                        val cosplayDataPayload = HashMap<String, Any?>()
                                        cosplayDataPayload["components"] = componentKeys

                                        db.collection("cosplays").document(id as String).set(cosplayDataPayload, SetOptions.merge())
                                    }
                                }

                                setResult(Activity.RESULT_OK)
                                this.finish()
                            }
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "NewComponent"
    }
}
