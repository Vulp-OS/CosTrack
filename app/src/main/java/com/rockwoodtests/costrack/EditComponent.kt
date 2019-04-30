package com.rockwoodtests.costrack

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_component.*
import kotlinx.android.synthetic.main.content_edit_component.*
import kotlinx.android.synthetic.main.fragment_component_tool_view.*
import kotlinx.android.synthetic.main.fragment_reference_view.*
import java.util.*

private const val NUM_PAGES = 2

class EditComponent: AppCompatActivity(), ReferenceView.OnFragmentInteractionListener, ComponentToolView.OnFragmentInteractionListener {
    private var user = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    private var componentID: String? = null
    private var cosplayID: String? = null

    private var startTime = 0L
    private var millisecondTime = 0L
    private var timeBuff = 0L
    private var totalTime = 0L
    private val handler = Handler()

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

        Log.d(TAG, "componentID: $componentID")
        Log.d(TAG, "cosplayID: $cosplayID")

        pagerManager = mainComponentContainer
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, data)
        val pageChangeListener = ScreenSlidePageChangeListener(component_navigation)
        pagerManager.adapter = pagerAdapter
        pagerManager.offscreenPageLimit = 1
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

    private fun uploadReferenceImage(uri: Uri) {
        val uuid = UUID.randomUUID()
        val path = user.uid + "/" + cosplayID + "/" + componentID + "/" + uuid

        val imageRef = storage.reference.child(path)

        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                db.collection("components").document(componentID as String)
                    .update("references", FieldValue.arrayUnion(it.toString()))
                    .addOnSuccessListener {
                        Snackbar.make(fabUploadImage, "Image Successfully Uploaded", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        pagerManager.adapter!!.notifyDataSetChanged()
                    }
            }
        }
    }

    private fun uploadCoverImage(uri: Uri) {
        val uuid = UUID.randomUUID()
        val path = user.uid + "/" + cosplayID + "/" + componentID + "/" + uuid

        val imageRef = storage.reference.child(path)

        // Upload new cover image. Don't perform any additional tasks if upload does not succeed.
        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {newRef ->

                //Delete old cover image if it isn't the default image and change URL to new cover image
                db.collection("components").document(componentID as String)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val oldCoverImageURL = document.data!!["cover_image"] as String
                            val oldRef = storage.getReferenceFromUrl(oldCoverImageURL)
                            if (!oldRef.path.contains("defaults")) {
                                // Delete the image. Don't change image URL if delete fails
                                oldRef.delete().addOnSuccessListener {
                                    db.collection("components").document(componentID as String)
                                        .update("cover_image", newRef.toString())
                                        .addOnSuccessListener {
                                            Snackbar.make(btnChangeCoverImage, "Cover Image Successfully Changed", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show()
                                            pagerManager.adapter!!.notifyDataSetChanged()
                                        }
                                }
                                    // Delete new image if old image deletion fails
                                    .addOnFailureListener {
                                        imageRef.delete()
                                    }
                            } else {
                                db.collection("components").document(componentID as String)
                                    .update("cover_image", newRef.toString())
                                    .addOnSuccessListener {
                                        Snackbar.make(btnChangeCoverImage, "Cover Image Successfully Changed", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show()
                                        pagerManager.adapter!!.notifyDataSetChanged()
                                    }
                            }
                        }
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_REFERENCE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                uploadReferenceImage(data.data!!)
            }
        }

        if (requestCode == RESULT_LOAD_COVER_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                uploadCoverImage(data.data!!)
                setResult(RESULT_COVER_IMAGE_CHANGED)
            }
        }
    }

    fun startTimer(v: View) {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(timerRunnable, 1000)
        btnStartTimer.isEnabled = false
        btnFinishTimer.isEnabled = false
        btnPauseTimer.isEnabled = true
    }

    fun pauseTimer(v: View) {
        timeBuff += millisecondTime
        handler.removeCallbacks(timerRunnable)
        btnStartTimer.isEnabled = true
        btnFinishTimer.isEnabled = true
        btnPauseTimer.isEnabled = false
    }

    fun finishTimer(v: View) {
        // Use cosplay id $id to accumulate time to the correct location
        val resetTime = "00:00:00"
        val timeToStore = totalTime/1000 + dataTimeSpent.text.toString().toInt()

        db.collection("components").document(componentID!!).update("time_logged", timeToStore).addOnSuccessListener {
            dataTimeSpent.text = timeToStore.toString()

            totalTime = 0
            timeBuff = 0
            lblTimer.text = resetTime
        }
    }

    fun renameComponent(v: View) {
        val inputNewName = EditText(v.context)

        val dialog = AlertDialog.Builder(v.context)
            .setTitle("Rename Component")
            .setMessage("Enter new component name.")
            .setView(inputNewName)
            .setPositiveButton("Rename", DialogInterface.OnClickListener { dialog, which ->
                db.collection("components").document(componentID!!).update("name", inputNewName.text.toString())
                dataComponentName.text = inputNewName.text
            })
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    fun deleteComponent(v: View) {
        val result = DataDeleter().deleteComponent(componentID!!, cosplayID!!)
        setResult(RESULT_COMPONENT_DELETED)
        this.finish()
    }

    private val timerRunnable = object: Runnable {
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
                    val rv = ReferenceView()

                    rv.arguments = data
                    return rv
                }
                1 -> {
                    val tv = ComponentToolView()
                    tv.arguments = data
                    return tv
                }
            }
            return ReferenceView()
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
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
        private const val TAG = "EditComponent"
        private const val RESULT_LOAD_REFERENCE_IMAGE = 1
        private const val RESULT_LOAD_COVER_IMAGE = 3
        private const val RESULT_COVER_IMAGE_CHANGED = 996
        private const val RESULT_COMPONENT_DELETED = 998
    }
}