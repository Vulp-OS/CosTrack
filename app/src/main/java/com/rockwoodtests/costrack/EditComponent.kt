package com.rockwoodtests.costrack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
//import android.view.View
import kotlinx.android.synthetic.main.activity_edit_component.*
import kotlinx.android.synthetic.main.content_edit_component.*
import kotlinx.android.synthetic.main.fragment_reference_view.*
import java.util.*

private const val NUM_PAGES = 2

class EditComponent: AppCompatActivity(), ReferenceView.OnFragmentInteractionListener, ToolView.OnFragmentInteractionListener, StatView.OnFragmentInteractionListener{
    private var user = FirebaseAuth.getInstance().currentUser!!
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

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

//    fun uploadNewReference(v: View) {
//        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//
//        startActivityForResult(intent, RESULT_LOAD_IMAGE)
//    }

    private fun uploadImage(uri: Uri) {
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
        }
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

    companion object {
        private const val TAG = "EditComponent"
        private const val RESULT_LOAD_IMAGE = 1
    }
}