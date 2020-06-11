package com.example.cartoonify

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_display_image.*
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_IMBITMAP = "imBitmap"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayImageFragment(val imageReadyListener: ImageReadyListener) : Fragment() {
    private var imBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
        }
        // TODO resize bitmap for now
        val im = Mat(imBitmap!!.width, imBitmap!!.height, CvType.CV_8UC1)
        Utils.bitmapToMat(imBitmap!!, im)
        Imgproc.resize(im, im, Size(720.0, 1080.0))
        val newImBitmap = Bitmap.createBitmap(im.cols(), im.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(im, newImBitmap)
        imBitmap = newImBitmap

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_view_display_image.setImageBitmap(imBitmap)
        imageReadyListener.imageReady(imBitmap!!)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imBitmap Image Bitmap.
         * @return A new instance of fragment DisplayImageFragment.
         */
        @JvmStatic
        fun newInstance(listener: ImageReadyListener, imBitmap: Bitmap) =
            DisplayImageFragment(listener).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMBITMAP, imBitmap)
                }
            }
    }
}