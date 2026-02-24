package com.nolly.pl3x.marker

import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.markers.marker.MultiPolyline
import net.pl3x.map.core.markers.marker.Polyline
import net.pl3x.map.core.markers.option.Options

class MapMultiPolyline(
	key: String,
	val lines: List<List<Point>>,
	var options: Options? = null,
	var pane: String? = null,
) : MapMarker(key) {
	override fun toPlx(): MultiPolyline {
		val multi = MultiPolyline(key)
		lines.forEachIndexed { i, pts ->
			multi.addPolyline(Polyline("${key}_line$i", pts))
		}
		options?.let { multi.setOptions(it) }
		pane?.let { multi.setPane(it) }
		return multi
	}
}
