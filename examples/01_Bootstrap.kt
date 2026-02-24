package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.Pl3xBootstrap
import org.bukkit.plugin.java.JavaPlugin

/**
 * EXAMPLE 01 - Bootstrap & Lifecycle
 *
 * This is always the first thing you set up.
 * Pl3xBootstrap manages the connection between your plugin and Pl3xMap.
 *
 * Rules:
 * - Call attach() in onEnable(), BEFORE accessing Pl3xAPI
 * - Call detach() in onDisable()
 * - Never call Pl3xAPI methods outside of an onEnabled callback or after readiness is confirmed
 * - Pl3xMap can enable/disable independently of your plugin (e.g. /pl3xmap reload)
 */
class MyPlugin : JavaPlugin() {
	override fun onEnable() {
		// Step 1 - attach hooks Pl3xBootstrap into Pl3xMap's event system.
		// This also registers the Pl3xEventBridge internally.
		Pl3xBootstrap.attach()

		// Step 2 - Register a callback that fires when Pl3xMap is fully ready.
		// This is the safe place to draw markers, register icons, etc.
		Pl3xBootstrap.onEnabled {
			logger.info("[MyPlugin] Pl3xMap is ready - initializing map features.")
			setupMapFeatures()
		}

		// Step 3 - Register a callback for when Pl3xMap shuts down or reloads.
		// Use this to clean up any state that depends on Pl3xMap being live.
		Pl3xBootstrap.onDisabled {
			logger.info("[MyPlugin] Pl3xMap went offline - map features suspended.")
		}

		// You can also check readiness at any time synchronously.
		// This is useful for commands or task schedulers that might run before Pl3xMap is ready.
		if (Pl3xBootstrap.isReady) {
			logger.info("[MyPlugin] Pl3xMap was already ready on enable.")
		}
	}

	override fun onDisable() {
		// Always detach on disable.
		// This unregisters all event hooks and clears all internal state (worlds, layers, markers, icons).
		Pl3xBootstrap.detach()
		logger.info("[MyPlugin] Detached from Pl3xMap.")
	}

	private fun setupMapFeatures() {
		// Safe to use Pl3xAPI here - called only inside onEnabled {}
		val worlds = Pl3xAPI.worlds()
		logger.info("[MyPlugin] Detected ${worlds.size} world(s) in Pl3xMap.")
	}
}
