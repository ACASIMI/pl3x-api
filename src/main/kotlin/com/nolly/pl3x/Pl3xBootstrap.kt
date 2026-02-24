package com.nolly.pl3x

import com.nolly.pl3x.event.Pl3xEventBridge
import net.pl3x.map.core.Pl3xMap
import net.pl3x.map.core.event.EventHandler
import net.pl3x.map.core.event.EventListener
import net.pl3x.map.core.event.RegisteredHandler
import net.pl3x.map.core.event.server.Pl3xMapDisabledEvent
import net.pl3x.map.core.event.server.Pl3xMapEnabledEvent

object Pl3xBootstrap {
	private var attached = false
	private val enabledListeners = mutableListOf<() -> Unit>()
	private val disabledListeners = mutableListOf<() -> Unit>()

	private val eventListener = object : EventListener {
		@EventHandler
		fun onEnabled(event: Pl3xMapEnabledEvent) {
			Pl3xContext.markReady()
			enabledListeners.forEach { it() }
		}

		@EventHandler
		fun onDisabled(event: Pl3xMapDisabledEvent) {
			Pl3xContext.markUnready()
			disabledListeners.forEach { it() }
		}
	}

	fun attach() {
		if (attached) return
		attached = true
		Pl3xMap.api().eventRegistry.register(eventListener)
		Pl3xEventBridge.register()
	}

	fun detach() {
		if (!attached) return
		attached = false
		Pl3xEventBridge.unregister()
		unregisterFromEvent(Pl3xMapEnabledEvent::class.java)
		unregisterFromEvent(Pl3xMapDisabledEvent::class.java)
		Pl3xContext.reset()
	}

	private fun unregisterFromEvent(eventClass: Class<*>) {
		try {
			val field = eventClass.getDeclaredField("handlers")
			field.isAccessible = true
			@Suppress("UNCHECKED_CAST")
			val handlers = field.get(null) as MutableList<RegisteredHandler>
			handlers.removeIf { it.listener === eventListener }
		} catch (_: Exception) {
		}
	}

	fun onEnabled(block: () -> Unit) {
		enabledListeners += block
	}

	fun onDisabled(block: () -> Unit) {
		disabledListeners += block
	}

	val isReady: Boolean get() = Pl3xContext.isReady
}
