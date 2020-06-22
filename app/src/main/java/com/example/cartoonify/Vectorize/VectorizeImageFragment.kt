package com.example.cartoonify.Vectorize

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cartoonify.ImageReadyListener
import com.example.cartoonify.PipelineFragment
import com.example.cartoonify.R
import kotlinx.android.synthetic.main.fragment_extract_foreground.*
import kotlinx.android.synthetic.main.fragment_vectorize_image.*
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_IMBITMAP = "imBitmap"

/**
 * A simple [Fragment] subclass.
 * Use the [VectorizeImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VectorizeImageFragment(listener: ImageReadyListener) :
    PipelineFragment(listener), Vectorizer.VectorizeImageResponseListener {
    private var imBitmap: Bitmap? = null
    private var im: Mat? = null
    private var imSize: Size? = null

    private lateinit var vectorizer: Vectorizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imBitmap = it.getParcelable(ARG_IMBITMAP)
        }
        if(imBitmap == null) {
            Toast.makeText(requireContext(), "No Image", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }else{
            im = Mat(imBitmap!!.height, imBitmap!!.width, CvType.CV_8U)
            Utils.bitmapToMat(imBitmap, im)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vectorize_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vectorizer = Vectorizer(this)
        vectorizer.vectorize(im!!)
    }

    override fun imageVectorized(imV: Mat) {
        // convert to bitmap
        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(imV.cols(), imV.rows(), conf) // this creates a MUTABLE bitmap
        Utils.matToBitmap(imV, bmp)
        requireActivity().runOnUiThread(Runnable{
            vectorize_image_view.setImageBitmap(bmp)
            vectorize_progress_bar.visibility = View.GONE
            listener.imageReady(bmp)
        })
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