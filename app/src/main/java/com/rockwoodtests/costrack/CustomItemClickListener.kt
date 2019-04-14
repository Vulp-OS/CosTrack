package com.rockwoodtests.costrack

import android.view.View
import com.google.firebase.storage.StorageReference

interface CustomItemClickListener {
    fun onItemClick(v: View, position: Int, imagePath: String)
}