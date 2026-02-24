package com.nolly.pl3x

import com.nolly.pl3x.registry.IconRegistry
import com.nolly.pl3x.registry.LayerRegistry
import com.nolly.pl3x.registry.MarkerRegistry
import com.nolly.pl3x.registry.WorldRegistry
import java.util.concurrent.atomic.AtomicBoolean

internal object Pl3xContext {
	private val _ready = AtomicBoolean(false)
	val isReady: Boolean get() = _ready.get()
	val worlds = WorldRegistry()
	val layers = LayerRegistry()
	val markers = MarkerRegistry()
	val icons = IconRegistry()
	fun markReady() = _ready.set(true)
	fun markUnready() = _ready.set(false)

	fun reset() {
		_ready.set(false)
		worlds.clear()
		layers.clear()
		markers.clear()
		icons.clear()
	}

	fun requireReady() {
		check(isReady) { "Pl3xMap is not ready yet - wait for Pl3xMapEnabledEvent" }
	}
}
