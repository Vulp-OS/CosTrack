package com.rockwoodtests.costrack

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cosplay_tool_view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "type"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CosplayToolView.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CosplayToolView.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CosplayToolView : Fragment() {
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
        return inflater.inflate(R.layout.fragment_cosplay_tool_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "Provided ID is: $id")
        Log.d(TAG, "Provided Type is: $type")

        loadCosplayInfo()
    }

    private fun loadCosplayInfo() {
        db.collection("cosplays").document(id!!).get().addOnSuccessListener { cosplayDocument ->
            if (cosplayDocument != null) {
                dataCosplayName.text = cosplayDocument.data!!["name"] as String
                dataNumberComponents.text = (cosplayDocument.data!!["components"] as ArrayList<*>).size.toString()

                // Determine how much money has been spent on all cosplays. Also add up time spent at same time
                val componentKeys = cosplayDocument.data!!["components"] as ArrayList<*>

                val miscTimeSpent = cosplayDocument.data!!["time_logged"] as Long

                dataMoneySpent.text = 0.toString()
                dataTimeSpent.text = miscTimeSpent.toString()
                dataMiscTimeSpent.text = miscTimeSpent.toString()

                for (key in componentKeys) {
                    db.collection("components").document(key as String).get()
                        .addOnSuccessListener {
                            if (it != null) {
                                var moneyPlaceholder = (dataMoneySpent.text as String).toLong()
                                moneyPlaceholder += it.data!!["money_spent"] as Long
                                dataMoneySpent.text = moneyPlaceholder.toString()

                                var timePlaceholder = (dataTimeSpent.text as String).toLong()
                                timePlaceholder += it.data!!["time_logged"] as Long
                                dataTimeSpent.text = timePlaceholder.toString()
                            }
                        }
                }

                //TODO: Fill Start/Due data later
            }
        }
    }

    fun saveTimeSpent(time: Long) {

    }

    // TODO: Rename method, update argument and hook method into UI event
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
    }
}
