package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.forEachWorld
import com.nolly.pl3x.dsl.forWorlds
import com.nolly.pl3x.world.MapWorld

/**
 * EXAMPLE 02 - Worlds
 *
 * MapWorld wraps Pl3xMap's World and is your gateway to layers, players,
 * spawn point, render control, and world-level config.
 *
 * Worlds are lazily created in the WorldRegistry the first time you access them.
 * They are backed by Pl3xMap's own world registry, so a world must be loaded there first.
 */
fun worldExamples() {
	// --- Accessing a single world by name ---
	// This will throw if the world is not loaded in Pl3xMap.
	val overworld: MapWorld = Pl3xAPI.world("world")

	// --- Listing all worlds currently registered in Pl3xMap ---
	val allWorlds: List<MapWorld> = Pl3xAPI.worlds()
	allWorlds.forEach { world ->
		println("World: ${world.name}")
	}

	// --- World properties ---
	val world = Pl3xAPI.world("world")

	println(world.name)            // The world's name string, e.g. "world"
	println(world.spawn)           // net.pl3x.map.core.markers.Point - the world spawn XZ
	println(world.isEnabled)       // Whether this world is actively being rendered by Pl3xMap
	println(world.players())       // List<MapPlayer> - players currently in this world

	// --- Getting a layer from a world ---
	// If the layer doesn't exist yet, it's created and registered automatically.
	val layer = world.layer("my_layer", "My Layer Label")

	// --- Getting a layer that might not exist (returns null instead of creating) ---
	val maybeLayer = world.layerOrNull("optional_layer")
	if (maybeLayer == null) {
		println("Layer does not exist yet.")
	}

	// --- Removing a layer from the world ---
	// This unregisters it from Pl3xMap and removes it from the internal registry.
	world.removeLayer("my_layer")

	// --- Iterating all worlds (DSL shorthand) ---
	forEachWorld {
		// "this" is MapWorld here
		println("Processing world: $name")
	}

	// --- Iterating worlds by a custom predicate ---
	forWorlds(predicate = { it.contains("survival") }) {
		println("Survival world: $name")
	}

	// --- Iterating specific named worlds only ---
	forWorlds("world", "world_nether", "world_the_end") {
		println("Dimension: $name")
	}
}
