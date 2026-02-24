package com.nolly.pl3x.render

import net.pl3x.map.core.renderer.progress.Progress
import net.pl3x.map.core.world.World
import java.util.concurrent.atomic.AtomicLong

class RenderProgress(private val inner: Progress) {
	val world: World? get() = inner.world
	val totalRegions: Long get() = inner.totalRegions
	val processedRegions: AtomicLong get() = inner.processedRegions
	val totalChunks: Long get() = inner.totalChunks
	val processedChunks: AtomicLong get() = inner.processedChunks
	val percent: Float get() = inner.percent
	val cps: Double get() = inner.cps
	val eta: String get() = inner.eta

	override fun toString() =
		"RenderProgress(world=${world?.name ?: "unknown"}, regions=$processedRegions/$totalRegions, ${percent}%, eta=${eta}s)"
}
