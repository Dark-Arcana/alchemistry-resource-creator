package main.java.al132.imageutil

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

val startX = 0

fun main() {
    val elementData = File("elements.csv").readLines().drop(2)

    for (line in elementData) {
        var buf: BufferedImage = ImageIO.read(File("element_big.png"))
        val (elementName, abbrev, r, g, b) = line.split(",").drop(1).take(5)
        processImage(buf, abbrev, elementName, r.toInt(), g.toInt(), b.toInt())
        generateJSON(elementName)
    }
}

fun generateJSON(name: String) {
    if (!Files.exists(Paths.get("json"))) Files.createDirectory(Paths.get("json"))
    val output = """{
	"parent": "item/generated",
	 "textures": {
		"layer0": "alchemistry:items/elements/element_$name"
	}
}""".trimIndent()
    File("json/element_$name.json").writeText(output)
}

fun processImage(old: BufferedImage, abbreviation: String, name: String, r: Int, g: Int, b: Int) {
    val temp = BufferedImage(old.width, old.height, BufferedImage.TRANSLUCENT)
    val graphics: Graphics2D = temp.createGraphics()
    graphics.drawImage(tint(old, r, g, b), 0, 0, old.width, old.height, null);
    graphics.font = Font("Arial", Font.BOLD, 14)
    graphics.paint = Color(56, 56, 56)
    graphics.drawString(abbreviation, startX + 1, 11)
    graphics.paint = Color(225, 225, 225)
    graphics.drawString(abbreviation, startX, 10)

    graphics.dispose()
    if (!Files.exists(Paths.get("textures"))) Files.createDirectory(Paths.get("textures"))
    ImageIO.write(temp, "png", File("textures/element_$name.png"))
}

fun tint(image: BufferedImage, r: Int, g: Int, b: Int): BufferedImage {
    for (i in 0 until image.getWidth()) {
        for (j in 0 until image.getHeight()) {
            val ax = image.colorModel.getAlpha(image.raster.getDataElements(i, j, null))
            var rx = image.colorModel.getRed(image.raster.getDataElements(i, j, null))
            var gx = image.colorModel.getGreen(image.raster.getDataElements(i, j, null))
            var bx = image.colorModel.getBlue(image.raster.getDataElements(i, j, null))
            rx *= r
            gx *= g
            bx *= b
            image.setRGB(i, j, Color((rx / 255.0).toInt(), (gx / 255.0).toInt(), (bx / 255.0).toInt(), ax).rgb)
        }
    }
    return image
}
