package com.example.cartoonify

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val ARG_PHOTO_URI = "photo_uri"

class ShowPhoto : Fragment() {

    val TAG = "Show Photo"
    private var photo_uri_string: String? = null

    companion object {

        @JvmStatic
        fun newInstance(param1: String?) =
            ShowPhoto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHOTO_URI, param1)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photo_uri_string = it.getString(ARG_PHOTO_URI)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(photo_uri_string != null) {
            val photoUri: Uri = Uri.parse(photo_uri_string)
            showPhoto(photoUri)
        }
        view.findViewById<Button>(R.id.button_confirm)
    }

    public fun showPhoto(photoUri: Uri) {
        val imageView = view?.findViewById<ImageView>(R.id.view_show_photo)
        imageView?.setImageURI(photoUri)
    }

}
