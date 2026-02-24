package com.nolly.pl3x.dsl

import com.nolly.pl3x.world.MapWorld
import net.pl3x.map.core.markers.Point

fun MapWorld.renderAroundSpawn(radiusBlocks: Int) {
	val spawnPoint = spawn
	render.radiusRender(spawnPoint.x, spawnPoint.z, radiusBlocks)
}

fun MapWorld.renderAround(blockX: Int, blockZ: Int, radiusBlocks: Int) {
	render.radiusRender(blockX, blockZ, radiusBlocks)
}

fun MapWorld.renderPlayerRegions() {
	val regions = players().map { it.position }.map { pos -> Point.of(pos.x shr 9, pos.z shr 9) }.distinct()
	render.regions(regions)
}
