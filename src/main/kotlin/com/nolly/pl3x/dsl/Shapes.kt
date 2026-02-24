package com.nolly.pl3x.dsl

import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.marker.points

fun MarkerBuilder.line(x1: Int, z1: Int, x2: Int, z2: Int): MarkerBuilder {
	return run {
		polygon {
			point(x1, z1)
			point(x2, z2)
		}
	}
}

fun MarkerBuilder.box(centerX: Int, centerZ: Int, halfSize: Int): MarkerBuilder {
	val x1 = centerX - halfSize
	val z1 = centerZ - halfSize
	val x2 = centerX + halfSize
	val z2 = centerZ + halfSize
	return corners(x1, z1, x2, z2)
}

fun MarkerBuilder.ring(
	centerX: Int,
	centerZ: Int,
	outerRadius: Int,
	innerRadius: Int,
	segments: Int = 32,
): MarkerBuilder {
	require(segments >= 3) { "Ring requires at least 3 segments" }

	val outer = points {
		repeat(segments) { i ->
			val angle = 2.0 * Math.PI * i / segments
			val x = centerX + outerRadius * kotlin.math.cos(angle)
			val z = centerZ + outerRadius * kotlin.math.sin(angle)
			point(x, z)
		}
	}
	val inner = points {
		repeat(segments) { i ->
			val angle = 2.0 * Math.PI * i / segments
			val x = centerX + innerRadius * kotlin.math.cos(angle)
			val z = centerZ + innerRadius * kotlin.math.sin(angle)
			point(x, z)
		}
	}
	polygon { outer.forEach { point(it) } }
	hole { inner.forEach { point(it) } }
	return this
}
