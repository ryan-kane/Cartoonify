package com.example.cartoonify.Vectorize

import android.graphics.Point
import android.util.Log
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

private const val Y_THRESH = 48.0 / 255.0
private const val U_THRESH = 7.0 / 255.0
private const val V_THRESH = 6.0 / 255.0

private const val TAG = "SimilarityGraph"

class SimilarityGraph {


//    Section 3.2 of this paper
//    https://johanneskopf.de/publications/pixelart/paper/pixel.pdf

    private var graph = Graph<PointC>()
    private var cols: Int? = null
    private var rows: Int? = null

    constructor(im: Mat){
        // this is expected to be extremely slow
        // run asynchronously and on very small images

        // add edges between all nodes
        var pointA: PointC? = null
        var pointB: PointC? = null
        rows = im.rows()
        cols = im.cols()

        for(y in 0 until rows!!) {
            for (x in 0 until cols!!) {
                // 8 edges are initialized to exist
                pointA = PointC(x,  y)
                for(i in arrayOf(-1, 0, 1)) {
                    for (j in arrayOf(-1, 0, 1)) {
                        // no edges pointing out of the image
                        if(
                            y + i >= 0 // off the top
                            && y + i < im.rows() // off the bottom
                            && x + j >= 0 // off the left
                            && x + j < im.cols() // off the right
                            && (j != 0 || i != 0) // no edge to self
                        ) {
                            if(j == 0 || i == 0){
                                Log.d(TAG, "$x, $y")
                            }
                            pointB = PointC(x + j, y + i)
                            graph.addEdge(pointA, pointB)
                        }
                    }
                }
            }
        }

        Log.d(TAG, "$rows")
        Log.d(TAG, "$cols")
        // remove edges between nodes with "sufficiently different" colours
        // sufficiently different is shown in similarColor
        for(y in 0 until im.rows()) {
            for (x in 0 until im.cols()) {
                pointA = PointC(x, y)
                for(i in arrayOf(-1, 0, 1)) {
                    if((i == -1 && y == 0) || (i == 1 && y == im.rows()-1)) continue
                    for (j in arrayOf(-1, 0, 1)) {
                        if((j == -1 && x == 0) || (j == 1 && x == im.cols()-1)) continue
                        if(i == 0 && j == 0) continue // no edge to self
                        // compare edges
                        pointB = PointC(x + j, y + i)
                        if(similar(im.get(y, x), im.get(y + i, x + j))) {
                            // edges are similar, do not remove edge
                            // handle diagonal cases
                            if(i != 0 && j != 0 && similar(im.get(y + i, x), im.get(y, x + j))) {
                                if(similar(im.get(y, x), im.get(y + i, x))) {
                                    // blue edge safe to remove
                                    graph.removeEdge(pointA, pointB)
                                } else {
                                    // red edge special case
                                    // TODO implement Heuristics
                                    if(
                                        graph.containsEdge(pointA, PointC(x, y + i))
                                        && graph.containsEdge(pointA, PointC(x + j, y))
                                    ) {
                                        graph.removeEdge(pointA, pointB)
                                    }
                                }
                            }
                        } else {
                            graph.removeEdge(pointA, pointB)
                        }
                    }
                }
            }
        }
    }

    private fun similar(lhs: DoubleArray, rhs: DoubleArray): Boolean {
        val r = (lhs[0] == rhs[0]) &&
                (lhs[1] == rhs[1])
                && (lhs[2] == rhs[2])
//                && (lhs[3] == rhs[3])

        if(r){
            Log.d(TAG, "Similar")
        }
        return r
    }

    private fun neighborIsDiagonal(y: Int, x: Int): Boolean {
        return ((x != 0) && (y != 0))
    }

    private fun getEdge(i: Int, j: Int): Int {
        when(i) {
            -1 -> {
                return when(j) {
                    -1 -> 0
                    0 -> 1
                    1 -> 2
                    else -> 0
                }
            }
            0 -> {
                return when(j) {
                    -1 -> 3
                    1 -> 4
                    else -> 0
                }
            }
            1 -> {
                return when(j) {
                    -1 -> 5
                    0 -> 6
                    1 -> 7
                    else -> 0
                }
            }
            else -> return 0
        }
    }

