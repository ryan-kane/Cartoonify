package com.example.cartoonify.Vectorize

import org.opencv.core.Point


object Edge {
    var first: PointC? = null
    var second: PointC? = null

    fun equals(other: Edge): Boolean {
        return (first == other.first && second == other.second) ||
                (second == other.first && first == other.second)
    }

    override fun hashCode(): Int {
        return first.hashCode().xor(second.hashCode())
    }

    operator fun invoke(first: PointC, second: PointC): Edge {
        this.first = first
        this.second = second
        return this
    }
}