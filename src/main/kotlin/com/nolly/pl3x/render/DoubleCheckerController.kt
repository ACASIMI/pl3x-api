package com.nolly.pl3x.render

import net.pl3x.map.core.Pl3xMap

class DoubleCheckerController {
	fun stop() {
		Pl3xMap.api().regionDoubleChecker.stop()
	}

	fun restart(delayMs: Long = 250_000L) {
		Pl3xMap.api().regionDoubleChecker.stop()
		Pl3xMap.api().regionDoubleChecker.start(delayMs)
	}
}
