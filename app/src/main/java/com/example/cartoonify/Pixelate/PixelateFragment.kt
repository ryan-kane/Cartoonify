package com.example.cartoonify.Pixelate

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cartoonify.ImageReadyListener
import com.example.cartoonify.R
import kotlinx.android.synthetic.main.fragment_pixelate.*
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

private const val ARG_IMBITMAP = "imBitMap"
private const val ARG_PIXELATE_TYPE = "pixelateType"

/**
 * A simple [Fragment] subclass.
 * Use the [PixelateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private val TAG = "PixelateFragment"

class PixelateFragment(val listener: ImageReadyListener) :
    Fragment(),
    Pixelater.PixelaterResponseListener {

    private var imBitmap: Bitmap? = null
    private var pixelateType: Int? = null
    private lateinit var im: Mat
    private lateinit var imSize: Size // original size of image for resizing at the end

    lateinit var pixelater: Pixelater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
//            pixelateType = it.getInt(ARG_PIXELATE_TYPE)
        }
        if(imBitmap == null) {
            Toast.makeText(requireContext(), "No Image", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }else{
            im = Mat(imBitmap!!.height, imBitmap!!.width, CvType.CV_8UC1)
            Utils.bitmapToMat(imBitmap, im)
            imSize = im.size()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pixelate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pixelater = Pixelater(this)
        pixelater.pixelateInBackground(im)
    }

    override fun pixelaterResponse(im: Mat) {
        Log.d(TAG, "pixelater responded")

        val resizeImg = Mat()
        Imgproc.resize(im, resizeImg, imSize)

        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(resizeImg.cols(), resizeImg.rows(), conf) // this creates a MUTABLE bitmap
        Utils.matToBitmap(resizeImg, bmp)

        requireActivity().runOnUiThread(Runnable {
            image_view_pixelate.setImageBitmap(bmp)
            pixelateProgressBar.visibility = View.GONE
            listener.imageReady(bmp)
        })
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
        fun newInstance(listener: ImageReadyListener, imBitmap: Bitmap) =
//        , pixelateType: Int) =
            PixelateFragment(listener).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
//                    putInt(ARG_PIXELATE_TYPE, pixelateType)
                }
            }
    }

}
