package com.example.cartoonify.Vectorize

import org.opencv.core.Mat

class Vectorizer(listener: VectorizeImageResponseListener) {

    interface VectorizeImageResponseListener {
        fun imageVectorized(im: Mat)
    }

    fun vectorize(im: Mat) {
        // run in background
        // similarity graph
        // border graph (dual graph)
        // set borders
        // approximate color map
        // flood colors
    }



}