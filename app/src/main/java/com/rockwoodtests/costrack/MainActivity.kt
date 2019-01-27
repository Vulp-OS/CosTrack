package com.rockwoodtests.costrack

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ad_view.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        if((0..10).random() >= 6)
            showAd()


        loadCosplays()
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

    fun createNewCosplay(v: View) {
        startActivity(Intent(this, NewCosplay::class.java))
    }

    fun showSelectedCosplay(v: View) {
        startActivity(Intent(this, EditCosplay::class.java))
    }

    private fun loadCosplays() {
        for (i in 1..10) {
            val inflatedLayout = layoutInflater.inflate(R.layout.cosplay_view, null, false)

            cardContainer.addView(inflatedLayout)
        }
    }

    private fun showAd() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")

        val inflatedLayout = layoutInflater.inflate(R.layout.ad_view, null, false)

        cardContainer.addView(inflatedLayout, 0)

        adView.loadAd(AdRequest.Builder().addTestDevice("4C7E70DE46968B35CAA28E6C24111C19").build())
    }
}
