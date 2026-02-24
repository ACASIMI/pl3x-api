package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.box
import com.nolly.pl3x.dsl.line
import com.nolly.pl3x.dsl.ring
import java.io.File

/**
 * EXAMPLE 05 - Advanced Markers (icon, multiPolygon, multiPolyline, shape helpers)
 *
 * Covers:
 * - Registering and using custom icon images
 * - Icon markers with size, anchor, rotation
 * - MultiPolygon: multiple polygons as a single marker (each with holes)
 * - MultiPolyline: multiple independent lines as a single marker
 * - Shape helpers: line(), box(), ring()
 * - Using MarkerBuilder directly (outside draw DSL)
 */
fun advancedMarkerExamples() {
	// =========================================================================
	// ICON REGISTRATION
	// Icons must be registered before they can be used in iconMarker().
	// The key is a stable string you'll reference in image().
	// =========================================================================

	// From a File path
	Pl3xAPI.icon("pin_red")
		.image(File("plugins/MyPlugin/icons/pin_red.png"))
		.format("png")       // default is "png"
		.register()

	// From an AWT BufferedImage (e.g. dynamically generated)
	// val img: BufferedImage = ImageIO.read(...)
	// Pl3xAPI.icon("dynamic_icon").image(img).register()

	// With overwrite = true, re-registering the same key won't throw.
	Pl3xAPI.icon("pin_red")
		.image(File("plugins/MyPlugin/icons/pin_red_v2.png"))
		.overwrite()
		.register()

	// =========================================================================
	// ICON MARKER
	// Required: center(), image(key)
	// Optional: size(), anchor(), rotation(), rotationOrigin()
	// =========================================================================
	Pl3xAPI.draw("world", "icons_layer", "POI Icons") {

		iconMarker("player_home") {
			center(100, 200)           // position on the map (block X, Z)
			image("pin_red")           // the key used during icon registration
			size(32, 32)               // display size in pixels (width, height)
			anchor(16.0, 32.0)         // anchor point offset (tip of the pin)
			rotation(45.0)             // rotate the icon 45 degrees
			rotationOrigin("center")   // CSS transform-origin string
			tooltip("Home Base")
			popup(""<b>Home</b><br>X: 100, Z: 200"")
		}

		// Square icon with uniform size shorthand
		iconMarker("shop_marker") {
			center(-300, 150)
			image("pin_red")
			size(24)                   // size(px: Int) sets width = height
			tooltip("Shop")
		}
	}

	// =========================================================================
	// MULTI-POLYGON
	// Required: at least one addPolygon {} call
	// Each addPolygon takes: outer lambda + vararg hole lambdas
	// All polygons share the same options/style.
	// =========================================================================
	Pl3xAPI.draw("world", "territories", "Territories") {

		multiPolygon("faction_lands") {
			// First territory - solid polygon
			addPolygon(
				outer = {
					point(0, 0)
					point(300, 0)
					point(300, 300)
					point(0, 300)
				}
			)
			// Second territory - polygon with a hole
			addPolygon(
				outer = {
					point(500, 500)
					point(800, 500)
					point(800, 800)
					point(500, 800)
				},
				// Hole lambda passed as vararg
				{
					point(600, 600)
					point(700, 600)
					point(700, 700)
					point(600, 700)
				}
			)
			fill("#FF440033")
			stroke("#FF4400", weight = 2)
			tooltip("Faction Lands")
		}
	}

	// =========================================================================
	// MULTI-POLYLINE
	// Required: at least one addLine {} call
	// Useful for grouping related lines as a single map marker.
	// =========================================================================
	Pl3xAPI.draw("world", "roads", "Road Network") {

		multiPolyline("highway_network") {
			addLine {
				point(0, 0)
				point(500, 0)
				point(500, 500)
			}
			addLine {
				point(-200, 100)
				point(0, 0)
				point(100, -200)
			}
			stroke("#AAAAAA", weight = 4)
			tooltip("Highway Network")
		}
	}

	// =========================================================================
	// SHAPE HELPERS
	// Extension functions on MarkerBuilder for common shapes.
	// Must be used via the DSL block to remain within world/layer context.
	// =========================================================================
	Pl3xAPI.draw("world", "shapes_layer", "Shape Demos") {

		// LINE - straight line between two points (implemented as a 2-point polyline)
		polyline("border_line") {
			line(x1 = -500, z1 = 0, x2 = 500, z2 = 0)
			stroke("#FFFFFF", weight = 2)
			tooltip("World Border Line")
		}

		// BOX - centered square, defined by center + half-size
		// Equivalent to rectangle(center - half, center - half, center + half, center + half)
		rectangle("spawn_box") {
			box(centerX = 0, centerZ = 0, halfSize = 64)
			fill("#FFFFFF11")
			stroke("#FFFFFF", weight = 1)
			tooltip("Spawn Box (128x128)")
		}

		// RING - donut-shaped polygon with outer and inner radius
		// segments controls how many points approximate the circle (default 32, min 3)
		region("arena_ring") {
			ring(
				centerX = 0,
				centerZ = 0,
				outerRadius = 200,
				innerRadius = 150,
				segments = 64    // higher = smoother circle
			)
			fill("#FF000044")
			stroke("#FF0000", weight = 2)
			tooltip("Arena Ring")
		}
	}

	// =========================================================================
	// MARKER BUILDER - used directly, outside the draw DSL
	// Useful when building markers programmatically in non-DSL contexts.
	// Must call .world() and .layer() before .register().
	// =========================================================================
	Pl3xAPI.circle("dynamic_circle")
		.world("world")
		.layer("dynamic_layer", "Dynamic Layer")
		.center(100, 100)
		.radius(50.0)
		.fill("#00FFFF33")
		.stroke("#00FFFF", weight = 2)
		.tooltip("Dynamically built circle")
		.register()
}
