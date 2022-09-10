package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    imageProperties()
}

fun imageProperties() {
    try {
        println("Input the image filename:")
        val imageInput = readImage(readln())
        readNumComponent(imageInput, "image")
        numBits(imageInput, "image")

        println("Input the watermark image filename:")
        val imageWatermark = readImage(readln())

        var transparencyColor: Color? = null
        var checkAlphaS = false

        if (checkAlpha(imageWatermark)) {
            checkAlphaS = isUseAlpha()
        } else {
            readNumComponent(imageWatermark, "watermark")
            numBits(imageWatermark, "watermark")
            dimensionsFilesEquals(imageInput, imageWatermark)

            println("Do you want to set a transparency color?")
            if (isAllow(readln())) {
                transparencyColor = createColor()
            }
        }

        println("Input the watermark transparency percentage (Integer 0-100):")
        val transparencyPercentage = getTransparencyPercentage(readln())

        println("Choose the position method (single, grid):")
        val isSingle = isSingle(readln())
        val position = watermarkPosition(isSingle, imageInput, imageWatermark)

        println("Input the output image filename (jpg or png extension):")
        val outputFile = readln()

        val tuneColor = tuneColor(
            imageInput,
            imageWatermark,
            transparencyPercentage,
            checkAlphaS,
            transparencyColor,
            position
        )
        saveFile(outputFile, tuneColor)

    } catch (e: Exception) {
        println(e.message)
        exitProcess(0)
    }
}

private class Position(val x: Int, val y: Int)

private fun watermarkPosition(isSingle: Boolean, imageInput: BufferedImage, imageWatermark: BufferedImage): Position? {
    val xPos = imageInput.width - imageWatermark.width
    val yPos = imageInput.height - imageWatermark.height

    return if (isSingle) {
        println("Input the watermark position ([x 0-$xPos] [y 0-$yPos]):")
        val position = createCoordinate(readln(), imageInput, imageWatermark)

        if (position.x in 0..xPos && position.y in 0..yPos) {
            position
        } else throw RuntimeException("The position input is out of range.")
    } else null
}

private fun createCoordinate(coordinate: String, imageInput: BufferedImage, imageWatermark: BufferedImage): Position {
    val coord = coordinate.split(" ")

    return if (isString(coord[0])) {
        return createStringCoord(coordinate, imageInput, imageWatermark)
    } else createNumberCoord(coordinate)
}

private fun isString(coord: String): Boolean {
    return try {
        coord.toInt()
        false
    } catch (e: Exception) {
        true
    }
}

private fun createStringCoord(coord: String, imageInput: BufferedImage, imageWatermark: BufferedImage): Position {
    return when (coord) {
        "top left" -> Position(0, 0)
        "top right" -> Position(imageInput.width, 0)
        "bottom left" -> Position(0, imageInput.height)
        "bottom right" -> Position(imageInput.width, imageInput.height)
        "middle " -> Position(imageInput.width / 2, imageInput.height / 2)
        else -> throw RuntimeException("The position input is invalid.")
    }
}

private fun createNumberCoord(coord: String): Position {
    val (x, y) = coord.split(" ")
    return Position(x.toInt(), y.toInt())
}

private fun isSingle(readChooser: String): Boolean {
    return when (readChooser) {
        "single" -> true
        "grid" -> false
        else -> throw RuntimeException("The position method input is invalid.")
    }
}

