package com.nolly.pl3x.marker

import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.markers.marker.MultiPolygon
import net.pl3x.map.core.markers.marker.Polygon
import net.pl3x.map.core.markers.marker.Polyline
import net.pl3x.map.core.markers.option.Options

class MapMultiPolygon(
	key: String,
	val polygons: List<PolygonDef>,
	var options: Options? = null,
	var pane: String? = null,
) : MapMarker(key) {
	data class PolygonDef(val outer: List<Point>, val holes: List<List<Point>> = emptyList())

	override fun toPlx(): MultiPolygon {
		val multi = MultiPolygon(key)
		polygons.forEachIndexed { pi, def ->
			val outer = Polyline("${key}_p${pi}_outer", def.outer)
			val polygon = Polygon(key + "_p$pi", outer)
			def.holes.forEachIndexed { hi, hole ->
				polygon.addPolyline(Polyline("${key}_p${pi}_hole$hi", hole))
			}
			multi.addPolygon(polygon)
		}
		options?.let { multi.setOptions(it) }
		pane?.let { multi.setPane(it) }
		return multi
	}
}
