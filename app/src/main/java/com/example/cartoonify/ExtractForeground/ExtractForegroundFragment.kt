package com.example.cartoonify.ExtractForeground

import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cartoonify.ImageReadyListener
import com.example.cartoonify.R
import kotlinx.android.synthetic.main.fragment_extract_foreground.*
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

private const val ARG_IMBITMAP = "imBitmap"

private const val TAG = "ExtractForegroundFragment"


/**
 * A simple [Fragment] subclass.
 * Use the [ExtractForegroundFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExtractForegroundFragment(val listener: ImageReadyListener) :
    Fragment(),
    ExtractForegroundListener{

    private var imBitmap: Bitmap? = null
    private var im: Mat? = null
    private lateinit var extractForeGroundView: ExtractForeGroundView
    private lateinit var foregroundExtractor: ForegroundExtractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
        }

        im = Mat(imBitmap!!.height, imBitmap!!.width, CvType.CV_8UC1)
        Utils.bitmapToMat(imBitmap, im)
        foregroundExtractor = ForegroundExtractor(this)
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
        resetExtractForegroundView(imBitmap!!)
    }

    override fun markBackground(maskBitmap: Bitmap) {
        Log.d(TAG,"background marked")
        listener.imageNotReady()
        progress_bar_extract_foreground.visibility = View.VISIBLE
        extractForeGroundView.isEnabled = false
        // convert to mat
        val mask = Mat(maskBitmap.height, maskBitmap.width, CvType.CV_8UC1)
        Utils.bitmapToMat(maskBitmap, mask)
        foregroundExtractor.extractForeground(im!!, mask) // happens in other thread
    }

    override fun foregroundExtracted(im: Mat) {
        Log.d(TAG,"foreground extracted")
        requireActivity().runOnUiThread(Runnable{
            imBitmap = Bitmap.createBitmap(im.cols(), im.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(im, imBitmap)
            resetExtractForegroundView(imBitmap!!)
            progress_bar_extract_foreground.visibility = View.GONE
            listener.imageReady(imBitmap!!)
        })
    }

    private fun resetExtractForegroundView(imBitmap: Bitmap) {
        extractForeGroundView = ExtractForeGroundView(this.requireContext())
        extractForeGroundView.setExtractForegroundListener(this)
        extractForeGroundView.setImBitmap(imBitmap)
        extract_foreground_container.removeAllViews()
        extract_foreground_container.layoutParams.width = imBitmap.width
        extract_foreground_container.layoutParams.height = imBitmap.height
        extract_foreground_container.addView(extractForeGroundView)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imBitmap Image Bitmap.
         * @return A new instance of fragment ExtractForegroundFragment.
         */
        @JvmStatic
        fun newInstance(listener: ImageReadyListener, imBitmap: Bitmap) =
            ExtractForegroundFragment(listener).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
                }
            }
    }
}
