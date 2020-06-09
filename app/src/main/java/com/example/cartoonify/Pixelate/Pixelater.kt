package com.example.cartoonify.Pixelate

import android.os.AsyncTask
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.logging.Handler
import kotlin.math.floor
import kotlin.math.log


private const val TAG = "Pixelater"
private const val KSPLITS = 8

class Pixelater(val listener: PixelaterResponseListener) {

    interface PixelaterResponseListener {
        fun pixelaterResponse(im: Mat)
    }

    fun pixelateInBackground(im: Mat){
        Thread(Runnable {
            Log.d(TAG, "Started background pixelating")
            val result: Mat = pixelate(im, 10, 0.1)
            listener.pixelaterResponse(result)
        }).start()
    }

    fun pixelate(im: Mat, factor: Int, sigma: Double): Mat{

        val r = im.rows()
        val c = im.cols()

        // get contours
        Log.d(TAG, "get contours")
        val contourImg = im.clone()
        val edge = Mat.zeros(r, c, im.type())
        val s_contourImg = Mat.zeros(r, c, im.type())
        Log.d(TAG, "canny")
        Imgproc.Canny(im, edge, 100.0, 200.0)

        val contours = ArrayList<MatOfPoint>()
        Log.d(TAG, "find contours")
        Imgproc.findContours(edge, contours, Mat(), Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE)

        Log.d(TAG, "draw contours")
        for (i in 0 until contours.size) {
            Imgproc.drawContours(contourImg, contours, i, Scalar(0.0, 0.0, 0.0),
                1, 8, Mat(), 0, Point())
        }

        // smooth image
        Log.d(TAG, "smooth")
        val smoothed = Mat()
        val size = 7.0
        val ksize = Size(size, size)
        Imgproc.GaussianBlur(
            contourImg,
            smoothed,
            ksize,
            sigma,
            sigma,
            Core.BORDER_DEFAULT
        )

        // resize
        Log.d(TAG, "resize")
        val resizeImg = Mat()
        val resizeW = floor((c/factor).toDouble())
        val resizeH = floor((r/factor).toDouble())
        val resizeSize = Size(resizeW, resizeH)
        Imgproc.resize(smoothed, resizeImg, resizeSize)

        // restrict color palette
        Log.d(TAG, "kmeans")
        val resultImg = kmeansColorQuantization(resizeImg)

        return resultImg
    }

    fun kmeansColorQuantization(im: Mat): Mat {

        var data = Mat()
        im.convertTo(data, CvType.CV_32F)
        data = data.reshape(1, data.total().toInt())

        val labels = Mat()
        var centers = Mat()

        Core.kmeans(
            data,
            KSPLITS,
            labels,
            TermCriteria(TermCriteria.MAX_ITER, 10, 1.0),
            3,
            Core.KMEANS_PP_CENTERS,
            centers
        )

        Log.d(TAG, "Labels: ${labels.dump()}")
        Log.d(TAG, "Labels: ${labels[500, 0][0]}")

        data = data.reshape(4, data.rows())
        centers = centers.reshape(4, centers.rows())

        Log.d(TAG, "Data: ${data.size()}")
        Log.d(TAG, "Centers: ${centers.size()}")

        for (i in 0 until data.rows()) {
            val centerId = labels[i, 0][0].toInt()
            Log.d(TAG, "$centerId")
            val center = centers.get(centerId, 0)

            data.put(i, 0, center[0], center[1], center[2], center[3])
        }

        val out = data.reshape(4, im.rows())
        out.convertTo(out, CvType.CV_8U)
        return out

    }

}