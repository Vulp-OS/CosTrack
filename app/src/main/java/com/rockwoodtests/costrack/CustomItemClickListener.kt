package com.rockwoodtests.costrack

import android.view.View

interface CustomItemClickListener {
    fun onItemClick(v: View, position: Int, imagePath: String)
}