package com.rockwoodtests.costrack

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.reference_view.view.*

class ReferenceViewRecyclerViewAdapter(private val items : ArrayList<String>, val context: Context, private val listener : CustomItemClickListener) : RecyclerView.Adapter<ReferenceViewRecyclerViewAdapterViewHolder>() {
    private val storage = FirebaseStorage.getInstance()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ReferenceViewRecyclerViewAdapterViewHolder {
        val mView = LayoutInflater.from(context).inflate(R.layout.reference_view, p0, false)

        return ReferenceViewRecyclerViewAdapterViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ReferenceViewRecyclerViewAdapterViewHolder, position: Int) {
        GlideApp.with(context).load(storage.getReferenceFromUrl(items[position])).into(holder.referenceImage)

        holder.referenceImageCardView.setOnClickListener {
            //zoomInCosplayContainer(it, items[position].path)
            listener.onItemClick(it, position, items[position])
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addAll(list: ArrayList<String>) {
        items.addAll(list)
        notifyDataSetChanged()
    }
}

class ReferenceViewRecyclerViewAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var referenceImage = view.referenceImage!!
    var referenceImageCardView = view.referenceImageCardView!!
}

