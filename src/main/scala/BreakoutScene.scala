package com.github.jarlah.scalagraphics

import java.awt.{Dimension, Color as AwtColor}
import java.util.Timer
import GraphicsIO.*

object BreakoutScene {
  trait Position {
    val x: Double
    val y: Double
  }

  trait RectangularShape extends Position {
    val width: Double
    val height: Double
    val backgroundColor: Color
    val fill: Boolean = false

    def left: Double = x
    def right: Double = x + width
    def top: Double = y
    def bottom: Double = y + height

    def intersects(other: RectangularShape): Boolean = {
      if (this.right < other.left || this.left > other.right) return false
      if (this.bottom < other.top || this.top > other.bottom) return false
      true
    }

    def render: GraphicsIO[Unit] = {
      for {
        previousColor <- getColor
        _ <- setColor(backgroundColor)
        _ <-
          if (fill) fillRect(x.toInt, y.toInt, width.toInt, height.toInt)
          else
            drawRect(
              x.toInt,
              y.toInt,
              width.toInt,
              height.toInt
            )
        _ <- setColor(previousColor)
      } yield ()
    }
  }

  case class Paddle(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      speed: Double,
      backgroundColor: Color = Orange
  ) extends RectangularShape {
    override val fill: Boolean = true
  }

  case class Ball(
      x: Double,
      y: Double,
      radius: Int,
      speedX: Double,
      speedY: Double,
      moving: Boolean,
      started: Boolean = false,
      backgroundColor: Color = Red
  ) extends RectangularShape {
    override val width: Double = radius * 2
    override val height: Double = radius * 2

    // Override the bounds to account for center positioning
    override def left: Double = x - radius
    override def right: Double = x + radius
    override def top: Double = y - radius
    override def bottom: Double = y + radius

    override def render: GraphicsIO[Unit] = {
      for {
        previousColor <- getColor
        _ <- setColor(backgroundColor)
        _ <- fillOval(
          (x - radius).toInt,
          (y - radius).toInt,
          radius * 2,
          radius * 2
        )
        _ <- setColor(previousColor)
      } yield ()
    }
  }

  case class Brick(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      visible: Boolean,
      backgroundColor: Color = White
  ) extends RectangularShape

  case class Wall(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      backgroundColor: Color = White
  ) extends RectangularShape
}

case class BreakoutScene(
    assetManager: AssetManager,
    keyManager: GameKeyManager,
    sceneUtils: SceneUtils
) extends Scene {

  import BreakoutScene._

  val timer = new Timer()

  val background = assetManager.getScaledImage(
    "welcome.jpeg",
    new Dimension(sceneUtils.width, sceneUtils.height)
  )
  var leftWall = Wall(0, 0, 1, sceneUtils.height)
  var rightWall = Wall(sceneUtils.width - 1, 0, 1, sceneUtils.height)
  var topWall = Wall(0, 0, sceneUtils.width, 1)
  var bottomWall =
    Wall(0, sceneUtils.height + 1, sceneUtils.width, 1)
  var paddle = createNewPaddle()
  var ball = createNewBall(paddle)
  var bricks: Array[Array[Brick]] = Array.fill(10, 5)(
    Brick(0, 0, 80, 20, true)
  ) // 10 columns and 5 rows of bricks

  // Initialize bricks position
  for {
    i <- bricks.indices
    j <- bricks(i).indices
  } {
    bricks(i)(j) = bricks(i)(j).copy(x = i * 80, y = j * 20)
  }

  override def render: GraphicsIO[Unit] = for {
    previousColor <- getColor
    previousFont <- getFont
    _ <- setColor(DarkGray)
    _ <- fillRect(0, 0, sceneUtils.width, sceneUtils.height)
    _ <- paddle.render
    _ <- ball.render
    _ <- bricks.flatten.foldLeft(pure(())) { (acc, brick) =>
      acc.flatMap(_ => brick.render)
    }
    _ <- setFont(previousFont)
    _ <- setColor(previousColor)
  } yield ()

  def update(delta: Double): Unit = {

    if (keyManager.isKeyJustPressed(GameKey.Space)) {
      ball = ball.copy(moving = !ball.moving)
      if (!ball.started) ball = ball.copy(started = true)
    }

    // if ball is not moving, do nothing
    if (!ball.moving) return;

    // Move paddle
    if (keyManager.isKeyPressed(GameKey.Left)) {
      paddle = paddle.copy(x = (paddle.x - paddle.speed * delta).max(0))
    }

    if (keyManager.isKeyPressed(GameKey.Right)) {
      paddle = paddle.copy(x =
        (paddle.x + paddle.speed * delta).min(sceneUtils.width - paddle.width)
      )
    }

    val oldBall = ball

    ball = ball.copy(
      x = ball.x + ball.speedX * delta,
      y = ball.y + ball.speedY * delta
    )

    // Handle ball-wall collision
    if (ball.intersects(leftWall) || ball.intersects(rightWall)) {
      ball = ball.copy(
        x = oldBall.x, // revert x
        speedX = -ball.speedX
      )
    }
    if (ball.intersects(topWall)) {
      ball = ball.copy(
        y = oldBall.y, // revert y
        speedY = -ball.speedY
      )
    }

    // Handle ball-paddle collision
    if (ball.intersects(paddle)) {
      ball = ball.copy(
        x = oldBall.x, // revert x
        y = paddle.y - 10,
        speedY = -ball.speedY
      )
    }

    // Handle ball-brick collision
    bricks = bricks.map(_.flatMap { brick =>
      if (ball.intersects(brick)) {
        // collision detected
        ball = ball.copy(
          x = oldBall.x, // revert x
          y = oldBall.y, // revert y
          speedY = -ball.speedY
        )
        None // Remove brick
      } else {
        Some(brick) // Keep brick
      }
    })

    // Handle ball hitting flor
    if (ball.intersects(bottomWall)) {
      paddle = createNewPaddle()
      ball = createNewBall(paddle)
    }
  }

  private def createNewPaddle(
      width: Double = 80,
      height: Double = 10,
      speed: Double = 5,
      heightOffset: Double = 50
  ) = {
    Paddle(
      sceneUtils.width / 2 - width / 2,
      sceneUtils.height - heightOffset,
      width,
      height,
      speed
    )
  }

  private def createNewBall(
      paddle: RectangularShape,
      heightOffset: Double = 10,
      radius: Int = 5,
      speedX: Double = 3,
      speedY: Double = -3,
      moving: Boolean = false,
      started: Boolean = false
  ) =
    Ball(
      paddle.x + paddle.width / 2,
      paddle.y - heightOffset,
      radius,
      speedX,
      speedY,
      moving
    )

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
