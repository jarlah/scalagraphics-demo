package com.github.jarlah.scalagraphics

import java.awt.{FontMetrics, Color as AwtColor, Font as AwtFont}
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

case class Point(x: Int, y: Int)

case class Snake(points: List[Point], direction: String) {
  def move(gridWidth: Int, gridHeight: Int): Snake = {
    val head = points.head
    val newHead = direction match {
      case "UP"    => Point(head.x, (head.y - 1 + gridHeight) % gridHeight)
      case "DOWN"  => Point(head.x, (head.y + 1 + gridHeight) % gridHeight)
      case "LEFT"  => Point((head.x - 1 + gridWidth) % gridWidth, head.y)
      case "RIGHT" => Point((head.x + 1 + gridWidth) % gridWidth, head.y)
    }
    val newPoints = newHead :: points.init // Copy the tail

    Snake(newPoints, direction)
  }

  def eatApple(apple: Point): Snake = Snake(apple :: points, direction)

  def isCollidingWithSelf: Boolean = points.tail.contains(points.head)
}

case class SnakeScene(
    assetManager: AssetManager,
    gameKeyManager: GameKeyManager,
    sceneUtils: SceneUtils
) extends Scene {

  private var snake: Snake = Snake(List(Point(10, 10)), "RIGHT")
  private var apple: Point = Point(20, 20)

  private var timeSinceLastMove: Double = 0.0
  private var moveInterval: Double = 20.0

  private var gameOver: Boolean = false
  private var gamePaused: Boolean = false

  private val gridSize = 10
  private val gridWidth = sceneUtils.width / gridSize
  private val gridHeight = sceneUtils.height / gridSize

  private val gameOverString = "Game Over"
  private val gameOverFont = Font("Arial", 48, FontStyle.unsafeFromInt(java.awt.Font.BOLD))
  private val gameOverColor = Red

  private val gamePausedString = "Game Paused"
  private val gamePausedFont = Font("Arial", 48, FontStyle.unsafeFromInt(java.awt.Font.BOLD))
  private val gamePausedColor = Green

  private val scoreFont = Font("Arial", 16, FontStyle.unsafeFromInt(java.awt.Font.BOLD))
  private val scoreColor = Black
  private var score: Int = 0

  private val speedFont = Font("Arial", 16, FontStyle.unsafeFromInt(java.awt.Font.BOLD))
  private val speedColor = Black

  override def update(delta: Double): Unit = {
    if (gameOver) return;

    if (gameKeyManager.isKeyJustPressed(GameKey.Escape))
      gamePaused = !gamePaused

    if (gamePaused) return;

    if (gameKeyManager.isKeyPressed(GameKey.Up) && snake.direction != "DOWN")
      snake = snake.copy(direction = "UP")
    if (gameKeyManager.isKeyPressed(GameKey.Down) && snake.direction != "UP")
      snake = snake.copy(direction = "DOWN")
    if (gameKeyManager.isKeyPressed(GameKey.Left) && snake.direction != "RIGHT")
      snake = snake.copy(direction = "LEFT")
    if (gameKeyManager.isKeyPressed(GameKey.Right) && snake.direction != "LEFT")
      snake = snake.copy(direction = "RIGHT")

    timeSinceLastMove += delta

    if (timeSinceLastMove >= moveInterval) {
      snake = snake.move(gridWidth, gridHeight)

      if (snake.isCollidingWithSelf) {
        gameOver = true
      } else if (snake.points.head == apple) {
        snake = snake.eatApple(apple) // Generate a new apple
        apple = generateApple
        moveInterval *= 0.9 // Increase speed by reducing the move interval
        score += 1 // Increment the score
      }

      timeSinceLastMove = 0.0 // Reset the time since the last move
    }
  }

  @tailrec
  final def generateApple: Point = {
    val newApple = Point(
      Random.nextInt(sceneUtils.width / gridSize),
      Random.nextInt(sceneUtils.height / gridSize)
    )
    if (snake.points.contains(newApple)) generateApple else newApple
  }

  // Render the game scene
  override def render: GraphicsIO[Unit] = {
    for {
      previousColor <- getColor
      // Clear the screen
      _ <- setColor(DarkGray)
      _ <- fillRect(0, 0, sceneUtils.width, sceneUtils.height)
      // Conditionally draw game over string
      _ <-
        if (gameOver) {
          for {
            previousFont <- getFont
            _ <- setColor(gameOverColor)
            _ <- setFont(gameOverFont)
            _ <- drawString(gameOverString, sceneUtils.width / 2, sceneUtils.height / 2)
            _ <- setFont(previousFont)
          } yield ()
        } else {
          pure(())
        }
      _ <-
        if (!gameOver && gamePaused) {
          drawLine(0, 0, sceneUtils.width, sceneUtils.height)
          for {
            previousFont <- getFont
            _ <- setColor(gamePausedColor)
            _ <- setFont(gamePausedFont)
            _ <- drawString(gamePausedString, sceneUtils.width / 2, sceneUtils.height / 2)
            _ <- setFont(previousFont)
          } yield ()
        } else {
          pure(())
        }
      previousFont <- getFont
      // Display the score
      _ <- setColor(scoreColor)
      _ <- setFont(scoreFont)
      _ <- drawString(s"Score: $score", 10, 20)
      _ <- setFont(previousFont)
      // Display the speed
      _ <- setColor(speedColor)
      _ <- setFont(speedFont)
      _ <- drawString(
        f"Speed: ${1000.0 / moveInterval}%.1f px/s",
        sceneUtils.width - 140,
        20
      )
      // Revert the font
      _ <- setFont(previousFont)
      // Draw the snake
      _ <- setColor(Green)
      _ <- snake.points.foldLeft(pure(())) { case (acc, Point(x, y)) =>
        acc.flatMap(_ =>
          fillRect(x * gridSize, y * gridSize, gridSize, gridSize)
        )
      }
      _ <- setColor(Red)
      // Draw the apple
      _ <- fillRect(
        apple._1 * gridSize,
        apple._2 * gridSize,
        gridSize,
        gridSize
      )
      // Revert the color
      _ <- setColor(previousColor)
    } yield ()
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
