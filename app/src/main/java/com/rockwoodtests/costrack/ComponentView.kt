package com.rockwoodtests.costrack

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.ad_view.*
import kotlinx.android.synthetic.main.component_view.view.*
import kotlinx.android.synthetic.main.fragment_component_view.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ComponentView.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ComponentView.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ComponentView : Fragment() {
    private var db = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()

    private var cosplayID: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_component_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            cosplayID = it.getString(ARG_PARAM1)
            Log.d(TAG, "CosplayID: $cosplayID")
        }

        arguments?.keySet()?.forEach {
            Log.d(TAG, "Argument KeySet: $it")
        }

        if((0..10).random() >= 6)
            showAd()

        loadComponents()
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

    private fun loadComponents() {
        db.collection("cosplays").document(cosplayID as String).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data?.get("components") != null) {
                        val componentKeys = document.data!!["components"] as ArrayList<*>

                        for (key in componentKeys) {
                            db.collection("components").document(key as String).get()
                                .addOnSuccessListener { componentDocument ->
                                    Log.d(TAG, "Loading Component $componentDocument")
                                    if (componentDocument.data != null) {
                                        val inflatedLayout =
                                            layoutInflater.inflate(R.layout.component_view, cosplayContainer, false)
                                        inflatedLayout.componentCardView.tag = componentDocument.id

                                        inflatedLayout.componentCoverImageText.text = componentDocument.data!!["name"] as String

                                        val coverImageRef =
                                            storage.getReferenceFromUrl(componentDocument.data!!["cover_image"] as String)
                                        GlideApp.with(this).load(coverImageRef).into(inflatedLayout.componentCoverImage)

                                        cosplayContainer.addView(inflatedLayout)

                                    } else {
                                        Log.d(TAG, "Could not find specified component")
                                    }
                                }
                        }
                    }

                } else {
                    Log.d(TAG, "Could not find specified cosplay")
                }
            }

        //db.collection("components").
    }

    private fun showAd() {
        MobileAds.initialize(activity, "ca-app-pub-3940256099942544~3347511713")

        val inflatedLayout = layoutInflater.inflate(R.layout.ad_view, cosplayContainer, false)

        cosplayContainer.addView(inflatedLayout, 0)

        adView.loadAd(AdRequest.Builder().addTestDevice("4C7E70DE46968B35CAA28E6C24111C19").build())
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
         * @param cosplayID Parameter 1.
         * @return A new instance of fragment ComponentView.
         */
        @JvmStatic
        fun newInstance(cosplayID: String) =
            ComponentView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, cosplayID)
                }
            }

        private const val TAG = "ComponentView"
    }
}
