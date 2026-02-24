package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.renderAround
import com.nolly.pl3x.dsl.renderAroundSpawn
import com.nolly.pl3x.dsl.renderPlayerRegions
import com.nolly.pl3x.render.RenderProgress
import com.nolly.pl3x.world.MapWorld

/**
 * EXAMPLE 09 - Render Control
 *
 * RenderController lets you programmatically trigger map tile generation.
 * All render methods operate on a specific world via world.render.
 *
 * Region coordinates in Pl3xMap follow Minecraft region file convention:
 *   region (0, 0) = blocks (0..511, 0..511)
 *   region (-1, 0) = blocks (-512..-1, 0..511)
 *
 * DoubleCheckerController manages Pl3xMap's background consistency checker
 * that re-verifies rendered regions against the world data.
 */
fun renderExamples() {
	val world: MapWorld = Pl3xAPI.world("world")

	// =========================================================================
	// FULL RENDER - re-renders the entire world
	// This is expensive. Only trigger when absolutely necessary.
	// =========================================================================
	world.render.full()

	// =========================================================================
	// REGION RENDER - re-renders a specific Minecraft region file
	// regionX and regionZ are region coordinates, not block coordinates.
	// =========================================================================
	world.render.region(regionX = 0, regionZ = 0)
	world.render.region(regionX = -1, regionZ = 2)

	// =========================================================================
	// REGION AT BLOCK - re-renders the region that contains a given block
	// Converts block coords to region coords internally (blockX shr 9).
	// =========================================================================
	world.render.regionAtBlock(blockX = 100, blockZ = 200)
	world.render.regionAtBlock(blockX = -512, blockZ = 0)

	// =========================================================================
	// MULTI-REGION RENDER - re-renders a list of specific regions
	// Useful for targeted updates after terrain changes.
	// =========================================================================
	world.render.regions(
		listOf(
			net.pl3x.map.core.markers.Point.of(0, 0),
			net.pl3x.map.core.markers.Point.of(1, 0),
			net.pl3x.map.core.markers.Point.of(0, 1),
		)
	)

	// =========================================================================
	// RADIUS RENDER - re-renders all regions within a block radius of a point
	// =========================================================================
	world.render.radiusRender(centerX = 0, centerZ = 0, radiusBlocks = 1024)

	// =========================================================================
	// PAUSE / RESUME - temporarily halt and resume the render queue
	// =========================================================================
	world.render.pause()
	// ... do some world modifications ...
	world.render.resume()

	// =========================================================================
	// PROGRESS - inspect the current render state
	// =========================================================================
	val progress: RenderProgress = world.render.progress()

	println("Regions total:     ${progress.totalRegions}")
	println("Regions processed: ${progress.processedRegions}")
	println("Chunks total:      ${progress.totalChunks}")
	println("Chunks processed:  ${progress.processedChunks}")
	println("Progress:          ${progress.percent}%")
	println("Chunks/sec:        ${progress.cps}")
	println("ETA:               ${progress.eta}")   // formatted string, e.g. "2m 30s"

	// =========================================================================
	// QUEUED WORLDS - which worlds currently have a render queued or running
	// =========================================================================
	val queued: List<String> = world.render.queuedWorlds()
	println("Worlds in render queue: $queued")

	// =========================================================================
	// DSL RENDER HELPERS - extension functions on MapWorld
	// =========================================================================

	// Render around the world spawn point
	world.renderAroundSpawn(radiusBlocks = 512)

	// Render around an arbitrary block coordinate
	world.renderAround(blockX = 500, blockZ = -300, radiusBlocks = 256)

	// Render only the regions where players currently are
	// Uses player positions to find which regions need updating - efficient for active worlds
	world.renderPlayerRegions()

	// =========================================================================
	// DOUBLE CHECKER CONTROLLER
	// The double checker is a Pl3xMap background task that re-validates rendered
	// regions against the live world data. You can stop/restart it with a delay.
	// =========================================================================

	// Access via the world's render controller
	val doubleChecker = world.render.doubleChecker

	// Stop the double checker (e.g. during a mass world edit to avoid thrashing)
	doubleChecker.stop()

	// Restart it after your changes - delay in milliseconds before it activates
	doubleChecker.restart(delayMs = 300_000L)  // wait 5 minutes before checking
}
