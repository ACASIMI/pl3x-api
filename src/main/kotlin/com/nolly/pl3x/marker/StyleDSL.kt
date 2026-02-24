package com.nolly.pl3x.marker

import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.markers.option.Fill
import net.pl3x.map.core.markers.option.Options
import net.pl3x.map.core.markers.option.Stroke
import net.pl3x.map.core.markers.option.Tooltip
import net.pl3x.map.core.util.Colors

class StyleDSL {
	companion object {
		fun from(opts: Options): StyleDSL {
			val dsl = StyleDSL()
			dsl.builder = opts.asBuilder()
			return dsl
		}
	}

	internal var builder = Options.builder()

	fun stroke(hex: String, weight: Int = 3): StyleDSL {
		builder.strokeColor(Colors.fromHex(hex)).strokeWeight(weight)
		return this
	}

	fun strokeEnabled(enabled: Boolean): StyleDSL {
		builder.stroke(enabled)
		return this
	}

	fun strokeDashPattern(pattern: String): StyleDSL {
		builder.strokeDashPattern(pattern)
		return this
	}

	fun strokeDashOffset(offset: String): StyleDSL {
		builder.strokeDashOffset(offset)
		return this
	}

	fun strokeLineCap(shape: Stroke.LineCapShape): StyleDSL {
		builder.strokeLineCapShape(shape)
		return this
	}

	fun strokeLineJoin(shape: Stroke.LineJoinShape): StyleDSL {
		builder.strokeLineJoinShape(shape)
		return this
	}

	fun fill(hex: String, type: Fill.Type = Fill.Type.EVENODD): StyleDSL {
		builder.fillColor(Colors.fromHex(hex)).fillType(type)
		return this
	}

	fun fillEnabled(enabled: Boolean): StyleDSL {
		builder.fill(enabled)
		return this
	}

	fun popup(
		content: String,
		pane: String? = null,
		offset: Point? = null,
		maxWidth: Int? = null,
		minWidth: Int? = null,
	): StyleDSL {
		builder.popupContent(content)
		pane?.let { builder.popupPane(it) }
		offset?.let { builder.popupOffset(it) }
		maxWidth?.let { builder.popupMaxWidth(it) }
		minWidth?.let { builder.popupMinWidth(it) }
		return this
	}

	fun popupMaxHeight(px: Int): StyleDSL {
		builder.popupMaxHeight(px)
		return this
	}

	fun popupAutoPan(enabled: Boolean): StyleDSL {
		builder.popupShouldAutoPan(enabled)
		return this
	}

	fun popupAutoClose(enabled: Boolean): StyleDSL {
		builder.popupShouldAutoClose(enabled)
		return this
	}

	fun popupCloseOnClick(enabled: Boolean): StyleDSL {
		builder.popupShouldCloseOnClick(enabled)
		return this
	}

	fun popupCloseOnEscape(enabled: Boolean): StyleDSL {
		builder.popupShouldCloseOnEscapeKey(enabled)
		return this
	}

	fun popupCloseButton(enabled: Boolean): StyleDSL {
		builder.popupCloseButton(enabled)
		return this
	}

	fun popupKeepInView(enabled: Boolean): StyleDSL {
		builder.popupShouldKeepInView(enabled)
		return this
	}

	fun tooltip(
		content: String,
		sticky: Boolean = false,
		direction: Tooltip.Direction = Tooltip.Direction.TOP,
		permanent: Boolean = false,
		opacity: Double = 1.0,
		pane: String? = null,
		offset: Point? = null,
	): StyleDSL {
		builder.tooltipContent(content).tooltipSticky(sticky).tooltipDirection(direction).tooltipPermanent(permanent)
			.tooltipOpacity(opacity)
		pane?.let { builder.tooltipPane(it) }
		offset?.let { builder.tooltipOffset(it) }
		return this
	}

	fun build(): Options = builder.build()
}

fun style(block: StyleDSL.() -> Unit): Options = StyleDSL().apply(block).build()
