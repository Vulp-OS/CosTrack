package com.rockwoodtests.costrack

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_component.*
import kotlinx.android.synthetic.main.ad_view.*
import kotlinx.android.synthetic.main.content_edit_cosplay.*

class EditComponent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_component)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if((0..10).random() >= 6)
            showAd()

        loadSubComponents()
    }

    fun showSelectedComponent(v: View) {
        startActivity(Intent(this, EditComponent::class.java))
    }

    fun createNewComponent(v: View) {
        startActivity(Intent(this, NewComponent::class.java))
    }

    private fun loadSubComponents() {
        for (i in 1..10) {
            val inflatedLayout = layoutInflater.inflate(R.layout.component_view, null, false)

            componentContainer.addView(inflatedLayout)
        }
    }

    private fun showAd() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")

        val inflatedLayout = layoutInflater.inflate(R.layout.ad_view, null, false)

        componentContainer.addView(inflatedLayout, 0)

        adView.loadAd(AdRequest.Builder().addTestDevice("4C7E70DE46968B35CAA28E6C24111C19").build())
    }
}
