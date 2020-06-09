package com.example.cartoonify

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private const val ARG_PARAM1 = "param1"

private const val TAG = "ExtractForegroundFragment"


/**
 * A simple [Fragment] subclass.
 * Use the [ExtractForegroundFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExtractForegroundFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var photoUriString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoUriString = it.getString(ARG_PARAM1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extract_foreground, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(photoUriString != null) {
            val photoUri: Uri = Uri.parse(photoUriString)
//            view.findViewById<ImageView>(R.id.extractForegroundImageView).setImageURI(photoUri)
        }else{
            Log.e(TAG, "PhotoURI is null")
            // TODO: Fail Activity
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExtractForegroundFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(photoUriString: String) =
            ExtractForegroundFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, photoUriString)
                }
            }
    }
}
