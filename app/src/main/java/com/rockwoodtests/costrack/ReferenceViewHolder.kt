package com.rockwoodtests.costrack

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.reference_view.view.*
import kotlin.properties.Delegates

class ReferenceViewRecyclerViewAdapter(private val items : ArrayList<StorageReference>, val context: Context, private val listener : CustomItemClickListener) : RecyclerView.Adapter<ReferenceViewRecyclerViewAdapterViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ReferenceViewRecyclerViewAdapterViewHolder {
        val mView = LayoutInflater.from(context).inflate(R.layout.reference_view, p0, false)

        return ReferenceViewRecyclerViewAdapterViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ReferenceViewRecyclerViewAdapterViewHolder, position: Int) {
        GlideApp.with(context).load(items[position]).into(holder.referenceImage)

        holder.referenceImageCardView.setOnClickListener {
            //zoomInCosplayContainer(it, items[position].path)
            listener.onItemClick(it, position, items[position].path)
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

