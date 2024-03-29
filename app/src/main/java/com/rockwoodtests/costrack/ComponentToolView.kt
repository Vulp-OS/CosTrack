package com.rockwoodtests.costrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_component_tool_view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "type"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ComponentToolView.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ComponentToolView.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ComponentToolView : Fragment() {
    private var db = FirebaseFirestore.getInstance()

    private var id: String? = null
    private var type: Int? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            type = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_component_tool_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "Provided ID is: $id")
        Log.d(TAG, "Provided Type is: $type")

        btnChangeCoverImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            activity?.startActivityForResult(intent, RESULT_LOAD_COVER_IMAGE)
        }

        loadComponentInfo()
    }

    private fun loadComponentInfo() {
        db.collection("components").document(id!!).get().addOnSuccessListener { componentDocument ->
            if (componentDocument != null) {
                dataComponentName.text = componentDocument.data!!["name"] as String
                dataComponentType.text = componentDocument.data!!["type"] as String
                dataMoneySpent.text = (componentDocument.data!!["money_spent"] as Long).toString()
                dataTimeSpent.text = (componentDocument.data!!["time_logged"] as Long).toString()
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CosplayToolView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CosplayToolView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        private const val TAG = "CosplayToolView"
        private const val RESULT_LOAD_COVER_IMAGE = 3
    }
}
