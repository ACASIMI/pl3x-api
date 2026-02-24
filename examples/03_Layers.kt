package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.LayerPreset
import com.nolly.pl3x.layer.MapLayer
import com.nolly.pl3x.dsl.applyPreset
import com.nolly.pl3x.layer.configure

/**
 * EXAMPLE 03 - Layers
 *
 * MapLayer wraps Pl3xMap's SimpleLayer. Each layer belongs to one world
 * and is identified by a unique key. Layers hold markers and have visual
 * configuration properties that control how they appear on the map UI.
 *
 * Key/label: the key is a stable identifier; the label is the display name
 * shown in the Pl3xMap UI layer control.
 */
fun layerExamples() {
	val world = Pl3xAPI.world("world")

	// --- Creating or getting a layer ---
	// If a layer with this key already exists for this world, it returns it.
	// If not, it creates, registers, and returns a new one.
	val layer: MapLayer = world.layer("zones", "Zone Overlays")

	// --- Configuring a layer inline ---
	layer.configure {
		// How often (in seconds) the client fetches updated marker data.
		updateInterval = 10

		// Whether this layer shows in the layer control panel on the map UI.
		showControls = true

		// If true, this layer is hidden by default in the UI (user must enable it).
		defaultHidden = false

		// Lower priority = appears higher in the layer list.
		priority = 5

		// Leaflet z-index for stacking layers on the map canvas.
		zIndex = 10

		// If true, markers update live without requiring a full page refresh.
		liveUpdate = true

		// Optional: target a custom Leaflet pane for this layer.
		pane = "overlayPane"

		// Optional: inject custom CSS scoped to this layer's Leaflet container.
		css = ".leaflet-pl3x-zones { opacity: 0.85; }"

		// The display label (can also be changed at any time).
		label = "Zone Overlays"
	}

	// --- Reading layer properties ---
	println("Layer key: ${layer.key}")
	println("Layer world: ${layer.worldName()}")
	println("Marker count: ${layer.markers().size}")

	// --- Checking and removing markers ---
	if (layer.hasMarker("old_zone")) {
		layer.removeMarker("old_zone")
	}

	// Clear ALL markers from this layer at once.
	layer.clearMarkers()

	// --- Getting all registered markers on a layer ---
	// Returns Map<String, Marker<*>> from raw Pl3xMap - useful for inspection.
	val rawMarkers = layer.markers()
	rawMarkers.forEach { (key, marker) ->
		println("Marker: $key -> $marker")
	}

	// --- Layer Presets ---
	// Define a reusable configuration block and apply it to multiple layers.

	val overlayPreset = LayerPreset {
		updateInterval = 30
		defaultHidden = true    // hidden by default - user opts in
		priority = 50
		zIndex = 5
		showControls = true
	}

	val adminLayer = world.layer("admin_zones", "Admin Zones")
	adminLayer.applyPreset(overlayPreset)

	val pvpLayer = world.layer("pvp_zones", "PvP Zones")
	pvpLayer.applyPreset(overlayPreset)
	pvpLayer.label = "PvP Zones"   // override individual fields after preset

	// --- Accessing layers globally from Pl3xAPI ---

	// Get the default layer of the first world.
	val defaultLayer: MapLayer = Pl3xAPI.layer("my_layer")

	// Get all "primary" layers across every world.
	// Internally resolves layers by common naming conventions:
	// worldName_default > worldName > default > main
	val allPrimaryLayers: List<MapLayer> = Pl3xAPI.layers()
	allPrimaryLayers.forEach {
		println("Primary layer for a world: ${it.key}")
	}
}
