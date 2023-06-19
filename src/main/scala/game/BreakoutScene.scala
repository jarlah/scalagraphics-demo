package game

import java.awt.Color
import java.util.Timer

object BreakoutScene {
  trait Position {
    val x: Double
    val y: Double
  }

  trait RectangularShape extends Position {
    val width: Double
    val height: Double

    def left: Double = x
    def right: Double = x + width
    def top: Double = y
    def bottom: Double = y + height

    def intersects(other: RectangularShape): Boolean = {
      if (this.right < other.left || this.left > other.right) return false
      if (this.bottom < other.top || this.top > other.bottom) return false
      true
    }
  }

  case class Paddle(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      speed: Double
  ) extends Position
      with RectangularShape {

    def render: GraphicsOp[Unit] =
      drawRect(x.toInt, y.toInt, width.toInt, height.toInt)
  }

  case class Ball(
      x: Double,
      y: Double,
      radius: Int,
      speedX: Double,
      speedY: Double,
      moving: Boolean
  ) extends Position
      with RectangularShape {
    override val width: Double = radius * 2
    override val height: Double = radius * 2

    // Override the bounds to account for center positioning
    override def left: Double = x - radius
    override def right: Double = x + radius
    override def top: Double = y - radius
    override def bottom: Double = y + radius

    def render: GraphicsOp[Unit] =
      drawOval((x - radius).toInt, (y - radius).toInt, radius * 2, radius * 2)
  }

  case class Brick(
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      visible: Boolean
  ) extends Position
      with RectangularShape {

    def render: GraphicsOp[Unit] =
      drawRect(x.toInt, y.toInt, width.toInt, height.toInt)
  }
}

case class BreakoutScene(
    assetManager: AssetManager,
    keyManager: GameKeyManager,
    sceneUtils: SceneUtils
) extends Scene {

  import BreakoutScene._

  val timer = new Timer()

  var paddle: Paddle =
    Paddle(sceneUtils.width / 2 - 80 / 2, sceneUtils.height - 50, 80, 10, 5)
  var ball: Ball =
    Ball(paddle.x + paddle.width / 2, paddle.y - 10, 5, 3, -3, false)
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

  override def render: GraphicsOp[Unit] = for {
    _ <- clearRect(0, 0, sceneUtils.width, sceneUtils.height)
    _ <- setColor(Color.BLACK)
    _ <- paddle.render
    _ <- ball.render
    _ <- bricks.flatten.foldLeft(GraphicsOp.pure(())) { (acc, brick) =>
      acc.flatMap(_ => brick.render)
    }
  } yield ()

  def update(delta: Double): Unit = {

    if (keyManager.isKeyJustPressed(GameKey.SPACE)) {
      ball = ball.copy(moving = !ball.moving)
    }

    // if ball is not moving, do nothing
    if (!ball.moving) return;

    // Move paddle
    if (keyManager.isKeyPressed(GameKey.LEFT)) {
      paddle = paddle.copy(x = (paddle.x - paddle.speed * delta).max(0))
    }

    if (keyManager.isKeyPressed(GameKey.RIGHT)) {
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
    if (ball.x - ball.radius < 0 || ball.x + ball.radius > sceneUtils.width) {
      ball = ball.copy(
        x = oldBall.x, // revert x
        speedX = -ball.speedX
      )
    }
    if (ball.y - ball.radius < 0 || ball.y + ball.radius > sceneUtils.height) {
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

    // Handle ball going out of bounds
    if (ball.y + ball.radius * 2 > sceneUtils.height) {
      paddle = Paddle(
        sceneUtils.width / 2 - paddle.width / 2,
        sceneUtils.height - 50,
        80,
        10,
        5
      )
      ball = Ball(paddle.x + paddle.width / 2, paddle.y - 10, 5, 3, -3, false)
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
