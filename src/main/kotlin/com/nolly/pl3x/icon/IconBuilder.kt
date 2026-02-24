package com.nolly.pl3x.icon

import com.nolly.pl3x.Pl3xContext
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class IconBuilder(private val key: String) {
	private var image: BufferedImage? = null
	private var format: String = "png"
	private var overwrite: Boolean = false

	fun image(img: BufferedImage): IconBuilder {
		image = img
		return this
	}

	fun image(path: String): IconBuilder {
		image = ImageIO.read(File(path))
		return this
	}

	fun image(file: File): IconBuilder {
		image = ImageIO.read(file)
		return this
	}

	fun format(fmt: String): IconBuilder {
		format = fmt
		return this
	}

	fun overwrite(): IconBuilder {
		overwrite = true
		return this
	}

	fun register() {
		val img = requireNotNull(image) { "IconBuilder: call .image(...) before .register()" }
		Pl3xContext.icons.register(key, img, format, overwrite)
	}
}
