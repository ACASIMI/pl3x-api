package com.nolly.pl3x.layer

import com.nolly.pl3x.Pl3xContext
import com.nolly.pl3x.marker.MapMarker
import net.pl3x.map.core.markers.layer.SimpleLayer
import net.pl3x.map.core.markers.marker.Marker
import net.pl3x.map.core.world.World

class MapLayer(val key: String, label: String, private val world: World) {
	internal fun worldName(): String = world.name

	val inner: SimpleLayer = object : SimpleLayer(key, { label }) {}

	var label: String = label
		set(value) {
			field = value
			inner.setLabel(value)
		}

	var updateInterval: Int = 15
		set(value) {
			field = value
			inner.setUpdateInterval(value)
		}

	var showControls: Boolean = true
		set(value) {
			field = value
			inner.setShowControls(value)
		}

	var defaultHidden: Boolean = false
		set(value) {
			field = value
			inner.setDefaultHidden(value)
		}

	var priority: Int = 99
		set(value) {
			field = value
			inner.setPriority(value)
		}

	var zIndex: Int = 99
		set(value) {
			field = value
			inner.setZIndex(value)
		}

	var pane: String? = null
		set(value) {
			field = value
			inner.setPane(value)
		}

	var css: String? = null
		set(value) {
			field = value
			inner.setCss(value)
		}

	var liveUpdate: Boolean = false
		set(value) {
			field = value
			inner.setLiveUpdate(value)
		}

	fun addMarker(marker: MapMarker) {
		inner.addMarker(marker.toPlx())
		Pl3xContext.markers.put(world.name, key, marker)
	}

	fun removeMarker(markerKey: String) {
		inner.removeMarker(markerKey)
		Pl3xContext.markers.remove(world.name, key, markerKey)
	}

	fun hasMarker(markerKey: String): Boolean = inner.hasMarker(markerKey)

	fun clearMarkers() {
		inner.clearMarkers()
		Pl3xContext.markers.clearLayer(world.name, key)
	}

	fun markers(): Map<String, Marker<*>> = inner.registeredMarkers()

	override fun toString() = "MapLayer(key=$key, world=${world.name})"
	override fun equals(other: Any?) = other is MapLayer && key == other.key && world.name == other.world.name
	override fun hashCode() = 31 * key.hashCode() + world.name.hashCode()
}

fun MapLayer.configure(block: MapLayer.() -> Unit): MapLayer = apply(block)
