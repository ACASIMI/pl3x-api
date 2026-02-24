package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.draw

/**
 * EXAMPLE 04 - Basic Markers (circle, ellipse, rectangle, polyline, region)
 *
 * The draw {} DSL is the primary way to add markers to the map.
 * All markers created inside the block are automatically bound to the
 * specified world and layer - no manual .world() or .layer() calls needed.
 *
 * Every factory function inside draw {} takes:
 *   - key: String - a stable, unique identifier for this marker on this layer
 *   - block: MarkerBuilder.() -> Unit - configuration lambda
 *
 * Calling draw {} again with the same key will ADD a duplicate unless you
 * use replace(), remove(), or layer.update() to handle it explicitly.
 */
fun basicMarkerExamples() {
	Pl3xAPI.draw("world", "markers", "Map Markers") {
		// =====================================================================
		// CIRCLE
		// Required: center(), radius()
		// Renders a filled/stroked circle centered at (x, z).
		// =====================================================================
		circle("spawn_zone") {
			center(0, 0)              // center at world origin
			radius(100.0)             // 100 blocks radius
			fill("#FF000033")         // red fill at ~20% opacity (hex RRGGBBAA)
			stroke("#FF0000", weight = 2)
			tooltip("Spawn Zone")     // shown on hover
			popup("<b>Spawn Zone</b><br>Radius: 100 blocks")
		}

		// =====================================================================
		// ELLIPSE
		// Required: center(), radius(x, z)
		// Optional: tilt (degrees) - rotates the ellipse
		// =====================================================================
		ellipse("oval_zone") {
			center(300, 300)
			radius(x = 80.0, z = 40.0)   // wide on X axis, narrow on Z
			// tilt(45.0)                 // optional: rotate 45 degrees
			fill("#0000FF22")
			stroke("#0000FF", weight = 1)
			tooltip("Oval Zone")
		}

		// =====================================================================
		// RECTANGLE
		// Required: corners(x1, z1, x2, z2)
		// Two diagonal corner points define the box.
		// =====================================================================
		rectangle("base_area") {
			corners(-50, -50, 50, 50)     // from (-50,-50) to (50,50)
			fill("#00FF0033")
			stroke("#00FF00", weight = 2)
			popup("<b>Base Area</b>")
		}

		// =====================================================================
		// POLYLINE
		// Required: at least 2 point() calls
		// Draws a non-filled path connecting the points in order.
		// =====================================================================
		polyline("trade_route") {
			point(0, 0)
			point(200, 100)
			point(400, 50)
			point(600, 300)
			stroke("#FFD70088", weight = 3)
			tooltip("Trade Route")
		}

		// =====================================================================
		// REGION (Polygon with optional holes)
		// Required: polygon {} block with at least 3 point() calls
		// Optional: one or more hole {} blocks (punch-through areas)
		// =====================================================================
		region("kingdom_border") {
			polygon {
				point(-200, -200)
				point(200, -200)
				point(200, 200)
				point(-200, 200)
			}
			// Optional inner cutout - creates a donut-shaped polygon
			hole {
				point(-100, -100)
				point(100, -100)
				point(100, 100)
				point(-100, 100)
			}
			fill("#FFD70022")
			stroke("#FFD700", weight = 3)
			tooltip("Kingdom Border")
			popup("<b>The Kingdom</b><br>You are entering royal territory.")
		}

		// =====================================================================
		// Removing a marker from inside the draw block
		// =====================================================================
		remove("old_marker_key")

		// =====================================================================
		// Replacing a marker atomically (remove + re-add in one call)
		// =====================================================================
		replace("spawn_zone") {
			circle("spawn_zone") {
				center(0, 0)
				radius(150.0)   // updated radius
				fill("#FF000055")
				stroke("#FF0000", weight = 3)
				tooltip("Spawn Zone (Updated)")
			}
		}

		// =====================================================================
		// Configuring the layer from inside the draw block
		// =====================================================================
		configure {
			updateInterval = 15
			liveUpdate = true
		}

		// =====================================================================
		// Clearing all markers on the layer from inside the draw block
		// =====================================================================
		// clear()  // uncomment to wipe all markers before re-drawing
	}
}
