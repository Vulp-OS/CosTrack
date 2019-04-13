package com.rockwoodtests.costrack

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_cosplay_reference_viewer.*
import kotlinx.android.synthetic.main.content_cosplay_reference_viewer.*

class CosplayReferenceViewer : AppCompatActivity() {
    private var imagePath: String? = null

    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cosplay_reference_viewer)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imagePath = intent.extras?.getString("imagePath")

        loadImageIntoView()
    }

    private fun loadImageIntoView() {
        GlideApp.with(this).load(imagePath).into(CosplayReferencePhotoContainer)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = intent
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

}