private fun createColor(): Color {
    println("Input a transparency color ([Red] [Green] [Blue]):")

    return try {
        val rgb = readln().split(" ")
        if (rgb.size != 3) {
            throw RuntimeException()
        }
        checkNumColor(rgb[0]) || checkNumColor(rgb[1]) || checkNumColor(rgb[2])
        Color(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
    } catch (e: Exception) {
        throw Exception("The transparency color input is invalid.")
    }
}

private fun checkNumColor(num: String) =
    if (num.toInt() > 0 || num.toInt() < 255) true else throw Exception("The transparency color input is invalid.")

private fun readImage(pathName: String): BufferedImage {
    return try {
        val file = File(pathName)
        ImageIO.read(file)
    } catch (e: IOException) {
        throw RuntimeException("The file $pathName doesn't exist.")
    }
}

private fun getTransparencyPercentage(transparency: String): Int {
    return try {
        val percentage = transparency.toInt()
        if (percentage in 0..100) {
            percentage
        } else throw RuntimeException("The transparency percentage is out of range.")
    } catch (e: NumberFormatException) {
        throw RuntimeException("The transparency percentage isn't an integer number.")
    }
}

private fun readNumComponent(imageInput: BufferedImage, name: String) {
    val numComponents = imageInput.colorModel.numComponents
    if (numComponents != 3) {
        throw RuntimeException("The number of $name color components isn't 3.")
    }
}

private fun numBits(imageInput: BufferedImage, name: String) {
    val pixelSize = imageInput.colorModel.pixelSize
    if (!(pixelSize == 24 || pixelSize == 32)) {
        throw RuntimeException("The $name isn't 24 or 32-bit.")
    }
}

private fun dimensionsFilesEquals(imageInput: BufferedImage, imageWatermark: BufferedImage) {
    val squareInput = imageInput.width * imageInput.height
    val squareWatermark = imageWatermark.width * imageWatermark.height
    if (squareInput < squareWatermark
        || imageInput.width < imageWatermark.width
        || imageInput.height < imageWatermark.height
    ) {
        throw RuntimeException("The watermark's dimensions are larger.")
    }
}

private fun saveFile(outputFile: String, tuneColor: BufferedImage) {
    val fileJpg = File(outputFile)
    when {
        outputFile.endsWith(".jpg") -> {
            ImageIO.write(tuneColor, "jpg", fileJpg)
            println("The watermarked image $outputFile has been created.")
        }

        outputFile.endsWith(".png") -> {
            ImageIO.write(tuneColor, "png", fileJpg)
            println("The watermarked image $outputFile has been created.")
        }

        else -> throw RuntimeException("The output file extension isn't \"jpg\" or \"png\".")
    }
}

private fun checkAlpha(image: BufferedImage) = image.transparency == Transparency.TRANSLUCENT

private fun isUseAlpha(): Boolean {
    println("Do you want to use the watermark's Alpha channel?")
    return isAllow(readln())
}

private fun isAllow(alpha: String): Boolean {
    return when (alpha.lowercase()) {
        "yes" -> true
        "no" -> false
        else -> throw Exception("")
    }
}

private fun tuneColor(
    imageInput: BufferedImage,
    imageWater: BufferedImage,
    transparencyPercentage: Int,
    checkAlphaS: Boolean = false,
    transparencyColor: Color?,
    position: Position?
): BufferedImage {
    val width = imageInput.width
    val height = imageInput.height
    val widthW = imageWater.width
    val heightW = imageWater.height
    val newImage: BufferedImage

    if (position != null) {
        if (checkAlphaS) {
            newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val i = Color(imageInput.getRGB(x, y))
                    val w = if (x > position.x && y > position.y
                        && x < position.x + widthW
                        && y < position.y + heightW
                    ) {
                        Color(imageWater.getRGB(x - position.x , y - position.y ), true)
                    } else i
                    if (w.alpha == 0) {
                        newImage.setRGB(x, y, i.rgb)
                    } else {
                        createNewImage(transparencyPercentage, i, w, newImage, x, y)
                    }
                }
            }
        } else {
            newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            if (transparencyColor != null) {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val i = Color(imageInput.getRGB(x, y))
                        val w = if (x > position.x && y > position.y
                            && x < position.x + widthW
                            && y < position.y + heightW
                        ) {
                            Color(imageWater.getRGB(x % widthW, y % heightW))
                        } else i
                        val color = if (w.rgb == transparencyColor.rgb) {
                            Color(i.red, i.green, i.blue, 0)
                        } else w
                        createNewImage(transparencyPercentage, i, color, newImage, x, y)
                    }
                }
            } else {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val i = Color(imageInput.getRGB(x, y))
                        val w = if (x > position.x && y > position.y
                            && x + position.x + widthW < width
                            && y + position.y + heightW < height
                        ) {
                            Color(imageWater.getRGB(x % widthW, y % heightW))
                        } else i
                        createNewImage(transparencyPercentage, i, w, newImage, x, y)
                    }
                }
            }
        }
    } else {
        if (checkAlphaS) {
            newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val i = Color(imageInput.getRGB(x, y))
                    val w = Color(imageWater.getRGB(x % widthW, y % heightW), true)
                    if (w.alpha == 0) {
                        newImage.setRGB(x, y, i.rgb)
                    } else {
                        createNewImage(transparencyPercentage, i, w, newImage, x, y)
                    }
                }
            }
        } else {
            newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            if (transparencyColor != null) {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val i = Color(imageInput.getRGB(x, y))
                        val w = Color(imageWater.getRGB(x % widthW, y % heightW))
                        val color = if (w.rgb == transparencyColor.rgb) {
                            Color(i.red, i.green, i.blue, 0)
                        } else w
                        createNewImage(transparencyPercentage, i, color, newImage, x, y)
                    }
                }
            } else {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val i = Color(imageInput.getRGB(x, y))
                        val w = Color(imageWater.getRGB(x % widthW, y % heightW))
                        createNewImage(transparencyPercentage, i, w, newImage, x, y)
                    }
                }
            }
        }
    }
    return newImage
}

private fun createNewImage(
    transparencyPercentage: Int,
    i: Color,
    w: Color,
    newImage: BufferedImage,
    x: Int,
    y: Int
) {
    val color = Color(
        (transparencyPercentage * w.red + (100 - transparencyPercentage) * i.red) / 100,
        (transparencyPercentage * w.green + (100 - transparencyPercentage) * i.green) / 100,
        (transparencyPercentage * w.blue + (100 - transparencyPercentage) * i.blue) / 100
    )
    newImage.setRGB(x, y, color.rgb)
}
