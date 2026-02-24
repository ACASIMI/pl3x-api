package com.nolly.pl3x.dsl

import com.nolly.pl3x.layer.MapLayer

class LayerPreset(private val block: MapLayer.() -> Unit) {
	fun applyTo(layer: MapLayer) {
		layer.block()
	}
}

fun MapLayer.applyPreset(preset: LayerPreset): MapLayer {
	preset.applyTo(this)
	return this
}
