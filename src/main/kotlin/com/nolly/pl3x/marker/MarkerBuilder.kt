package com.nolly.pl3x.marker

import com.nolly.pl3x.Pl3xContext
import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.world.MapWorld
import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.markers.Vector
import net.pl3x.map.core.markers.option.Fill
import net.pl3x.map.core.markers.option.Options

class MarkerBuilder(private val key: String, private val type: Type) {
	enum class Type { REGION, POLYLINE, CIRCLE, ELLIPSE, RECTANGLE, ICON, MULTI_POLYGON, MULTI_POLYLINE }

	private var worldName: String? = null
	private var layerKey: String? = null
	private var layerLabel: String? = null
	private var points = mutableListOf<Point>()
	private var holes = mutableListOf<List<Point>>()
	private var polygonDefs = mutableListOf<MapMultiPolygon.PolygonDef>()
	private var multiLines = mutableListOf<List<Point>>()
	private var center: Point? = null
	private var radius: Double = 0.0
	private var radiusX: Double = 0.0
	private var radiusZ: Double = 0.0
	private var tilt: Double? = null
	private var point1: Point? = null
	private var point2: Point? = null
	private var iconKey: String? = null
	private var iconSize: Vector? = null
	private var iconAnchor: Vector? = null
	private var iconRotation: Double? = null
	private var iconRotationOrigin: String? = null
	private var markerPaneName: String? = null

	private var styleDsl: StyleDSL? = null
	private fun dsl(): StyleDSL = styleDsl ?: StyleDSL().also { styleDsl = it }

	fun world(name: String): MarkerBuilder {
		worldName = name; return this
	}

	fun layer(key: String, label: String = key): MarkerBuilder {
		layerKey = key; layerLabel = label; return this
	}

	fun point(x: Double, z: Double): MarkerBuilder {
		points += Point.of(x, z); return this
	}

	fun point(x: Int, z: Int) = point(x.toDouble(), z.toDouble())

	fun polygon(block: PointsBuilder.() -> Unit): MarkerBuilder {
		points = PointsBuilder().apply(block).build().toMutableList()
		return this
	}

	fun hole(block: PointsBuilder.() -> Unit): MarkerBuilder {
		holes += PointsBuilder().apply(block).build()
		return this
	}

	fun addPolygon(
		outer: PointsBuilder.() -> Unit,
		vararg holeBuilders: PointsBuilder.() -> Unit,
	): MarkerBuilder {
		val outerPts = PointsBuilder().apply(outer).build()
		val holePts = holeBuilders.map { PointsBuilder().apply(it).build() }
		polygonDefs += MapMultiPolygon.PolygonDef(outerPts, holePts)
		return this
	}

	fun addLine(block: PointsBuilder.() -> Unit): MarkerBuilder {
		multiLines += PointsBuilder().apply(block).build()
		return this
	}

	fun center(x: Double, z: Double): MarkerBuilder {
		center = Point.of(x, z); return this
	}

	fun center(x: Int, z: Int) = center(x.toDouble(), z.toDouble())

	fun radius(r: Double): MarkerBuilder {
		radius = r; return this
	}

	fun radius(x: Double, z: Double): MarkerBuilder {
		radiusX = x; radiusZ = z; return this
	}

	fun tilt(degrees: Double): MarkerBuilder {
		tilt = degrees; return this
	}

	fun corners(x1: Int, z1: Int, x2: Int, z2: Int) =
		corners(x1.toDouble(), z1.toDouble(), x2.toDouble(), z2.toDouble())

	fun corners(x1: Double, z1: Double, x2: Double, z2: Double): MarkerBuilder {
		point1 = Point.of(x1, z1); point2 = Point.of(x2, z2); return this
	}

	fun image(key: String): MarkerBuilder {
		iconKey = key; return this
	}

	fun size(px: Double): MarkerBuilder {
		iconSize = Vector.of(px, px); return this
	}

	fun size(w: Double, h: Double): MarkerBuilder {
		iconSize = Vector.of(w, h); return this
	}

	fun size(px: Int) = size(px.toDouble())
	fun anchor(x: Double, z: Double): MarkerBuilder {
		iconAnchor = Vector.of(x, z); return this
	}

	fun rotation(degrees: Double): MarkerBuilder {
		iconRotation = degrees; return this
	}

	fun rotationOrigin(origin: String): MarkerBuilder {
		iconRotationOrigin = origin; return this
	}

	fun pane(paneName: String): MarkerBuilder {
		markerPaneName = paneName; return this
	}

	fun options(opts: Options): MarkerBuilder {
		styleDsl = StyleDSL.from(opts)
		return this
	}

	fun style(block: StyleDSL.() -> Unit): MarkerBuilder {
		dsl().apply(block)
		return this
	}

	fun stroke(hex: String, weight: Int = 3): MarkerBuilder = style { stroke(hex, weight) }
	fun fill(hex: String, type: Fill.Type = Fill.Type.EVENODD): MarkerBuilder = style { fill(hex, type) }
	fun popup(content: String): MarkerBuilder = style { popup(content) }
	fun tooltip(content: String, sticky: Boolean = false): MarkerBuilder = style { tooltip(content, sticky) }

	private fun buildOptions(): Options? = styleDsl?.build()

	fun build(): MapMarker = when (type) {
		Type.REGION -> MapRegion(key, points.toList(), holes.toList(), buildOptions(), markerPaneName)
		Type.POLYLINE -> MapPolyline(key, points.toList(), buildOptions(), markerPaneName)
		Type.CIRCLE -> MapCircle(
			key, requireNotNull(center) { "Circle requires center" }, radius, buildOptions(), markerPaneName
		)

		Type.ELLIPSE -> MapEllipse(
			key,
			requireNotNull(center) { "Ellipse requires center" },
			radiusX,
			radiusZ,
			tilt,
			buildOptions(),
			markerPaneName
		)

		Type.RECTANGLE -> MapRectangle(
			key,
			requireNotNull(point1) { "Rectangle requires corners" },
			requireNotNull(point2) { "Rectangle requires corners" },
			buildOptions(),
			markerPaneName
		)

		Type.ICON -> MapIconMarker(
			key,
			requireNotNull(center) { "Icon requires center point (use center())" },
			requireNotNull(iconKey) { "Icon requires an image key (use image())" },
			iconSize, iconAnchor, iconRotation, iconRotationOrigin,
			buildOptions(), markerPaneName
		)

		Type.MULTI_POLYGON -> MapMultiPolygon(
			key,
			polygonDefs.toList()
				.also { require(it.isNotEmpty()) { "MultiPolygon requires at least one addPolygon()" } },
			buildOptions(), markerPaneName
		)

		Type.MULTI_POLYLINE -> MapMultiPolyline(
			key,
			multiLines.toList()
				.also { require(it.isNotEmpty()) { "MultiPolyline requires at least one addLine()" } },
			buildOptions(), markerPaneName
		)
	}

	fun register(): MapMarker {
		val wName = requireNotNull(worldName) { "MarkerBuilder: call .world(name) before .register()" }
		val lKey = requireNotNull(layerKey) { "MarkerBuilder: call .layer(key) before .register()" }
		val marker = build()
		val world: MapWorld = Pl3xContext.worlds.getOrCreate(wName)
		val layer: MapLayer = world.layer(lKey, layerLabel ?: lKey)
		layer.addMarker(marker)
		return marker
	}
}
