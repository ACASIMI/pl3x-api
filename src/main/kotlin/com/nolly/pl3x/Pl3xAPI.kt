package com.nolly.pl3x

import com.nolly.pl3x.dsl.LayerScope
import com.nolly.pl3x.dsl.draw
import com.nolly.pl3x.event.Pl3xEventBridge
import com.nolly.pl3x.icon.IconBuilder
import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.player.MapPlayer
import com.nolly.pl3x.world.MapWorld
import net.pl3x.map.core.Pl3xMap
import java.util.*

object Pl3xAPI {
	val events: Pl3xEventBridge get() = Pl3xEventBridge

	fun world(name: String): MapWorld {
		Pl3xContext.requireReady()
		return Pl3xContext.worlds.getOrCreate(name)
	}

	fun worlds(): List<MapWorld> {
		Pl3xContext.requireReady()
		return Pl3xMap.api().worldRegistry.values().map { Pl3xContext.worlds.getOrCreate(it.name) }
	}

	fun players(): List<MapPlayer> {
		Pl3xContext.requireReady()
		return Pl3xMap.api().playerRegistry.values().map { MapPlayer(it) }
	}

	fun player(uuid: UUID): MapPlayer? {
		Pl3xContext.requireReady()
		return Pl3xMap.api().playerRegistry.get(uuid)?.let { MapPlayer(it) }
	}

	fun icon(key: String): IconBuilder {
		Pl3xContext.requireReady()
		return IconBuilder(key)
	}

	fun region(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.REGION)
	}

	fun circle(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.CIRCLE)
	}

	fun ellipse(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.ELLIPSE)
	}

	fun polyline(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.POLYLINE)
	}

	fun rectangle(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.RECTANGLE)
	}

	fun iconMarker(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.ICON)
	}

	fun multiPolygon(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.MULTI_POLYGON)
	}

	fun multiPolyline(key: String): MarkerBuilder {
		Pl3xContext.requireReady()
		return MarkerBuilder(key, MarkerBuilder.Type.MULTI_POLYLINE)
	}

	fun draw(worldName: String, layerKey: String, layerLabel: String = layerKey, block: LayerScope.() -> Unit) {
		Pl3xContext.requireReady()
		world(worldName).draw(layerKey, layerLabel, block)
	}

	fun layer(key: String): MapLayer = worlds().first().layer(key)
	fun layers(): List<MapLayer> = worlds().flatMap {
		listOfNotNull(
			it.layerOrNull(it.name + "_default") ?: it.layerOrNull(it.name) ?: it.layerOrNull("default")
			?: it.layerOrNull("main")
		)
	}

	fun raw(): Pl3xMap = Pl3xMap.api()
}
