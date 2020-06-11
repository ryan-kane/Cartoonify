package com.example.cartoonify.ExtractForeground

import android.graphics.Bitmap
import android.os.strictmode.ImplicitDirectBootViolation
import android.util.Log
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.concurrent.TimeUnit

private const val TAG = "ForegroundExtractor"

class ForegroundExtractor(private val listener: ExtractForegroundListener) {

    private var mask: Mat? = null

    fun extractForeground(im: Mat, newMask: Mat) {
        // expected to be very slow, do in background
        Thread(Runnable {
            Log.d(TAG, "mask: ${newMask[0, 0][0]}, ${newMask[0, 0][1]}, ${newMask[0, 0][2]}, ${newMask[0, 0][3]}")
            Log.d(TAG, "im: ${im[0, 0][0]}, ${im[0, 0][1]}, ${im[0, 0][2]}, ${im[0, 0][3]}")
            extractMask(newMask)
            Imgproc.cvtColor(im,im , Imgproc.COLOR_RGBA2RGB)

            val rect = Rect(10, 10, im.cols() - 20, im.rows() - 20)
            val bgdModel = Mat()
            val fgdModel = Mat()

            Log.d(TAG, "${im.size()}")
            Log.d(TAG, "${mask!!.size()}")

            Imgproc.grabCut(
                im,
                mask,
                rect,
                fgdModel,
                bgdModel,
                1,
                Imgproc.GC_INIT_WITH_MASK
            )
            val foreground = Mat(im.size(), CvType.CV_8UC1)
            val background = Mat(im.size(), CvType.CV_8UC1)
            Imgproc.cvtColor(im, im, Imgproc.COLOR_RGB2RGBA)
            Imgproc.threshold(mask, background, 2.0, 4.0, Imgproc.THRESH_TOZERO)
            im.copyTo(foreground, background)
            listener.foregroundExtracted(foreground)
        }).start()
    }


    fun convertMask(mask: Mat): Mat {
//        val cMask = Mat(mask.size(), CvType.CV_8U)
//        val buffer = ByteArray(3)
        return mask
    }

    fun extractMask(newMask: Mat) {
        if (mask == null) {
            mask = Mat(newMask.size(), CvType.CV_8U)
            mask!!.setTo(Scalar(Imgproc.GC_PR_FGD.toDouble()))
        }

        val buffer = ByteArray(3)
        for(r in 0 until newMask.rows()){
            for(c in 0 until newMask.cols()) {
                if(newMask[r, c][3] == 255.0) {
                    buffer[0] = Imgproc.GC_BGD.toByte()
                    mask!!.put(r, c, buffer)
                }
//                else {
//                    buffer[0] = Imgproc.GC_PR_FGD.toByte()
//                    mask!!.put(r, c, buffer)
//                }
            }
        }
    }
}