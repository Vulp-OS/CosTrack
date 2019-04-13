package com.rockwoodtests.costrack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_cosplay.*
import kotlinx.android.synthetic.main.content_edit_cosplay.*
import kotlinx.android.synthetic.main.fragment_cosplay_tool_view.*
import kotlinx.android.synthetic.main.fragment_reference_view.*
import java.util.*

private const val NUM_PAGES = 3

class EditCosplay : AppCompatActivity(), ComponentView.OnFragmentInteractionListener, ReferenceView.OnFragmentInteractionListener, CosplayToolView.OnFragmentInteractionListener, StatView.OnFragmentInteractionListener {

    private var id: String? = null

    private var user = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    private var startTime = 0L
    private var millisecondTime = 0L
    private var timeBuff = 0L
    private var totalTime = 0L
    private val handler = Handler()

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
        pagerManager.offscreenPageLimit = 2
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

    private fun uploadImage(uri: Uri) {
        val uuid = UUID.randomUUID()
        val path = user.uid + "/" + id + "/" + uuid

        val imageRef = storage.reference.child(path)

        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                db.collection("cosplays").document(id as String)
                    .update("references", FieldValue.arrayUnion(it.toString()))
                    .addOnSuccessListener {
                        Snackbar.make(fabUploadImage, "Image Successfully Uploaded", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                uploadImage(data.data!!)
            }
        } else if (requestCode == RESULT_VIEW_IMAGE && resultCode == Activity.RESULT_OK) {
            Snackbar.make(imageContainer, "Image Viewed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    fun startTimer(v: View) {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 1000)
        btnStartTimer.isEnabled = false
        btnFinishTimer.isEnabled = false
        btnPauseTimer.isEnabled = true
    }

    fun pauseTimer(v: View) {
        timeBuff += millisecondTime
        handler.removeCallbacks(runnable)
        btnStartTimer.isEnabled = true
        btnFinishTimer.isEnabled = true
        btnPauseTimer.isEnabled = false
    }

    private val runnable = object: Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            totalTime = timeBuff + millisecondTime
            var seconds = totalTime/1000
            var minutes = seconds / 60
            val hours = minutes / 60

            minutes %= 60
            seconds %= 60

            lblTimer.text = ("${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}")

            handler.postDelayed(this, 1000)
        }
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
                    val tv = CosplayToolView()
                    tv.arguments = data
                    return tv
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

    fun zoomInCosplayContainer(imagePath: String) {
        Log.d(TAG, "Before creating Intent")
        val intent = Intent(this, CosplayReferenceViewer::class.java)
        Log.d(TAG, "After creating Intent")
        intent.putExtra("imagePath", imagePath)

        startActivityForResult(intent, RESULT_VIEW_IMAGE)
    }

    companion object {
        private const val TAG = "EditCosplay"
        private const val RESULT_LOAD_IMAGE = 1
        private const val RESULT_VIEW_IMAGE = 2
    }
}
