package com.example.cartoonify.Vectorize

import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val TAG = "Vectorizer"

class Vectorizer(val listener: VectorizeImageResponseListener) {

    interface VectorizeImageResponseListener {
        fun imageVectorized(im: Mat)
    }

    fun vectorize(im: Mat) {
        // run in background
        Thread(Runnable {

            // similarity graph
            Log.d(TAG, "similarity graph")
            val similarityGraph = SimilarityGraph(im)

            // border graph (dual graph)
            Log.d(TAG, "extract dual graph")
            val borderGraph = similarityGraph.extractDualGraph()

            // set borders
            Log.d(TAG, "set borders")
            val borderIm = setBorders(im.size(), borderGraph.getChains(), 10)

            // approximate color map
            Log.d(TAG, "approximate colors")
            val colors = approximateColorMap(im, 10)
            // flood colors
            Log.d(TAG, "Flood colors")
            val out = floodColors(borderIm, colors)

            Log.d(TAG, "Done")
            listener.imageVectorized(out)
        }).start()



    }

    fun setBorders(size : Size, borders : ArrayList<ArrayList<PointC>>, scale : Int): Mat {
        val dst = Mat(
            (size.height*scale + 1).toInt(),
            (size.width*scale + 1).toInt(),
            CvType.CV_8UC4
        )
        val black = Scalar(0.0, 0.0, 0.0, 128.0)
        for(chain in borders) {
            for(i in 1 until chain.size) {
                Imgproc.line(
                    dst,
                    Point(chain[i - 1].x*scale, chain[i - 1].y * scale),
                    Point(chain[i].x*scale, chain[i].y * scale),
                    black
                )
            }

        }
        return dst
    }

    private fun approximateColorMap(src: Mat, scale: Int): HashMap<PointC, DoubleArray>{
        val dst = HashMap<PointC, DoubleArray>()
        for (i in 0 until src.rows()) {
            for (j in 0 until src.cols()){
                if (i != src.rows() && j != src.cols()) {
                    dst[PointC(
                        j * scale + (scale/2).toDouble(),
                        i * scale + (scale/2).toDouble()
                    )] = src.get(i, j)
                }
            }
        }
        return dst
    }

    private fun floodColors(src: Mat, colors: HashMap<PointC, DoubleArray>): Mat {
        val dst = floodFill(src, colors)
        for (y in 0 until src.rows()) {
            for (x in 0 until src.cols()) {
                if(dst.get(y, x)[3] == 128.0) {
                    if( x < 0 ) {
                        val color = src.get(y, x-1)
                        src.put(y, x, color[0], color[1], color[2], color[3])
                    } else if (y < 0) {
                        val color = src.get(y-1, x)
                        src.put(y, x, color[0], color[1], color[2], color[3])
                    }

                }
            }

        }
        return dst
    }

    private fun floodFill(src: Mat, colors: HashMap<PointC, DoubleArray>): Mat {
        for (point in colors.keys) {
            val color = colors[point]!!

            if (color[3] == 0.0) return src
            val points: Queue<PointC> = LinkedList()
            points.add(point)
            while (points.isNotEmpty()) {
                if (
                    points.element().y < 0 ||
                    points.element().y >= src.rows() ||
                    points.element().x >= src.cols() ||
                    points.element().x < 0 ||
                    src.get(points.element().y.toInt(), points.element().x.toInt())[3] != 0.0
                ) {
                    if (
                        points.peek() == point &&
                        !src.get(point.y.toInt(), point.x.toInt())!!.contentEquals(color)
                    ) {
                        Log.d(TAG, "Error, color issue")
                    }
                    if (
                        points.element().y >= 0 &&
                        points.element().y < src.rows() &&
                        points.element().x < src.cols() &&
                        points.element().x >= 0 &&
                        src.get(points.element().y.toInt(), points.element().x.toInt())[3] == 128.0
                    ) {
                        src.put(
                            points.element().y.toInt(),
                            points.element().x.toInt(),
                            color[0], color[1], color[2], color[3]
                        )
                    }
                    points.remove()
                    continue
                }
                src.put(
                    points.element().y.toInt(),
                    points.element().x.toInt(),
                    color[0], color[1], color[2], color[3]
                )
                points.add(PointC(points.element().x, points.element().y + 1))
                points.add(PointC(points.element().x, points.element().y - 1))
                points.add(PointC(points.element().x + 1, points.element().y))
                points.add(PointC(points.element().x - 1, points.element().y))
                points.remove()
            }
        }
        return src
    }


}