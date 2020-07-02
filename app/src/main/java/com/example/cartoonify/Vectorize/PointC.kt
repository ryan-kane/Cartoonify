package com.example.cartoonify.Vectorize

import org.opencv.core.Point

class PointC : Point {

    constructor(x: Int, y: Int): super(x.toDouble(), y.toDouble())
    constructor(x: Double, y: Double): super(x, y)

    fun compareTo(other: PointC): Int {
        return if(this.y == other.y && this.x == other.x) {
            0
        } else if ((this.y < other.y) || (this.y == other.y && this.x < other.x)) {
            -1
        } else {
            1
        }
    }

    override fun hashCode(): Int {
        return x.hashCode().xor(y.hashCode())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }
}