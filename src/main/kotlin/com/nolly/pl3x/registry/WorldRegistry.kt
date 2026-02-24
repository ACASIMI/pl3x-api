package com.nolly.pl3x.registry

import com.nolly.pl3x.world.MapWorld
import net.pl3x.map.core.Pl3xMap
import java.util.concurrent.ConcurrentHashMap

class WorldRegistry {
	private val worlds = ConcurrentHashMap<String, MapWorld>()

	fun getOrCreate(name: String): MapWorld {
		return worlds.getOrPut(name) {
			val pl3xWorld = Pl3xMap.api().worldRegistry.get(name) ?: error("World '$name' is not loaded in Pl3xMap")
			MapWorld(pl3xWorld)
		}
	}

	fun get(name: String): MapWorld? = worlds[name]
	fun all(): Map<String, MapWorld> = worlds.toMap()
	fun remove(name: String) = worlds.remove(name)
	fun clear() = worlds.clear()
}
