package com.rockwoodtests.costrack

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.cosplay_view.view.*

class CosplayViewRecyclerViewAdapter(private var items : ArrayList<HashMap<String, String>>, val context: Context) : RecyclerView.Adapter<CosplayViewRecyclerViewAdapterViewHolder>() {
    private val storage = FirebaseStorage.getInstance()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CosplayViewRecyclerViewAdapterViewHolder {
        val mView = LayoutInflater.from(context).inflate(R.layout.cosplay_view, p0, false)

        return CosplayViewRecyclerViewAdapterViewHolder(mView)
    }

    override fun onBindViewHolder(holder: CosplayViewRecyclerViewAdapterViewHolder, position: Int) {
        val titleText = (items[position])["name"]!! + " - " + (items[position])["series"]!!

        GlideApp.with(context).load(storage.getReferenceFromUrl((items[position])["coverImageURL"]!!)).into(holder.cosplayImage)
        holder.cosplayText.text = titleText
        holder.cosplayImageCardView.tag = (items[position])["tag"]!!

        Log.d("CosplayViewHolder", "\n\tName: " + items[position]["name"] + "\n\tcoverImageURL: " + items[position]["coverImageURL"] + "\n\tTag: " + items[position]["tag"])
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: ArrayList<HashMap<String, String>>) {
        items = list
        notifyDataSetChanged()
    }
}

class CosplayViewRecyclerViewAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var cosplayImage = view.cosplayCoverImage!!
    var cosplayText = view.cosplayCoverImageText!!
    var cosplayImageCardView = view.cosplayCardView!!
}