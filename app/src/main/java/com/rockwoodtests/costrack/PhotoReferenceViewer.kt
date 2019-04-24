package com.rockwoodtests.costrack

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_photo_reference_viewer.*
import kotlinx.android.synthetic.main.content_photo_reference_viewer.*

class PhotoReferenceViewer : AppCompatActivity() {
    private var imagePath: String? = null
    private var parentType: Int? = null
    private var parentID: String? = null

    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_reference_viewer)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imagePath = intent.extras?.getString("imagePath")
        parentType = intent.extras?.getInt("parentType")
        parentID = intent.extras?.getString("parentID")

        Log.d(TAG, "Obtaining Extras for Activity:")
        Log.d(TAG, "\tImagePath: $imagePath")
        Log.d(TAG, "\tparentType: $parentType")
        Log.d(TAG, "\tparentID: $parentID")

        loadImageIntoView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_photo_reference_viewer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete -> {
                if (!DataDeleter().deleteReferenceImage(imagePath!!, parentType!!, parentID!!)) {
                    Snackbar.make(CosplayReferencePhotoContainer, "Could Not Delete Reference Image!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                } else {
                    setResult(RESULT_DELETED)
                    finish()
                }
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadImageIntoView() {
        Log.d(TAG, imagePath)
        GlideApp.with(this).load(storage.getReferenceFromUrl(imagePath!!)).into(CosplayReferencePhotoContainer)
    }

    companion object {
        private const val TAG = "PhotoReferenceViewer"
        private const val RESULT_DELETED = 999
    }
}
