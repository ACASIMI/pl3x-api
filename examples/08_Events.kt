package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.Pl3xBootstrap
import com.nolly.pl3x.dsl.draw
import net.pl3x.map.core.world.World

/**
 * EXAMPLE 08 - Events
 *
 * The library exposes two event surfaces:
 *
 * 1. Pl3xBootstrap - lifecycle hooks for your plugin's own enable/disable reactions
 * 2. Pl3xAPI.events (Pl3xEventBridge) - fine-grained Pl3xMap event subscriptions
 *
 * Listener lists are CopyOnWriteArrayList - safe to subscribe from any thread.
 * On detach(), all listeners are automatically cleared.
 */
fun eventExamples() {
	// =========================================================================
	// BOOTSTRAP LIFECYCLE HOOKS
	// These fire once per Pl3xMap enable/disable cycle.
	// You can register multiple callbacks - all will be called in order.
	// =========================================================================

	// Fires when Pl3xMap becomes ready (after Pl3xMapEnabledEvent)
	Pl3xBootstrap.onEnabled {
		println("[Events] Pl3xMap enabled - safe to draw markers now.")
		// Typically used to do your initial map setup
		initialMapSetup()
	}

	// Fires when Pl3xMap shuts down or reloads (after Pl3xMapDisabledEvent)
	Pl3xBootstrap.onDisabled {
		println("[Events] Pl3xMap disabled - suspending map features.")
	}

	// =========================================================================
	// EVENT BRIDGE - Pl3xAPI.events
	// Fine-grained event hooks. These are separate from Bootstrap hooks and
	// allow multiple independent systems to react to the same events.
	// =========================================================================

	// Fires when a world is loaded into Pl3xMap
	Pl3xAPI.events.onWorldLoad { world: World ->
		println("[Events] World loaded: ${world.name}")

		// Common pattern: set up default layers for a newly loaded world
		Pl3xAPI.draw(world.name, "world_markers", "World Markers") {
			configure {
				updateInterval = 20
				showControls = true
			}
			// Initial markers for this world...
		}
	}

	// Fires when a world is unloaded from Pl3xMap
	Pl3xAPI.events.onWorldUnload { world: World ->
		println("[Events] World unloaded: ${world.name}")
		// Clean up any resources you associated with this world
	}

	// Fires when Pl3xMap becomes enabled (same trigger as Bootstrap.onEnabled,
	// but goes through the EventBridge - useful for decoupled subsystems)
	Pl3xAPI.events.onMapEnabled {
		println("[Events] Map enabled via EventBridge.")
	}

	// Fires when Pl3xMap becomes disabled
	Pl3xAPI.events.onMapDisabled {
		println("[Events] Map disabled via EventBridge.")
	}

	// Fires when the Minecraft server finishes loading (ServerLoadedEvent)
	// Useful for initializing things that require all worlds to be up
	Pl3xAPI.events.onServerLoaded {
		println("[Events] Server fully loaded.")
		val worlds = Pl3xAPI.worlds()
		println("[Events] Total worlds in Pl3xMap: ${worlds.size}")
	}

	// =========================================================================
	// CLEARING LISTENERS MANUALLY (rarely needed - detach() does this)
	// =========================================================================
	// Pl3xAPI.events.clearAll()  // wipes ALL registered event bridge listeners
}

private fun initialMapSetup() {
	Pl3xAPI.worlds().forEach { world ->
		Pl3xAPI.draw(world.name, "default_layer", "Default") {
			configure {
				updateInterval = 30
				showControls = true
				defaultHidden = false
			}
		}
	}
}
