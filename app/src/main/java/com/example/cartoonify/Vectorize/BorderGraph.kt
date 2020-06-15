package com.example.cartoonify.Vectorize

import android.graphics.PointF
import org.opencv.core.Point
import kotlin.math.sqrt

class BorderGraph {

    private val links = HashMap<PointC, ArrayList<PointC>>()
    private val singleLength = HashSet<Edge>()


    fun clearChainSegment(chain: ArrayList<PointC>) {
        for (i in 1 until chain.size) {
            singleLength.remove(Edge(chain[i], chain[i-1]))
        }

    }

    fun getChains(): ArrayList<ArrayList<PointC>> {
        val chains = ArrayList<ArrayList<PointC>>()
        val seen = HashSet<PointC>()

        for (point in links.keys) {
            if (seen.contains(point)) {
                if(links[point]?.size == 0) continue
                var current = point
                var previous = point
                var tmp: PointC? = null
                val chain = ArrayList<PointC>()
                while(true) {
                    if (current != previous && !(links[current]!!.contains(previous))) {
                        chain.clear()
                        chain.add(current)
                        seen.add(current)
                        tmp = current
                        current = previous
                        previous = tmp
                        while(links[current]!!.contains(previous)) {
                            chain.add(current)
                            seen.add(current)
                            if (links[current]!![0] == previous) {
                                previous = current
                                current = links[current]!![1]
                            } else {
                                previous = current
                                current = links[current]!![0]
                            }
                        }
                        chain.add(current)
                        chains.add(chain)
                        clearChainSegment(chain)
                    }
                    chain.add(current)
                    if( current == point && !seen.contains(point)) {
                        chains.add(chain)
                        clearChainSegment(chain)
                    }
                    seen.add(current)
                    if(links[current]!![0] == previous) {
                        previous = current
                        current = links[current]!![1]
                    } else {
                        previous = current
                        current = links[current]!![0]
                    }
                }
            }
        }
        for(edge in singleLength) {
            val chain = ArrayList<PointC>()
            chain.add(edge.first!!)
            chain.add(edge.second!!)
            chains.add(chain)
        }
        return chains
    }


    fun splitJunctions() {
        for (point in links.keys) {
            if(links[point]?.size == 4) {
                links[point]?.clear()
            } else if (links[point]?.size == 3) {
                val a = PointC(links[point]?.get(0)!!.x - point.x,
                    links[point]?.get(0)!!.y - point.y)
                val b = PointC(links[point]?.get(1)!!.x - point.x,
                    links[point]?.get(1)!!.y - point.y)
                val c = PointC(links[point]?.get(2)!!.x - point.x,
                    links[point]?.get(2)!!.y - point.y)
                val cos_ab = a.dot(b) / (sqrt((a.dot(a))) * sqrt(b.dot(b)))
                val cos_bc = b.dot(c) / (sqrt((b.dot(b))) * sqrt(c.dot(c)))
                val cos_ac = a.dot(c) / (sqrt((a.dot(a))) * sqrt(c.dot(c)))
                if (cos_ab < cos_bc && cos_ab < cos_ac) {
                        links[point]?.removeAt(2)
                }
                else if (cos_bc < cos_ac && cos_bc < cos_ab) {
                    links[point]?.removeAt(0)

                }
                else {
                    links[point]?.removeAt(1)
                }
            }
        }
    }


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