package com.example.cartoonify.Vectorize

import android.util.Log
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

private const val Y_THRESH = 48.0 / 255.0
private const val U_THRESH = 7.0 / 255.0
private const val V_THRESH = 6.0 / 255.0

private const val TAG = "SimilarityGraph"

class SimilarityGraph {


//    Section 3.2 of this paper
//    https://johanneskopf.de/publications/pixelart/paper/pixel.pdf

    private var graph: Array<Array<Array<Boolean>>> = arrayOf<Array<Array<Boolean>>>()

    constructor(im: Mat){
        // this is expected to be extremely slow
        // run asynchronously

        for(y in 0 until im.rows()) {
            var nodes = arrayOf<Array<Boolean>>()
            for (x in 0 until im.cols()) {
                var node = arrayOf<Boolean>()
                // 8 edges are initialized to exist
                for(i in 0..8) {
                    node += true
                }
                nodes += node
            }
            graph += nodes
        }

        // remove the edges pointing out of the image from the graph
        for(y in 0 until im.rows()) {
            // left side
            graph[y][0][0] = false
            graph[y][0][3] = false
            graph[y][0][5] = false

            // right side
            graph[y][im.cols() - 1][2] = false
            graph[y][im.cols() - 1][4] = false
            graph[y][im.cols() - 1][7] = false
        }

        for(x in 0 until im.cols()) {
            // top
            graph[0][x][5] = false
            graph[0][x][6] = false
            graph[0][x][7] = false

            //bottom
            graph[im.rows() - 1][x][0] = false
            graph[im.rows() - 1][x][1] = false
            graph[im.rows() - 1][x][2] = false
        }

        // remove edges between nodes with sufficiently different colours
        // sufficiently different is shown in similarColor
        for(y in 0 until im.rows()) {
            for (x in 0 until im.cols()) {
                for(i in -1 until 1) {
                    if((i == -1 && y == 0) || (i == 1 && y == im.cols()-1)) continue
                    for (j in -1 until 1) {
                        if((j == -1 && x == 0) || (j == 1 && x == im.rows()-1)) continue
                        if(i == 0 && j == 0) continue // no edge to self
                        // compare edges
                        if(similar(im.get(y, x), im.get(y + i, x + j))) {
                            // edges are similar, do not disconnect
                            // handle diagonal cases
                            if((i != 0 && j != 0) &&
                                similar(im.get(y + i, x), im.get(y, x + j))) {
                                if(similar(im.get(y, x), im.get(y + i, x))) {
                                    // blue edge safe to remove
                                    disconnect(y, x, i, j)
                                } else {
                                    // red edge special case
                                    // TODO implement Heuristics
                                    if(!graph[y + i][x + j][getEdge(-i, -j)]) {
                                        disconnect(y, x, i, j)
                                    }else if (!graph[y + i][x][getEdge(-i, j)] || !graph[y][x + j][getEdge(i, -j)]){
                                        continue
                                    } else {
                                        disconnect(y, x, i, j)
                                    }
                                }
                            }
                        } else {
                            disconnect(y, x, i, j)
                        }
                    }
                }
            }
        }
    }

    private fun similar(lhs: DoubleArray, rhs: DoubleArray): Boolean {
        // compares YUV pixels
        return (lhs[0] == rhs[0]) &&
                (lhs[1] == rhs[1]) &&
                (lhs[2] == rhs[2]) &&
                (lhs[3] == rhs[3])

    }

    private fun neighborIsDiagonal(y: Int, x: Int): Boolean {
        return ((x != 0) && (y != 0))
    }

    private fun disconnect(y: Int, x: Int, i: Int, j: Int) {
        graph[y][x][getEdge(i, j)] = false
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

    private fun findPixelCorner(y: Int, x: Int, i: Int, j: Int): Corner {
        val pixel = graph[y][x]
        val cx = x + 0.5
        val cy = y + 0.5
        var result = Corner
        result.firstValid = false
        result.secondValid = false
        result.split = false
        val invalid = PointC(-1.0, -1.0)
        if(!pixel[getEdge(i, 0)] || !pixel[getEdge(0, j)]) {
            if((y + i >= 0 && y + i < graph.size) || (x + j >= 0 && x + j < graph[0].size)) {
                if (pixel[getEdge(i, j)]) {
                    val first = invalid
                    val second = invalid
                    if(!pixel[getEdge(i, 0)]) {
                        result.firstValid = true
                        result.first = PointC(cx + 0.25 * j, cy + 0.75 * i)
                    }
                    if(!pixel[getEdge(0, j)]) {
                        result.secondValid = true
                        result.second = PointC(cx + 0.75 * j, cy + 0.25 * i)
                    }
                    result.split = true
                    return result
                }
            }
            if(y + i >= 0 && y + i < graph.size) {
                if((!pixel[getEdge(i, 0)]) && (graph[y + i][x][getEdge(-i, j)])) {
                    result.firstValid = true
                    result.first = PointC(cx + 0.25 * j, cy + 0.25 * i)
                    return result
                }
            }
            if(x + j >= 0 && x + j < graph[0].size) {
                if((!pixel[getEdge(0, j)]) && graph[y][x + j][getEdge(i, -j)]) {
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

    fun extractDualGraph(): BorderGraph {
        val borders = BorderGraph()
        for(y in graph.indices) {
            for(x in graph[0].indices){
                // get corners
                var corners = arrayOf<Array<Corner>>()
                corners += arrayOf(
                    findPixelCorner(y, x, -1, -1),
                    findPixelCorner(y, x, -1, 1)
                )
                corners += arrayOf(
                    findPixelCorner(y, x, 1, -1),
                    findPixelCorner(y, x, 1, 1)
                )
                for (ci in 0..1) {
                    val i = (2 * ci) - 1
                    for(cj in 0..1) {
                        val j = (2 * cj) - 1
                        if(!(corners[ci][cj].split)!!) {
                            if(corners[ci][cj].firstValid!!) {
                                // link to adjacent
                                if(!graph[y][x][getEdge(i, 0)]){
                                    val ncj = if (cj == 1) 0 else 1
                                    if(corners[ci][ncj].split!!) {
                                        borders.addEdge(corners[ci][cj].first!!,
                                            corners[ci][ncj].first!!)
                                    }else if(corners[ci][cj].firstValid!!) {
                                        borders.addEdge(corners[ci][cj].first!!,
                                            corners[ci][ncj].first!!)
                                    }
                                }
                                if(graph[y][x][getEdge(0, j)]) {
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

}


