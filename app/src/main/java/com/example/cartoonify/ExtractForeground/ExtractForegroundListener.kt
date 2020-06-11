package com.example.cartoonify.ExtractForeground

import android.graphics.Bitmap
import org.opencv.core.Mat

interface ExtractForegroundListener {
    fun markBackground(maskBitmap: Bitmap)
    fun foregroundExtracted(im: Mat)
}