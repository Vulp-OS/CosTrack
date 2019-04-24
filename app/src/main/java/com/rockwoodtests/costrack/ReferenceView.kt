package com.rockwoodtests.costrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_reference_view.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "type"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReferenceView.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ReferenceView.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReferenceView : Fragment() {
    private var db = FirebaseFirestore.getInstance()

    private var id: String? = null
    private var type: Int? = null
    private var listener: OnFragmentInteractionListener? = null

    private var adapter: ReferenceViewRecyclerViewAdapter? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reference_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            id = it.getString(ARG_PARAM1)
            type = it.getInt(ARG_PARAM2)
        }

        Log.d(TAG, "Provided ID is: $id")
        Log.d(TAG, "Provided Type is: $type")

        fabUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            activity?.startActivityForResult(intent, RESULT_LOAD_IMAGE)
        }

        refreshContainer.setOnRefreshListener {
            adapter?.clear()
            refreshList()

            refreshContainer.isRefreshing = false
        }

        registerForContextMenu(imageContainer)
        loadReferences()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_VIEW_IMAGE && resultCode == RESULT_DELETED) {
            Snackbar.make(fabUploadImage, "Image Successfully Deleted", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            refreshList()
        }
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun refreshList() {
        when(type) {
            0 -> refreshListForCosplay()
            1 -> refreshListForComponent()
            else -> Log.d(TAG, "Unknown type specified")
        }
    }

    private fun refreshListForCosplay() {
        db.collection("cosplays").document(id as String).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageReferences = ArrayList<String>()

                    if (document.data?.get("references") != null) {
                        val referenceURLs = document.data!!["references"] as ArrayList<*>

                        for (url in referenceURLs) {
                            imageReferences.add(url as String)
                        }
                    }

                    adapter!!.clear()
                    adapter!!.addAll(imageReferences)
                } else {
                    Log.d(TAG, "Could not find specified cosplay")
                }
            }
    }

    private fun refreshListForComponent() {
        db.collection("components").document(id as String).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageReferences = ArrayList<String>()

                    if (document.data?.get("references") != null) {

                        val referenceURLs = document.data!!["references"] as ArrayList<*>

                        for (url in referenceURLs) {
                            imageReferences.add(url as String)
                        }
                    }

                    adapter!!.clear()
                    adapter!!.addAll(imageReferences)
                } else {
                    Log.d(TAG, "Could not find specified cosplay")
                }
            }
    }

    private fun loadReferences() {
        when(type) {
            0 -> loadReferencesForCosplay()
            1 -> loadReferencesForComponent()
            else -> Log.d(TAG, "Unknown type specified")
        }
    }

    private fun loadReferencesForCosplay() {
        db.collection("cosplays").document(id as String).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    populateReferenceImages(document)
                } else {
                    Log.d(TAG, "Could not find specified cosplay")
                }
            }
    }

    private fun loadReferencesForComponent() {
        db.collection("components").document(id as String).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    populateReferenceImages(document)
                } else {
                    Log.d(TAG, "Could not find specified component")
                }
            }
    }

    /**
     * Returns a CustomItemClickListener that opens the PhotoReferenceViewer when triggered
     * @return listener
     */
    private fun getCustomItemClickListener(): CustomItemClickListener {
        return object : CustomItemClickListener {
            override fun onItemClick(v: View, position: Int, imagePath: String) {
                val intent = Intent(v.context, PhotoReferenceViewer::class.java)
                intent.putExtra("imagePath", imagePath)
                intent.putExtra("parentID", id)
                intent.putExtra("parentType", type)

                startActivityForResult(intent, RESULT_VIEW_IMAGE)
            }
        }
    }

    private fun populateReferenceImages(document: DocumentSnapshot) {
        val imageReferences = ArrayList<String>()

        if (document.data?.get("references") != null) {

            val referenceURLs = document.data!!["references"] as ArrayList<*>

            for (url in referenceURLs) {
                imageReferences.add(url as String)
            }
        }

        val listener = getCustomItemClickListener()

        adapter = ReferenceViewRecyclerViewAdapter(imageReferences, this.context!!, listener)

        imageContainer.adapter = adapter
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param id Parameter 1.
         * @param type Parameter 2.
         * @return A new instance of fragment ReferenceView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: String, type: Int) =
            ReferenceView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                    putInt(ARG_PARAM2, type)
                }
            }
        private const val TAG = "ReferenceView"
        private const val RESULT_LOAD_IMAGE=1
        private const val RESULT_VIEW_IMAGE = 2
        private const val RESULT_DELETED = 999
    }
}
