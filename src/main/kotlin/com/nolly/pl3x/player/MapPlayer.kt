package com.nolly.pl3x.player

import net.pl3x.map.core.markers.Point
import net.pl3x.map.core.player.Player
import java.net.URL
import java.util.*

class MapPlayer(private val inner: Player) {
	val uuid: UUID get() = inner.uuid
	val name: String get() = inner.name

	val position: Point get() = inner.position
	val yaw: Float get() = inner.yaw

	val health: Int get() = inner.health
	val armorPoints: Int get() = inner.armorPoints

	val isHidden: Boolean get() = inner.isHidden

	val isNpc: Boolean get() = inner.isNPC

	val isInvisible: Boolean get() = inner.isInvisible
	val isSpectator: Boolean get() = inner.isSpectator

	val skinUrl: URL? get() = inner.skin

	val worldName: String get() = inner.world.name

	fun setHidden(hidden: Boolean, persistent: Boolean = true) = inner.setHidden(hidden, persistent)

	fun raw(): Player = inner

	override fun toString() = "MapPlayer(uuid=$uuid, name=$name)"
	override fun equals(other: Any?) = other is MapPlayer && uuid == other.uuid
	override fun hashCode() = uuid.hashCode()
}
