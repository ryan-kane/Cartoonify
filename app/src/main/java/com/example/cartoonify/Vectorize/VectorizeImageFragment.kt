package com.example.cartoonify.Vectorize

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cartoonify.ImageReadyListener
import com.example.cartoonify.PipelineFragment
import com.example.cartoonify.R

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_IMBITMAP = "imBitmap"

/**
 * A simple [Fragment] subclass.
 * Use the [VectorizeImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VectorizeImageFragment(listener: ImageReadyListener) :
    PipelineFragment(listener) {
    private var imBitmap: Bitmap? = null

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
        return inflater.inflate(R.layout.fragment_vectorize_image, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param listener ImageReadyListener.
         * @param imBitmap Image Bitmap.
         * @return A new instance of fragment VectorizeImageFragment.
         */
        @JvmStatic
        fun newInstance(listener: ImageReadyListener, imBitmap: Bitmap) =
            VectorizeImageFragment(listener).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
                }
            }
    }
}