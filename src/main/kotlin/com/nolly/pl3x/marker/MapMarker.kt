package com.nolly.pl3x.marker

import net.pl3x.map.core.markers.marker.Marker

sealed class MapMarker(val key: String) {
	abstract fun toPlx(): Marker<*>
	override fun toString() = "${this::class.simpleName}(key=$key)"
	override fun equals(other: Any?) = other is MapMarker && key == other.key
	override fun hashCode() = key.hashCode()
}
