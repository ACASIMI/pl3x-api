package com.nolly.pl3x.registry

import com.nolly.pl3x.marker.MapMarker
import java.util.concurrent.ConcurrentHashMap

class MarkerRegistry {
	private val markers = ConcurrentHashMap<String, MapMarker>()
	private fun compositeKey(worldName: String, layerKey: String, markerKey: String) = "$worldName:$layerKey:$markerKey"

	fun put(worldName: String, layerKey: String, marker: MapMarker) {
		markers[compositeKey(worldName, layerKey, marker.key)] = marker
	}

	fun get(worldName: String, layerKey: String, markerKey: String): MapMarker? =
		markers[compositeKey(worldName, layerKey, markerKey)]

	fun has(worldName: String, layerKey: String, markerKey: String): Boolean =
		markers.containsKey(compositeKey(worldName, layerKey, markerKey))

	fun remove(worldName: String, layerKey: String, markerKey: String): MapMarker? =
		markers.remove(compositeKey(worldName, layerKey, markerKey))

	fun clearLayer(worldName: String, layerKey: String) {
		markers.keys
			.filter { it.startsWith("$worldName:$layerKey:") }
			.forEach { markers.remove(it) }
	}

	fun clearWorld(worldName: String) {
		markers.keys.filter { it.startsWith("$worldName:") }.forEach { markers.remove(it) }
	}

	fun all(): Map<String, MapMarker> = markers.toMap()
	fun clear() = markers.clear()
}
