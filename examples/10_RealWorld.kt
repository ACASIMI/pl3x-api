package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.Pl3xBootstrap
import com.nolly.pl3x.dsl.*
import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.world.MapWorld
import java.io.File

/**
 * EXAMPLE 10 - Real-World Usage Patterns
 *
 * This file demonstrates practical, production-style usage combining
 * multiple parts of the API together in realistic scenarios.
 *
 * Scenarios:
 *   A. Faction territory map with borders and capitals
 *   B. Dynamic player tracker refreshed on a timer
 *   C. Event zone that activates and deactivates
 *   D. World-agnostic default layer setup on world load
 *   E. Icon-based POI system driven by a data list
 */

// =============================================================================
// SCENARIO A - Faction Territories
//
// Draw faction borders and capital markers from a data list.
// Re-draws are handled by clearing the layer first.
// =============================================================================

data class Faction(
	val id: String,
	val name: String,
	val color: String,              // hex, e.g. "#FF4400"
	val borderPoints: List<Pair<Int, Int>>,
	val capitalX: Int,
	val capitalZ: Int,
)

fun drawFactions(worldName: String, factions: List<Faction>) {
	val world = Pl3xAPI.world(worldName)
	val layer = world.layer("factions", "Factions")

	// Wipe the layer before redrawing to avoid stale markers
	layer.clearMarkers()

	Pl3xAPI.draw(worldName, "factions", "Factions") {
		factions.forEach { faction ->

			// Territory border
			region("faction_border_${faction.id}") {
				polygon {
					faction.borderPoints.forEach { (x, z) -> point(x, z) }
				}
				fill("${faction.color}22")    // color at ~13% opacity
				stroke(faction.color, weight = 3)
				tooltip(faction.name)
				popup(
					"<b>${faction.name}</b>" +
					"<br>Capital: ${faction.capitalX}, ${faction.capitalZ}"
				)
			}

			// Capital icon
			iconMarker("faction_capital_${faction.id}") {
				center(faction.capitalX, faction.capitalZ)
				image("faction_pin")    // must be pre-registered
				size(28)
				anchor(14.0, 28.0)
				tooltip("${faction.name} - Capital")
			}
		}
	}
}

// =============================================================================
// SCENARIO B - Live Player Tracker
//
// Refreshes player position markers every N ticks via a Bukkit scheduler.
// Each player gets their own circle + label on a shared layer.
// Removes markers for players that have gone offline between refreshes.
// =============================================================================

private val trackedPlayers = mutableSetOf<String>()

fun refreshPlayerTracker() {
	val currentPlayers = Pl3xAPI.players()
	val currentNames = currentPlayers.map { it.name }.toSet()

	// Remove markers for players no longer online
	val removed = trackedPlayers - currentNames
	removed.forEach { name ->
		forEachWorld {
			layerOrNull("player_tracker")?.let { layer ->
				layer.removeMarker("tracker_$name")
			}
		}
	}
	trackedPlayers.clear()
	trackedPlayers.addAll(currentNames)

	// Update/add markers for current players
	currentPlayers.forEach { player ->
		val world = Pl3xAPI.world(player.worldName)
		val layer = world.layer("player_tracker", "Player Tracker")

		// Remove stale marker before adding updated one
		layer.removeMarker("tracker_${player.name}")

		player.drawOn("player_tracker", "Player Tracker") {
			circle("tracker_${player.name}") {
				centerHere()
				radius(6.0)
				stroke("#FFFFFF", weight = 1)
				fill("#FFFFFF33")
				tooltip(player.name)
				popup("<b>${player.name}</b><br>HP: ${player.health}")
			}
		}
	}
}

// =============================================================================
// SCENARIO C - Temporary Event Zone
//
// An event zone that can be toggled on/off on demand.
// Uses a dedicated layer so it can be shown/hidden independently.
// =============================================================================

object EventZoneManager {
	private const val LAYER_KEY = "event_zone"
	private const val MARKER_KEY = "active_event_zone"

	fun activate(worldName: String, centerX: Int, centerZ: Int, radius: Int, eventName: String) {
		Pl3xAPI.draw(worldName, LAYER_KEY, "Event Zone") {
			configure {
				updateInterval = 5
				liveUpdate = true
				defaultHidden = false
			}

			// Remove existing zone if any
			remove(MARKER_KEY)

			circle(MARKER_KEY) {
				center(centerX, centerZ)
				radius(radius.toDouble())
				stroke("#FF6600", weight = 4)
				fill("#FF660044")
				tooltip("⚡ Active Event: $eventName")
				popup(
					"<b>$eventName</b>" +
					"<br>Center: $centerX, $centerZ" +
					"<br>Radius: $radius blocks"
				)
			}
		}
	}

