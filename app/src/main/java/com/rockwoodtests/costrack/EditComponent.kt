package com.rockwoodtests.costrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
//import android.view.View
import kotlinx.android.synthetic.main.activity_edit_component.*
import kotlinx.android.synthetic.main.content_edit_component.*

private const val NUM_PAGES = 3

class EditComponent: AppCompatActivity(), ReferenceView.OnFragmentInteractionListener, ToolView.OnFragmentInteractionListener, StatView.OnFragmentInteractionListener{

    private var componentID: String? = null
    private var cosplayID: String? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_references -> {
                pagerManager.setCurrentItem(0, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tools -> {
                pagerManager.setCurrentItem(1, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                pagerManager.setCurrentItem(2, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private lateinit var pagerManager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_component)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        componentID = intent.extras?.getString("componentID")
        cosplayID = intent.extras?.getString("cosplayID")
        val data = Bundle()
        data.putString("id", componentID)
        data.putInt("type", 1)

        Log.d("EditComponent", "componentID: $componentID")
        Log.d("EditComponent", "cosplayID: $cosplayID")

        pagerManager = mainComponentContainer
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, data)
        val pageChangeListener = ScreenSlidePageChangeListener(component_navigation)
        pagerManager.adapter = pagerAdapter
        pagerManager.offscreenPageLimit = 2
        pagerManager.addOnPageChangeListener(pageChangeListener)

        component_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onBackPressed() {          // override the default behavior of pressing back, as the suspended view will not re-start with the proper arguments normally
        val intent = Intent(this, EditCosplay::class.java)
        intent.putExtra("id", cosplayID)
        startActivity(intent)
        finish()
    }

//    fun uploadNewReference(v: View) {
//
//    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, private val data: Bundle) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            when (position) {
                0  -> {
                    val rv = ReferenceView()
                    rv.arguments = data
                    return rv
                }
                1 -> {
                    val tv = ToolView()
                    tv.arguments = data
                    return tv
                }
                2 -> {
                    val sv = StatView()
                    sv.arguments = data
                    return sv
                }
            }
            return ReferenceView()
        }
    }

    private inner class ScreenSlidePageChangeListener(bottomNavigationView: BottomNavigationView) : ViewPager.OnPageChangeListener {
        var bnv = bottomNavigationView

        override fun onPageScrollStateChanged(p0: Int) {
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        }

        override fun onPageSelected(p0: Int) {
            bnv.menu.getItem(p0).isChecked = true
        }
    }
}