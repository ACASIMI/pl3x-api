package com.nolly.pl3x.dsl

import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.marker.MapMarker
import com.nolly.pl3x.marker.MarkerBuilder

fun MapLayer.update(markerKey: String, type: MarkerBuilder.Type, block: MarkerBuilder.() -> Unit): MapMarker {
	removeMarker(markerKey)
	return MarkerBuilder(markerKey, type).world(worldName()).layer(key).apply(block).register()
}
