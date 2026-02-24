package com.nolly.pl3x.render

import net.pl3x.map.core.Pl3xMap
import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.world.World

class RenderController(private val world: World) {
	val doubleChecker: DoubleCheckerController by lazy { DoubleCheckerController() }

	fun full() {
		val regions = world.listRegions(true)
		if (regions.isEmpty()) return
		Pl3xMap.api().regionProcessor.addRegions(world, regions)
	}

	fun region(regionX: Int, regionZ: Int) {
		Pl3xMap.api().regionProcessor.addRegions(world, listOf(Point.of(regionX, regionZ)))
	}

	fun regionAtBlock(blockX: Int, blockZ: Int) = region(blockX shr 9, blockZ shr 9)

	fun regions(regions: Collection<Point>) {
		if (regions.isEmpty()) return
		Pl3xMap.api().regionProcessor.addRegions(world, regions)
	}

	fun radiusRender(centerX: Int, centerZ: Int, radius: Int) {
		val rX = centerX shr 9
		val rZ = centerZ shr 9
		val rR = radius shr 9
		val existing = world.listRegions(true)
		val filtered = existing.filter { it.x in (rX - rR)..(rX + rR) && it.z in (rZ - rR)..(rZ + rR) }
		if (filtered.isEmpty()) return
		Pl3xMap.api().regionProcessor.addRegions(world, filtered)
	}

	fun pause() {
		Pl3xMap.api().regionProcessor.isPaused = true
	}

	fun resume() {
		Pl3xMap.api().regionProcessor.isPaused = false
	}

	val isPaused: Boolean get() = Pl3xMap.api().regionProcessor.isPaused

	fun progress(): RenderProgress? {
		val p = Pl3xMap.api().regionProcessor.progress
		if (p.world?.name != world.name) return null
		return RenderProgress(p)
	}

	fun queuedWorlds(): Set<World> = Pl3xMap.api().regionProcessor.queuedWorlds
}
