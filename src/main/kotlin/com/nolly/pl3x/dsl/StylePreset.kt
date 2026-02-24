package com.nolly.pl3x.dsl

import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.marker.StyleDSL
import net.pl3x.map.core.markers.option.Options

class StylePreset(private val block: StyleDSL.() -> Unit) {
	fun build(): Options = StyleDSL().apply(block).build()
}

fun MarkerBuilder.applyPreset(preset: StylePreset): MarkerBuilder = options(preset.build())
