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
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_cosplay.*
import kotlinx.android.synthetic.main.content_edit_cosplay.*

private const val NUM_PAGES = 4

class EditCosplay : AppCompatActivity(), ComponentView.OnFragmentInteractionListener, ReferenceView.OnFragmentInteractionListener, ToolView.OnFragmentInteractionListener, StatView.OnFragmentInteractionListener {

    private var id: String? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_components -> {
                pagerManager.setCurrentItem(0, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_references -> {
                pagerManager.setCurrentItem(1, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tools -> {
                pagerManager.setCurrentItem(2, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                pagerManager.setCurrentItem(3, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private lateinit var pagerManager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_cosplay)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.extras?.getString("id")

        val data = Bundle()
        data.putString("id", id)
        data.putInt("type", 0)      // type 0 = Cosplay, type 1 = Component;    this allows the fragment to know what it is supposed to grab

        Log.d(TAG, "CosplayID: $id")

        pagerManager = mainCosplayContainer
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, data)
        val pageChangeListener = ScreenSlidePageChangeListener(cosplay_navigation)
        pagerManager.adapter = pagerAdapter
        pagerManager.offscreenPageLimit = 3
        pagerManager.addOnPageChangeListener(pageChangeListener)

        cosplay_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    fun showSelectedComponent(v: View) {
        val componentID = v.tag as String

        val intent = Intent(this, EditComponent::class.java)
        intent.putExtra("componentID", componentID)
        intent.putExtra("cosplayID", id)
        startActivity(intent)
        finish()
    }

    fun createNewComponent(v: View) {
        val intent = Intent(this, NewComponent::class.java)
        intent.putExtra("id", id)

        startActivity(intent)
    }

    fun uploadNewReference(v: View) {

    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, private val data: Bundle) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            when (position) {
                0  -> {
                    val cv = ComponentView()
                    cv.arguments = data
                    return cv
                }
                1 -> {
                    val rv = ReferenceView()
                    rv.arguments = data
                    return rv
                }
                2 -> {
                    val tv = ToolView()
                    tv.arguments = data
                    return tv
                }
                3 -> {
                    val sv = StatView()
                    sv.arguments = data
                    return sv
                }
            }
            val cv = ComponentView()
            cv.arguments = data

            return cv
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

    companion object {
        private const val TAG = "EditCosplay"
    }
}
