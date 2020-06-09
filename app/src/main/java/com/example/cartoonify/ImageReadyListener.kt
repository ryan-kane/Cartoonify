package com.example.cartoonify

import android.graphics.Bitmap

interface ImageReadyListener {
    fun imageReady(imBitmap: Bitmap)
}