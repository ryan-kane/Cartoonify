package com.example.cartoonify

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_show_photo.*
import org.opencv.android.Utils
import org.opencv.core.Mat

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

private const val ARG_IMBITMAP = "imBitmap"

class ShowPhoto : Fragment() {

    val TAG = "Show Photo"
    private var imBitmap: Bitmap? = null
    private val im = Mat()

    companion object {

        @JvmStatic
        fun newInstance(imBitmap: Bitmap?) =
            ShowPhoto().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
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
        if(imBitmap != null) {
            view_show_photo.setImageBitmap(imBitmap)
        }
        view.findViewById<Button>(R.id.button_confirm)
    }


}
