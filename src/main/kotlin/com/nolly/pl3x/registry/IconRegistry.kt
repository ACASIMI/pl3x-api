package com.nolly.pl3x.registry

import net.pl3x.map.core.Pl3xMap
import net.pl3x.map.core.image.IconImage
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap

class IconRegistry {
	private val registeredKeys = ConcurrentHashMap.newKeySet<String>()

	fun register(key: String, image: BufferedImage, format: String = "png", overwrite: Boolean = false) {
		val registry = Pl3xMap.api().iconRegistry
		if (!overwrite) {
			require(!registry.has(key)) { "Icon '$key' is already registered" }
		}
		val iconImage = IconImage(key, image, format)
		registry.register(iconImage)
		registeredKeys += key
	}

	fun unregister(key: String) {
		Pl3xMap.api().iconRegistry.unregister(key)
		registeredKeys -= key
	}

	fun has(key: String): Boolean = Pl3xMap.api().iconRegistry.has(key)
	fun get(key: String): IconImage? = Pl3xMap.api().iconRegistry.get(key)
	fun registeredKeys(): Set<String> = registeredKeys.toSet()
	fun untrack(key: String) = registeredKeys.remove(key)
	fun clear() = registeredKeys.clear()
}
