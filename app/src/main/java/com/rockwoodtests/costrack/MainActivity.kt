package com.rockwoodtests.costrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ad_view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.cosplay_view.view.*
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private val api = GoogleApiAvailability.getInstance()

    private var adapter: CosplayViewRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (api.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            // Force user to sign in if they are not already signed in to an account
            if (user == null) {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN
                )
            } else {
//                refreshContainer.setOnRefreshListener {
//                    adapter?.clear()
//                    refreshList()
//
//                    refreshContainer.isRefreshing = false
//                }

                loadContent()
            }
        } else {
            api.getErrorDialog(this, api.isGooglePlayServicesAvailable(this), RC_SIGN_IN).show()
        }
    }

    private fun loadContent() {
        if ((0..10).random() >= 6)
            showAd()

        initialLoadCosplays()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            //val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                user = FirebaseAuth.getInstance().currentUser
                db = FirebaseFirestore.getInstance()
                storage = FirebaseStorage.getInstance()

                loadContent()
            } else {
                TODO("Handle failed login")
            }
        }
    }

    fun createNewCosplay(v: View) {
        startActivity(Intent(this, NewCosplay::class.java))
    }

    fun showSelectedCosplay(v: View) {
        val cosplayID = v.tag as String

        val intent = Intent(this, EditCosplay::class.java)
        intent.putExtra("id", cosplayID)
        startActivity(intent)
    }

    private fun initialLoadCosplays() {
        Log.d(TAG, "initialLoadCosplays: " + user?.uid)


        db.collection("cosplays").whereEqualTo("owner", user!!.uid).get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents.documents) {
                        val inflatedLayout = layoutInflater.inflate(R.layout.cosplay_view, cardContainer, false)
                        inflatedLayout.cosplayCardView.tag = document.id

                        val titleCardName = document.data!!["name"] as String + " - " + document.data!!["series"] as String
                        inflatedLayout.cosplayCoverImageText.text = titleCardName

                        val coverImageRef = storage.getReferenceFromUrl(document.data!!["cover_image"] as String)
                        GlideApp.with(this).load(coverImageRef).into(inflatedLayout.cosplayCoverImage)

                        cardContainer.addView(inflatedLayout)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

//    private fun initialLoadCosplays() {
//        db.collection("cosplays").whereEqualTo("owner", user!!.uid).get()
//            .addOnSuccessListener { query ->
//                populateCosplayCards(query)
//            }
//    }
//
//    private fun populateCosplayCards(query: QuerySnapshot) {
//        //val cosplayData = HashMap<String, HashMap<String, String>>()
//        val cosplays = ArrayList<HashMap<String, String>>()
//
//        for (document in query.documents) {
//            if (document != null) {
//                val cosplayData = HashMap<String, String>()
//                cosplayData["name"] = document.data!!["name"] as String
//                cosplayData["series"] = document.data!!["series"] as String
//                cosplayData["coverImageURL"] = document.data!!["cover_image"] as String
//                cosplayData["tag"] = document.id
//
//                cosplays.add(cosplayData)
//            }
//        }
//
//        Log.d(TAG, cosplays.toString())
//
//        adapter = CosplayViewRecyclerViewAdapter(cosplays, this)
//
//        Log.d(TAG, adapter?.itemCount.toString())
//
//        cardContainer.adapter = adapter
//    }
//
//    private fun refreshList() {
//
//    }

    private fun showAd() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")

        val inflatedLayout = layoutInflater.inflate(R.layout.ad_view, cardContainer, false)

        cardContainer.addView(inflatedLayout, 0)

        adView.loadAd(AdRequest.Builder().addTestDevice("4C7E70DE46968B35CAA28E6C24111C19").build())
    }

    companion object {
        private const val RC_SIGN_IN = 123
        private const val TAG = "MainActivity"
    }
}