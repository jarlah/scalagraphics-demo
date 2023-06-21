package com.github.jarlah.scalagraphics

import javax.imageio.ImageIO
import java.awt.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util
import scala.collection.mutable
import scala.util.Try

class AssetManager {
  private val assets = mutable.Map[String, Image]()

  def getImage(assetName: String): Image = {
    if (!assets.contains(assetName)) {
      assets.put(assetName, loadImage(assetName))
    }
    assets(assetName)
  }

  def getScaledImage(assetName: String, dimension: Dimension): Image = {
    val image = getImage(assetName)
    val originalSize =
      new Dimension(image.getWidth(null), image.getHeight(null))
    val newSize = getScaledDimension(originalSize, dimension)
    image.getScaledInstance(newSize.width, newSize.height, Image.SCALE_SMOOTH)
  }

  private def getScaledDimension(imageSize: Dimension, boundary: Dimension) = {
    val widthRatio = boundary.getWidth / imageSize.getWidth
    val heightRatio = boundary.getHeight / imageSize.getHeight
    val ratio = Math.max(widthRatio, heightRatio)
    new Dimension(
      (imageSize.width * ratio).toInt,
      (imageSize.height * ratio).toInt
    )
  }

  private def loadImage(assetName: String) = Try(
    ImageIO.read(
      getClass.getClassLoader.getResourceAsStream("assets/" + assetName)
    )
  ).getOrElse(null)
}
