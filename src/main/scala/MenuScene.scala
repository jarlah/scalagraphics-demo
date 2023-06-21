package com.github.jarlah.scalagraphics

import GraphicsOp.{
  clearRect,
  drawString,
  fillRect,
  getColor,
  getFont,
  setColor,
  setFont
}

import java.awt.{Color, Font}
import scala.collection.mutable.ArrayBuffer

sealed trait SceneOption {
  def name: String
  def scene: Scene
}

case class BreakoutSceneOption(name: String = "Breakout", scene: BreakoutScene)
    extends SceneOption
case class SnakeSceneOption(name: String = "Snake", scene: SnakeScene)
    extends SceneOption

case class MenuScene(
    assetManager: AssetManager,
    gameKeyManager: GameKeyManager,
    sceneUtils: SceneUtils
) extends Scene {

  private val titleFont = new Font("Arial", Font.BOLD, 32)
  private val optionFont = new Font("Arial", Font.PLAIN, 24)

  private val options: Array[SceneOption] = Array(
    BreakoutSceneOption(scene =
      BreakoutScene(assetManager, gameKeyManager, sceneUtils)
    ),
    SnakeSceneOption(scene =
      SnakeScene(assetManager, gameKeyManager, sceneUtils)
    )
  )

  private var selectedIndex: Int = 0

  override def update(delta: Double): Unit = {
    if (gameKeyManager.isKeyJustPressed(GameKey.Up)) {
      selectedIndex = (selectedIndex - 1 + options.length) % options.length
    }
    if (gameKeyManager.isKeyJustPressed(GameKey.Down)) {
      selectedIndex = (selectedIndex + 1) % options.length
    }
    if (gameKeyManager.isKeyJustPressed(GameKey.Enter)) {
      val selectedOption = options(selectedIndex)
      sceneUtils.setScene(selectedOption.scene)
    }
  }

  override def render: GraphicsOp[Unit] = {
    for {
      previousFont <- getFont
      previousColor <- getColor
      _ <- setColor(convertColor(Color.DARK_GRAY))
      _ <- fillRect(0, 0, sceneUtils.width, sceneUtils.height)

      // Render title
      _ <- setColor(convertColor(Color.WHITE))
      _ <- setFont(titleFont)
      _ <- drawString("Game Menu", 20, 50)

      // Render options
      _ <- setFont(optionFont)
      optionsWithIndex = options.zipWithIndex
      _ <- optionsWithIndex.foldLeft(GraphicsOp.pure(())) {
        case (acc, (option, index)) =>
          val x = 50
          val y = 100 + index * 30
          acc.flatMap(_ => {
            if (index == selectedIndex) {
              setColor(convertColor(Color.YELLOW)).flatMap(_ =>
                drawString(s"> ${option.name}", x, y)
              )
            } else {
              setColor(convertColor(Color.WHITE)).flatMap(_ => drawString(option.name, x, y))
            }
          })
      }

      _ <- setColor(convertColor(previousColor))
      _ <- setFont(previousFont)
    } yield ()
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