	fun deactivate(worldName: String) {
		Pl3xAPI.world(worldName)
			.layerOrNull(LAYER_KEY)
			?.removeMarker(MARKER_KEY)
	}
}

// =============================================================================
// SCENARIO D - Auto-initialize layers on world load
//
// Whenever a new world loads into Pl3xMap, set up your standard layer
// configuration without needing to hardcode world names.
// =============================================================================

fun setupAutoWorldInit() {
	Pl3xAPI.events.onWorldLoad { pl3xWorld ->
		// pl3xWorld is net.pl3x.map.core.world.World (raw Pl3xMap type)
		val worldName = pl3xWorld.name

		Pl3xAPI.draw(worldName, "world_info", "World Info") {
			configure {
				updateInterval = 60
				showControls = true
				defaultHidden = false
				priority = 100      // lowest priority (bottom of layer list)
			}
		}

		Pl3xAPI.draw(worldName, "spawn_markers", "Spawn Markers") {
			configure {
				updateInterval = 120
				showControls = true
			}
			val world = Pl3xAPI.world(worldName)
			circle("world_spawn") {
				center(world.spawn.x.toInt(), world.spawn.z.toInt())
				radius(20.0)
				stroke("#00FF00", weight = 2)
				fill("#00FF0022")
				tooltip("World Spawn")
				popup("<b>Spawn</b><br>$worldName")
			}
		}
	}
}

// =============================================================================
// SCENARIO E - POI System from a Data List
//
// Renders a list of points of interest as icon markers.
// Registers icon images from disk on startup.
// =============================================================================

data class PointOfInterest(
	val id: String,
	val name: String,
	val worldName: String,
	val x: Int,
	val z: Int,
	val category: String,   // "shop", "town", "dungeon", etc.
	val description: String,
)

object PoiSystem {
	private val categoryIcons = mapOf(
		"shop"    to "icon_shop",
		"town"    to "icon_town",
		"dungeon" to "icon_dungeon",
	)

	fun registerIcons(pluginFolder: File) {
		categoryIcons.forEach { (category, iconKey) ->
			val file = File(pluginFolder, "icons/$category.png")
			if (file.exists()) {
				Pl3xAPI.icon(iconKey)
					.image(file)
					.overwrite()  // safe to call on every reload
					.register()
			}
		}
	}

	fun drawAll(pois: List<PointOfInterest>) {
		// Group by world to minimize layer lookups
		pois.groupBy { it.worldName }.forEach { (worldName, worldPois) ->
			Pl3xAPI.draw(worldName, "poi", "Points of Interest") {
				configure {
					updateInterval = 300    // rarely changes - update every 5 minutes
					showControls = true
				}
				clear()     // wipe stale POIs before re-drawing the full set

				worldPois.forEach { poi ->
					val iconKey = categoryIcons[poi.category] ?: return@forEach

					iconMarker("poi_${poi.id}") {
						center(poi.x, poi.z)
						image(iconKey)
						size(24)
						anchor(12.0, 24.0)
						tooltip("[${poi.category.uppercase()}] ${poi.name}")
						popup(
							"<b>${poi.name}</b>" +
							"<br><i>${poi.category}</i>" +
							"<br>${poi.description}" +
							"<br><small>$worldName - ${poi.x}, ${poi.z}</small>"
						)
					}
				}
			}
		}
	}
}

// =============================================================================
// FULL PLUGIN WIRING - brings all scenarios together
// =============================================================================

class FullExamplePlugin : org.bukkit.plugin.java.JavaPlugin() {

	override fun onEnable() {
		Pl3xBootstrap.attach()

		Pl3xBootstrap.onEnabled {
			// Register icons
			PoiSystem.registerIcons(dataFolder)

			// Setup auto world init - runs for every future world load too
			setupAutoWorldInit()

			// Draw initial data
			val factions = listOf(
				Faction(
					id = "red",
					name = "Red Faction",
					color = "#FF4400",
					borderPoints = listOf(0 to 0, 500 to 0, 500 to 500, 0 to 500),
					capitalX = 250,
					capitalZ = 250,
				)
			)
			drawFactions("world", factions)

			PoiSystem.drawAll(
				listOf(
					PointOfInterest("spawn_shop", "Spawn Shop", "world", 10, 10, "shop", "Sells basic goods."),
					PointOfInterest("main_town", "Main Town", "world", 200, -150, "town", "The central hub."),
				)
			)

			// Activate an event zone
			EventZoneManager.activate("world", 0, 0, 128, "Dragon Event")
		}

		Pl3xBootstrap.onDisabled {
			logger.info("[FullExample] Pl3xMap went offline.")
		}
	}

	override fun onDisable() {
		Pl3xBootstrap.detach()
	}
}
