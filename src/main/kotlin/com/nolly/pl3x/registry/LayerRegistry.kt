package com.nolly.pl3x.registry

import com.nolly.pl3x.layer.MapLayer
import java.util.concurrent.ConcurrentHashMap

class LayerRegistry {
	private val layers = ConcurrentHashMap<String, MapLayer>()
	private fun compositeKey(worldName: String, layerKey: String) = "$worldName:$layerKey"

	fun register(worldName: String, layer: MapLayer) {
		val key = compositeKey(worldName, layer.key)
		require(!layers.containsKey(key)) {
			"Layer '${layer.key}' already registered for world '$worldName'"
		}
		layers[key] = layer
	}

	fun getOrCreate(worldName: String, layerKey: String, factory: () -> MapLayer): MapLayer {
		return layers.getOrPut(compositeKey(worldName, layerKey), factory)
	}

	fun clearWorld(worldName: String) {
		layers.keys.filter { it.startsWith("$worldName:") }.forEach { layers.remove(it) }
	}

	fun get(worldName: String, layerKey: String): MapLayer? = layers[compositeKey(worldName, layerKey)]
	fun remove(worldName: String, layerKey: String): MapLayer? = layers.remove(compositeKey(worldName, layerKey))
	fun all(): Map<String, MapLayer> = layers.toMap()
	fun clear() = layers.clear()
}
