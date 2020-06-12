package com.example.cartoonify.Vectorize

import android.graphics.PointF

class BorderGraph {

    private val links = HashMap<PointC, ArrayList<PointC>>()
    private val singleLength = HashSet<Edge>()

    fun addPoint(point: PointC) {
        if(!links.containsKey(point)){
            val connections = ArrayList<PointC>()
            links[point] = connections
        }
    }

    fun addEdge(first: PointC, second: PointC) {
        addPoint(first)
        addPoint(second)
        singleLength.add(Edge(first, second))
        if(!links[first]?.contains(second)!!) {
            links[first]?.add(second)
        }
        if(!links[second]?.contains(first)!!) {
            links[second]?.add(first)
        }
    }

}