    private fun findPixelCorner(pointA: PointC, i: Int, j: Int): Corner {
        val pointB = PointC(pointA.x + j, pointA.y + i)
        val cx = pointA.x + 0.5
        val cy = pointA.y + 0.5
        var result = Corner
        result.firstValid = false
        result.secondValid = false
        result.split = false
        val invalid = PointC(-1.0, -1.0)
        if(
            !graph.containsEdge(pointA, PointC(pointA.x, pointA.y + i))
            || !graph.containsEdge(pointA, PointC(pointA.x + j, pointA.y))
        ) {
            if((pointA.y + i >= 0 && pointA.y + i < rows!!) || (pointA.x + j >= 0 && pointA.x + j < cols!!)) {
                if(graph.containsEdge(pointA, pointB)) {
                    val first = invalid
                    val second = invalid
                    if(!graph.containsEdge(pointA, PointC(pointA.x, pointB.y))) {
                        result.firstValid = true
                        result.first = PointC(cx + 0.25 * j, cy + 0.75 * i)
                    }
                    if(!graph.containsEdge(pointA, PointC(pointB.x, pointA.y))) {
                        result.secondValid = true
                        result.second = PointC(cx + 0.75 * j, cy + 0.25 * i)
                    }
                    result.split = true
                    return result
                }
            }
            if(pointA.y + i >= 0 && pointA.y + i < rows!!) {
                if(
                    !graph.containsEdge(pointA, PointC(pointB.x, pointA.x))
                    && graph.containsEdge(pointA, PointC(pointA.x, pointB.y))
                ) {
                    result.firstValid = true
                    result.first = PointC(cx + 0.25 * j, cy + 0.25 * i)
                    return result
                }
            }
            if(pointA.x + j >= 0 && pointA.x + j < cols!!) {
                if(
                    !graph.containsEdge(pointA, PointC(pointB.x, pointA.y))
                    && graph.containsEdge(PointC(pointB.x, pointA.y), pointA)
                ) {
                    result.firstValid = true
                    result.first = PointC(cx + 0.25 * j, cy + 0.25 * i)
                    return result
                }
            }
            result.first = PointC(cx + 0.5 * j, cy + 0.5 * i)
            result.firstValid = true
            return result
        }
        return result
    }

    fun similarityMat(im: Mat): Mat {
        val out = Mat(rows!! * 10, cols!! * 10, CvType.CV_8UC4, Scalar(255.0, 255.0, 255.0, 255.0))
        for (y in 0 until rows!!) {
            for(x in 0 until cols!!) {
                val pointA = PointC(x, y)
                val color = Scalar(im[y, x])
                for(i in arrayOf(-1, 0, 1)) {
                    for(j in arrayOf(-1, 0, 1)) {
                        if( i == 0 && j == 0) continue
                        val pointB = PointC(x + i, y + j)
                        if(graph.containsEdge(pointA, pointB)) {
                            Imgproc.line(
                                out,
                                PointC((pointA.x+1) * 10,(pointA.y+1) * 10),
                                PointC((pointB.x+1) * 10,(pointB.y+1) * 10),
                                color,
                                2
                            )
                        }
                    }
                }
            }
        }
        return out
    }

    fun extractDualGraph(): BorderGraph {
        val borders = BorderGraph()
        for(point in graph.adjacencyMap.keys) {
            // get corners
            var corners = arrayOf<Array<Corner>>()
            corners += arrayOf(
                findPixelCorner(point, -1, -1),
                findPixelCorner(point, -1, 1),
                findPixelCorner(point, 1, -1),
                findPixelCorner(point, 1, 1)
            )
            for (ci in 0..1) {
                val i = (2 * ci) - 1
                for(cj in 0..1) {
                    val j = (2 * cj) - 1
                    if(!(corners[ci][cj].split)!!) {
                        if(corners[ci][cj].firstValid!!) {
                            // link to adjacent
                            if(!graph.containsEdge(point, PointC(point.x, point.y + i))) {
                                val ncj = if (cj == 1) 0 else 1
                                if(corners[ci][ncj].split!!) {
                                    borders.addEdge(
                                        corners[ci][cj].first!!,
                                        corners[ci][ncj].first!!
                                    )
                                }else if(corners[ci][cj].firstValid!!) {
                                    borders.addEdge(
                                        corners[ci][cj].first!!,
                                        corners[ci][ncj].first!!
                                    )
                                }
                            }
                            if(graph.containsEdge(point, PointC(point.x + j, point.y))){
                                val nci = if (ci == 1) 0 else 1
                                if (corners[nci][cj].split!!) {
                                    borders.addEdge(corners[ci][cj].first!!,
                                        corners[nci][cj].second!!)
                                } else if (corners[nci][cj].firstValid!!) {
                                    borders.addEdge(corners[ci][cj].first!!,
                                        corners[nci][cj].first!!)
                                }
                            }
                        }

                    }
                }
            }
        }
        return borders
    }

    object Corner{
        var firstValid : Boolean? = null
        var secondValid : Boolean? = null
        var split : Boolean? = null
        var first: PointC? = null
        var second: PointC? = null

    }

    private inner class Graph<T> {
        val adjacencyMap = HashMap<T, HashSet<T>>()

        fun addEdge(nodeA: T, nodeB: T) {
            adjacencyMap
                .computeIfAbsent(nodeA) {HashSet()}
                .add(nodeB)

            adjacencyMap
                .computeIfAbsent(nodeB) {HashSet()}
                .add(nodeA)
        }

        fun removeEdge(nodeA: T, nodeB: T) {
            adjacencyMap[nodeA]?.remove(nodeB)
            adjacencyMap[nodeB]?.remove(nodeA)
        }

        fun containsEdge(nodeA: T, nodeB: T): Boolean {
            return adjacencyMap.containsKey(nodeA) && adjacencyMap[nodeA]!!.contains(nodeB)
                    && adjacencyMap.containsKey(nodeB) && adjacencyMap[nodeB]!!.contains(nodeA)
        }
    }

}


