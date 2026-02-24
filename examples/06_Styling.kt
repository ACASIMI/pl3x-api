package examples

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.dsl.StylePreset
import com.nolly.pl3x.dsl.applyPreset
import com.nolly.pl3x.marker.StyleDSL
import com.nolly.pl3x.marker.style
import net.pl3x.map.core.markers.option.Fill
import net.pl3x.map.core.markers.option.Stroke
import net.pl3x.map.core.markers.option.Tooltip

/**
 * EXAMPLE 06 - Styling
 *
 * All visual customization goes through StyleDSL, which wraps Pl3xMap's Options builder.
 * You can apply styles inline via style {}, or via the shorthand methods on MarkerBuilder.
 * StylePresets allow you to define a reusable style block and apply it to multiple markers.
 *
 * Color format: hex string - either '#RRGGBB' or '#RRGGBBAA'
 *   - '#FF0000'     → opaque red
 *   - '#FF000080'   → red at ~50% opacity
 *   - '#FF000000'   → fully transparent red (invisible fill, but still interactive)
 */
fun stylingExamples() {
	// =========================================================================
	// FULL STYLE DSL - all available options
	// =========================================================================
	Pl3xAPI.draw("world", "styled_layer", "Styled Markers") {

		circle("fully_styled") {
			center(0, 0)
			radius(100.0)

			style {
				// --- Stroke (border line) ---
				stroke("#FF0000", weight = 3)          // color + line thickness in pixels
				strokeEnabled(true)                    // show/hide the stroke entirely
				strokeDashPattern("10, 5")             // CSS stroke-dasharray pattern
				strokeDashOffset("0")                  // CSS stroke-dashoffset
				strokeLineCap(Stroke.LineCapShape.ROUND)    // BUTT, ROUND, SQUARE
				strokeLineJoin(Stroke.LineJoinShape.ROUND)  // MITER, ROUND, BEVEL

				// --- Fill ---
				fill("#FF000033", type = Fill.Type.EVENODD)  // color + fill rule
				// Fill.Type options: EVENODD, NONZERO
				fillEnabled(true)                      // show/hide fill entirely

				// --- Popup (click to open) ---
				popup(
					content = "<b>Styled Circle</b><br>This is a popup.",
					pane = "popupPane",                // optional Leaflet pane name
					maxWidth = 300,
					minWidth = 100
				)
				popupMaxHeight(200)                    // px - enables scrolling if content overflows
				popupAutoPan(true)                     // auto-pan map so popup is visible
				popupAutoClose(true)                   // close when another popup opens
				popupCloseOnClick(false)               // don't close on map click
				popupCloseOnEscape(true)               // close on ESC key
				popupCloseButton(true)                 // show the × button
				popupKeepInView(true)                  // prevent popup from going off-screen

				// --- Tooltip (hover) ---
				tooltip(
					content = "Styled Circle",
					sticky = true,                     // tooltip follows the cursor
					direction = Tooltip.Direction.TOP, // TOP, BOTTOM, LEFT, RIGHT, CENTER, AUTO
					permanent = false,                 // always visible (not just on hover)
					opacity = 0.9,                     // 0.0–1.0
					pane = "tooltipPane"
				)
			}
		}

		// =====================================================================
		// SHORTHAND methods - equivalents for the most common style calls
		// These exist directly on MarkerBuilder for ergonomic one-liners.
		// =====================================================================
		rectangle("shorthand_demo") {
			corners(200, 200, 400, 400)

			stroke("#00FF00", weight = 2)       // shorthand for style { stroke(...) }
			fill("#00FF0033")                   // shorthand for style { fill(...) }
			tooltip("Hover text", sticky = false)
			popup("<b>Click for info</b>")
		}
	}

	// =========================================================================
	// STYLE PRESETS - reusable, named style configurations
	// Define once, apply to any number of markers.
	// =========================================================================

	// Define presets at a scope accessible by all your draw calls
	val dangerZone = StylePreset {
		stroke("#FF0000", weight = 3)
		fill("#FF000033")
		tooltip("⚠ Danger Zone", sticky = true)
	}

	val safeZone = StylePreset {
		stroke("#00FF00", weight = 2)
		fill("#00FF0033")
		tooltip("✔ Safe Zone")
	}

	val highlightZone = StylePreset {
		stroke("#FFD700", weight = 4)
		strokeDashPattern("8, 4")
		fill("#FFD70022")
		tooltip("★ Highlighted")
	}

	// Apply presets in draw blocks
	Pl3xAPI.draw("world", "preset_layer", "Preset Demos") {

		circle("danger_1") {
			center(100, 100)
			radius(50.0)
			applyPreset(dangerZone)
		}

		circle("danger_2") {
			center(300, 100)
			radius(75.0)
			applyPreset(dangerZone)    // same style, different geometry
		}

		rectangle("safe_base") {
			corners(-100, -100, 100, 100)
			applyPreset(safeZone)
		}

		region("highlight_border") {
			polygon {
				point(500, 0); point(700, 0); point(700, 200); point(500, 200)
			}
			applyPreset(highlightZone)
		}
	}

	// =========================================================================
	// BUILDING OPTIONS STANDALONE - using the style() top-level function
	// Useful when you need an Options object outside of a DSL context.
	// =========================================================================
	val opts = style {
		stroke("#FFFFFF", weight = 1)
		fill("#FFFFFF11")
		tooltip("Standalone style")
	}
	// opts is now a net.pl3x.map.core.markers.option.Options instance
	// You can pass it to any MarkerBuilder via .options(opts)
	Pl3xAPI.circle("opts_demo")
		.world("world")
		.layer("opts_layer")
		.center(0, 500)
		.radius(30.0)
		.options(opts)
		.register()
}
