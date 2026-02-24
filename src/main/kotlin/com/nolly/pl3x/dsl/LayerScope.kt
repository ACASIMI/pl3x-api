package com.nolly.pl3x.dsl

import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.marker.MapMarker
import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.world.MapWorld

class LayerScope(val world: MapWorld, val layer: MapLayer) {
	fun circle(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.CIRCLE), block)

	fun ellipse(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.ELLIPSE), block)

	fun region(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.REGION), block)

	fun polyline(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.POLYLINE), block)

	fun rectangle(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.RECTANGLE), block)

	fun iconMarker(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.ICON), block)

	fun multiPolygon(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.MULTI_POLYGON), block)

	fun multiPolyline(key: String, block: MarkerBuilder.() -> Unit = {}): MapMarker =
		bound(MarkerBuilder(key, MarkerBuilder.Type.MULTI_POLYLINE), block)

	fun configure(block: MapLayer.() -> Unit) = layer.block()

	fun remove(markerKey: String) = layer.removeMarker(markerKey)

	fun replace(markerKey: String, block: LayerScope.() -> MapMarker) {
		layer.removeMarker(markerKey)
		block()
	}

	fun clear() = layer.clearMarkers()

	private fun bound(builder: MarkerBuilder, block: MarkerBuilder.() -> Unit): MapMarker {
		builder.world(world.name).layer(layer.key, layer.label)
		builder.block()
		return builder.register()
	}
}

fun MapWorld.draw(layerKey: String, layerLabel: String = layerKey, block: LayerScope.() -> Unit) {
	val l = layer(layerKey, layerLabel)
	LayerScope(this, l).block()
}
