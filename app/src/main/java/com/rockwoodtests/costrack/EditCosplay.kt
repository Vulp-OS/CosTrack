package com.rockwoodtests.costrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_cosplay.*
import kotlinx.android.synthetic.main.ad_view.*
import kotlinx.android.synthetic.main.content_edit_component.*
import kotlinx.android.synthetic.main.content_edit_cosplay.*

class EditCosplay : AppCompatActivity(), CosplayView.OnFragmentInteractionListener, ReferenceView.OnFragmentInteractionListener, ToolView.OnFragmentInteractionListener, StatView.OnFragmentInteractionListener {

    private val cosplayFragment = CosplayView() as Fragment
    private val referenceFragment = ReferenceView() as Fragment
    private val toolFragment = ToolView() as Fragment
    private val statFragment = StatView() as Fragment
    private var active = cosplayFragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_components -> {
                fragmentManager.beginTransaction().hide(active).show(cosplayFragment).commit()
                active = cosplayFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_references -> {
                fragmentManager.beginTransaction().hide(active).show(referenceFragment).commit()
                active = referenceFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tools -> {
                fragmentManager.beginTransaction().hide(active).show(toolFragment).commit()
                active = toolFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                fragmentManager.beginTransaction().hide(active).show(statFragment).commit()
                active = statFragment
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cosplay)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cosplay_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fragmentManager.beginTransaction().add(R.id.mainComponentContainer, statFragment, "4").hide(statFragment).commit()
        fragmentManager.beginTransaction().add(R.id.mainComponentContainer, toolFragment, "3").hide(toolFragment).commit()
        fragmentManager.beginTransaction().add(R.id.mainComponentContainer, referenceFragment, "2").hide(toolFragment).commit()
        fragmentManager.beginTransaction().add(R.id.mainComponentContainer, cosplayFragment, "1").commit()
    }

    fun showSelectedComponent(v: View) {
        startActivity(Intent(this, EditComponent::class.java))
    }

    fun createNewComponent(v: View) {
        startActivity(Intent(this, NewComponent::class.java))
    }

    override fun onFragmentInteraction(uri: Uri) {

    }


}
