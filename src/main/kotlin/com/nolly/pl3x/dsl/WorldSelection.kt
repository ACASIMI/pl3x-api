package com.nolly.pl3x.dsl

import com.nolly.pl3x.Pl3xAPI
import com.nolly.pl3x.world.MapWorld

fun forEachWorld(block: MapWorld.() -> Unit) {
	Pl3xAPI.worlds().forEach { it.block() }
}

fun forWorlds(predicate: (String) -> Boolean, block: MapWorld.() -> Unit) {
	Pl3xAPI.worlds().filter { predicate(it.name) }.forEach { it.block() }
}

fun forWorlds(vararg names: String, block: MapWorld.() -> Unit) {
	val set = names.toSet()
	Pl3xAPI.worlds().filter { it.name in set }.forEach { it.block() }
}
