package com.example.cartoonify.Pixelate

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cartoonify.R
import org.opencv.android.Utils
import org.opencv.core.Mat

private const val ARG_IMBITMAP = "imBitMap"
private const val TAG = "PixelateSelectFragment"

enum class PixelateOptions {
    KMEANS
}

class PixelateSelectFragment: Fragment() {

    private var imBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
        }
        if(imBitmap == null) {
            Toast.makeText(requireContext(), "No Image", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imBitmap Image BitMap to be passed to this function.
         * @return A new instance of fragment PixelateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(imBitmap: Bitmap) =
            PixelateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
                }
            }
    }


    private inner class PagerAdapter(f: Fragment): FragmentStateAdapter(f) {
        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun createFragment(position: Int): Fragment {
            return PixelateFragment()
        }

    }
}