package al132.imageutil

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


val absolutePath = File("").absolutePath
val resourceRoot = "resources/alchemistry"
val imageResource = ClassLoader.getSystemClassLoader().getResource("element_big.png");

fun main() {

    println("Please enter the name of your element")
    val elementName = readLine()!!.toLowerCase()
    if (elementName.startsWith("/csv")) runCSV(elementName)
    else {
        println("Please enter the abbreviation of your element")
        val abbreviation = readLine()!!.toLowerCase().capitalize()
        println("Please enter the R,G,B of your element, example in next line")
        println("230, 114, 52")
        val (r, g, b) = readLine()!!.split(",").map { it.trim().toInt() }
        val buf: BufferedImage = ImageIO.read(imageResource)
        processImage(buf, abbreviation, elementName, r, g, b)
        generateJSON(elementName)
    }
    println("Resources generated!")
}

fun runCSV(rawInput: String) {
    val elementData = File(rawInput.split(" ")[1]).readLines().drop(2)
    for (line in elementData) {
        val buf: BufferedImage = ImageIO.read(imageResource)
        val (elementName, abbrev, r, g, b) = line.split(",").drop(1).take(5)
        processImage(buf, abbrev, elementName, r.toInt(), g.toInt(), b.toInt())
        generateJSON(elementName)
    }
}


fun generateJSON(name: String) {
    val path = File("$absolutePath/$resourceRoot/models/item/element_$name.json")
    path.parentFile.mkdirs()
    val output = """{
	"parent": "item/generated",
	 "textures": {
		"layer0": "alchemistry:items/elements/element_$name"
	}
}""".trimIndent()
    path.writeText(output)
}

fun processImage(old: BufferedImage, abbreviation: String, name: String, r: Int, g: Int, b: Int) {
    val startX = 0

    val temp = BufferedImage(old.width, old.height, BufferedImage.TRANSLUCENT)
    val graphics: Graphics2D = temp.createGraphics()
    graphics.drawImage(tint(old, r, g, b), 0, 0, old.width, old.height, null);
    graphics.font = Font("Arial", Font.BOLD, 14)
    graphics.paint = Color(56, 56, 56)
    graphics.drawString(abbreviation, startX + 1, 11)
    graphics.paint = Color(225, 225, 225)
    graphics.drawString(abbreviation, startX, 10)
    graphics.dispose()

    val graphicsFile = File("$absolutePath/$resourceRoot/textures/items/elements/element_$name.png")
    graphicsFile.parentFile.mkdirs()
    ImageIO.write(temp, "png", graphicsFile)
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
