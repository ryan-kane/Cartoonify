package com.example.cartoonify.Pixelate

import org.opencv.core.Mat

class Pixelater(val listener: PixelaterResponseListener) {

    interface PixelaterResponseListener {
        fun pixelaterResponse(im: Mat)
    }

    fun pixelateKMeans(im: Mat){
        listener.pixelaterResponse(im)
    }


}