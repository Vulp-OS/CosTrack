package com.rockwoodtests.costrack

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.reference_view.view.*

class ReferenceViewRecyclerViewAdapter(private val items : ArrayList<StorageReference>, val context: Context) : RecyclerView.Adapter<ReferenceViewRecyclerViewAdapterViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ReferenceViewRecyclerViewAdapterViewHolder {
        return ReferenceViewRecyclerViewAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.reference_view, p0, false))
    }

    override fun onBindViewHolder(holder: ReferenceViewRecyclerViewAdapterViewHolder, position: Int) {
        GlideApp.with(this.context).load(items[position]).into(holder.referenceImage)

        holder.referenceImageCardView.setOnClickListener {
            ReferenceView().zoomFromThumb(holder.referenceImage.id)
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: ArrayList<StorageReference>) {
        items.addAll(list)
        notifyDataSetChanged()
    }
}

class ReferenceViewRecyclerViewAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var referenceImage = view.referenceImage!!
    var referenceImageCardView = view.referenceImageCardView!!
}