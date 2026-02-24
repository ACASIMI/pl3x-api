package com.nolly.pl3x.marker

import net.pl3x.map.core.markers.Point

class PointsBuilder {
	private val _points = mutableListOf<Point>()

	fun point(x: Double, z: Double) {
		_points += Point.of(x, z)
	}

	fun point(x: Int, z: Int) = point(x.toDouble(), z.toDouble())

	fun point(p: Point) {
		_points += p
	}

	fun build(): List<Point> = _points.toList()
}

fun points(block: PointsBuilder.() -> Unit): List<Point> = PointsBuilder().apply(block).build()
