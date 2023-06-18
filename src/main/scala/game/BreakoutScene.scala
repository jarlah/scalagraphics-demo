package game

import java.util.Timer

case class BreakoutScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneUtils: SceneUtils) extends Scene {

  val timer = new Timer()

  private case class Paddle(x: Int, y: Int, width: Int, height: Int, speed: Int)
  private case class Ball(x: Int, y: Int, radius: Int, speedX: Int, speedY: Int, moving: Boolean)
  private case class Brick(x: Int, y: Int, width: Int, height: Int, visible: Boolean)

  private var paddle: Paddle = Paddle(sceneUtils.width / 2, sceneUtils.height - 50, 80, 10, 5)
  private var ball: Ball = Ball(paddle.x + paddle.width / 2, paddle.y - 10, 10, 3, -3, false)
  private var bricks = Array.fill(10, 5)(Brick(0, 0, 80, 20, true)) // 10 columns and 5 rows of bricks

  // Initialize bricks position
  for {
    i <- bricks.indices
    j <- bricks(i).indices
  } {
    bricks(i)(j) = bricks(i)(j).copy(x = i * 80, y = j * 20)
  }

  override def render: GraphicsOp[Unit] = for {
    _ <- clearRect(0, 0, sceneUtils.width, sceneUtils.height)
    _ <- drawRect(paddle.x, paddle.y, paddle.width, paddle.height) // assume drawRect exists
    _ <- drawOval(ball.x, ball.y, ball.radius, ball.radius)
    _ <- bricks.flatten.foldLeft(GraphicsOp.pure(())) { (acc, brick) =>
      acc.flatMap(_ => if (brick.visible) drawRect(brick.x, brick.y, brick.width, brick.height) else GraphicsOp.pure(()))
    }
  } yield ()


  def update(delta: Double): Unit = {

    if (keyManager.isKeyJustPressed(GameKey.SPACE)) {
      ball = ball.copy(moving = !ball.moving)
    }

    // Move ball
    if (ball.moving) {
      // Move paddle
      if (keyManager.isKeyPressed(GameKey.LEFT)) {
        paddle = paddle.copy(x = (paddle.x - paddle.speed * delta).toInt.max(0))
      }
      if (keyManager.isKeyPressed(GameKey.RIGHT)) {
        paddle = paddle.copy(x = (paddle.x + paddle.speed * delta).toInt.min(sceneUtils.width - paddle.width))
      }
      ball = ball.copy(x = (ball.x + ball.speedX * delta).toInt, y = (ball.y + ball.speedY * delta).toInt)

      // Handle ball-wall collision
      if (ball.x < 0 || ball.x + ball.radius * 2 > sceneUtils.width)
        ball = ball.copy(speedX = -ball.speedX)

      if (ball.y < 0)
        ball = ball.copy(speedY = -ball.speedY)

      // Handle ball-paddle collision
      if (ball.y + ball.radius >= paddle.y &&
        ball.y <= paddle.y + paddle.height &&
        ball.x + ball.radius >= paddle.x &&
        ball.x <= paddle.x + paddle.width)
        ball = ball.copy(speedY = -ball.speedY)

      // Handle ball-brick collision
      bricks = bricks.map(_.flatMap { brick =>
        if (ball.y + ball.radius >= brick.y &&
          ball.y <= brick.y + brick.height &&
          ball.x + ball.radius >= brick.x &&
          ball.x <= brick.x + brick.width) {
          // collision detected
          ball = ball.copy(speedY = -ball.speedY)
          None // Remove brick
        } else {
          Some(brick) // Keep brick
        }
      })

      // Handle ball going out of bounds
      if (ball.y + ball.radius * 2 > sceneUtils.height) {
        paddle = Paddle(sceneUtils.width / 2, sceneUtils.height - 50, 80, 10, 5)
        ball = Ball(paddle.x + paddle.width / 2, paddle.y - 10, 10, 3, -3, false)
      }
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}