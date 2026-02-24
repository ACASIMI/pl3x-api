package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.drawOn
import com.nolly.pl3x.player.MapPlayer
import java.util.UUID

/**
 * EXAMPLE 07 - Players
 *
 * MapPlayer wraps Pl3xMap's Player and exposes all player data visible to the map.
 * PlayerDrawingScope (via drawOn {}) is a specialized draw context that automatically
 * resolves the player's current world, making it easy to draw markers relative to them.
 *
 * Use cases: player pins, radius indicators, territory visualizations, debug overlays.
 */
fun playerExamples() {
	// =========================================================================
	// ACCESSING PLAYERS
	// =========================================================================

	// Get all online players tracked by Pl3xMap
	val players: List<MapPlayer> = Pl3xAPI.players()

	// Get a specific player by UUID (returns null if not online or not tracked)
	val uuid = UUID.fromString("00000000-0000-0000-0000-000000000000")
	val player: MapPlayer? = Pl3xAPI.player(uuid)

	// =========================================================================
	// PLAYER PROPERTIES
	// =========================================================================
	players.forEach { p ->
		println("UUID:       ${p.uuid}")
		println("Name:       ${p.name}")
		println("Position:   ${p.position}")  // net.pl3x.map.core.markers.Point (x, z)
		println("Yaw:        ${p.yaw}")       // Float - horizontal facing angle
		println("Health:     ${p.health}")    // Int - current health points
		println("Armor:      ${p.armorPoints}")
		println("World:      ${p.worldName}")
		println("Hidden:     ${p.isHidden}")      // hidden from map display
		println("NPC:        ${p.isNpc}")
		println("Invisible:  ${p.isInvisible}")
		println("Spectator:  ${p.isSpectator}")
		println("Skin URL:   ${p.skinUrl}")    // URL? - may be null for non-premium accounts

		// Access raw Pl3xMap Player object if needed
		val raw = p.raw()
	}

	// =========================================================================
	// HIDING / SHOWING A PLAYER ON THE MAP
	// =========================================================================
	player?.let {
		// Hide the player from the map
		it.setHidden(true, persistent = true)   // persistent = survives relog
		// Show them again
		it.setHidden(false, persistent = false) // temporary - resets on relog
	}

	// =========================================================================
	// PLAYER DRAWING SCOPE - drawOn {}
	//
	// drawOn resolves the player's current world automatically.
	// The layer is created on that world, not a hardcoded world name.
	// centerHere() is a convenience that sets center() to the player's current position.
	// =========================================================================
	players.forEach { p ->

		p.drawOn("player_overlays", "Player Overlays") {

			// Draw a circle at the player's current position
			circle("${p.name}_position") {
				centerHere()          // equivalent to center(p.position.x, p.position.z)
				radius(8.0)
				stroke("#FFFFFF", weight = 1)
				fill("#FFFFFF22")
				tooltip(p.name)
			}

			// Draw a detection radius around the player
			circle("${p.name}_detection") {
				centerHere()
				radius(32.0)
				stroke("#FF000066", weight = 1)
				fill("#FF000011")
				tooltip("Detection radius: ${p.name}")
			}

			// Draw an icon at the player's location
			icon("${p.name}_icon") {
				centerHere()
				image("pin_red")      // must be registered first - see Example 05
				size(20)
				anchor(10.0, 20.0)
				tooltip(p.name)
				popup(
					"<b>${p.name}</b>" +
					"<br>HP: ${p.health}" +
					"<br>World: ${p.worldName}"
				)
			}

			// Draw a rectangle relative to the player's position
			// (manual offset from centerHere, since rectangle uses corners())
			rectangle("${p.name}_chunk") {
				val x = p.position.x.toInt()
				val z = p.position.z.toInt()
				// Highlight the 16x16 chunk the player is in
				val chunkX = (x shr 4) shl 4
				val chunkZ = (z shr 4) shl 4
				corners(chunkX, chunkZ, chunkX + 16, chunkZ + 16)
				stroke("#FFFF00", weight = 1)
				fill("#FFFF0011")
				tooltip("${p.name}'s chunk")
			}
		}
	}

	// =========================================================================
	// REFRESHING PLAYER MARKERS (common pattern for repeating tasks)
	// Run this in a Bukkit repeating task to keep markers updated.
	// =========================================================================
	fun refreshPlayerMarkers() {
		Pl3xAPI.players().forEach { p ->
			// Remove old marker before re-adding the updated one
			val world = Pl3xAPI.world(p.worldName)
			val layer = world.layerOrNull("player_overlays")
			layer?.removeMarker("${p.name}_position")

			p.drawOn("player_overlays", "Player Overlays") {
				circle("${p.name}_position") {
					centerHere()
					radius(8.0)
					stroke("#FFFFFF", weight = 1)
					fill("#FFFFFF22")
					tooltip(p.name)
				}
			}
		}
	}
}
