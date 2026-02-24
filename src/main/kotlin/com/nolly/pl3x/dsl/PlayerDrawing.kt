package com.nolly.pl3x.dsl

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.marker.MarkerBuilder
import com.nolly.pl3x.player.MapPlayer

class PlayerDrawingScope(private val player: MapPlayer, layerKey: String, layerLabel: String = layerKey) {
	private val world = Pl3xAPI.world(player.worldName)
	private val layer = world.layer(layerKey, layerLabel)

	fun circle(key: String, block: MarkerBuilder.() -> Unit = {}) =
		bound(key, MarkerBuilder.Type.CIRCLE, block)

	fun region(key: String, block: MarkerBuilder.() -> Unit = {}) =
		bound(key, MarkerBuilder.Type.REGION, block)

	fun rectangle(key: String, block: MarkerBuilder.() -> Unit = {}) =
		bound(key, MarkerBuilder.Type.RECTANGLE, block)

	fun icon(key: String, block: MarkerBuilder.() -> Unit = {}) =
		bound(key, MarkerBuilder.Type.ICON, block)

	fun MarkerBuilder.centerHere(): MarkerBuilder {
		val pos = player.position
		return center(pos.x, pos.z)
	}

	private fun bound(
		key: String,
		type: MarkerBuilder.Type,
		block: MarkerBuilder.() -> Unit,
	) = MarkerBuilder(key, type)
		.world(world.name)
		.layer(layer.key, layer.label)
		.apply(block)
		.register()
}

fun MapPlayer.drawOn(layerKey: String, layerLabel: String = layerKey, block: PlayerDrawingScope.() -> Unit) {
	PlayerDrawingScope(this, layerKey, layerLabel).block()
}
