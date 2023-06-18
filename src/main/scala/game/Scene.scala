package game
import java.awt.event.{ComponentEvent, ComponentListener, KeyAdapter, KeyEvent, KeyListener}
import java.awt.{Dimension, Graphics, Image}
import java.util.{Timer, TimerTask}

trait Scene {
  def update(delta: Double): Unit
  def render: GraphicsOp[Unit]
  def onResize(newWidth: Int, newHeight: Int): Unit

  def onEnter(): Unit

  def onExit(): Unit
}

trait SceneUtils {
  def setScene(scene: Scene): Unit
  def width: Int
  def height: Int
}

case class SceneManager(keyManager: GameKeyManager, var width: Int, var height: Int) extends KeyListener, ComponentListener, SceneUtils {

  private var currentScene: Scene = _

  def setScene(scene: Scene): Unit = {
    if (currentScene != null) currentScene.onExit()
    currentScene = scene
    if (currentScene != null) currentScene.onEnter()
  }

  def render: GraphicsOp[Unit] = {
    currentScene.render
  }

  def update(delta: Double): Unit = {
    keyManager.update()
    currentScene.update(delta)
  }

  def onResize(newWidth: Int, newHeight: Int): Unit = {
    currentScene.onResize(newWidth, newHeight)
  }

  override def keyTyped(e: KeyEvent): Unit = {
    keyManager.keyTyped(e)
  }

  override def keyPressed(e: KeyEvent): Unit = {
    keyManager.keyPressed(e)
  }

  override def keyReleased(e: KeyEvent): Unit = {
    keyManager.keyReleased(e)
  }

  override def componentResized(e: ComponentEvent): Unit = {
    width = e.getComponent.getWidth
    height = e.getComponent.getHeight
    currentScene.onResize(width, height)
  }

  override def componentMoved(e: ComponentEvent): Unit = ()

  override def componentShown(e: ComponentEvent): Unit = {
    currentScene.onExit()
  }

  override def componentHidden(e: ComponentEvent): Unit = {
    currentScene.onEnter()
  }

  def getCurrentScene: Scene = currentScene
}

case class WelcomeScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneUtils: SceneUtils) extends Scene {
  private var image: Image = assetManager.getImage("welcome.jpeg")
  override def render: GraphicsOp[Unit] = for {
    _ <- drawImage(image, 0, 0)
    _ <- drawString("Welcome to the game!", 100, 100)
  } yield ()

  override def update(delta: Double): Unit = {
    if (keyManager.isKeyJustPressed(GameKey.ENTER)) {
      sceneUtils.setScene(new BreakoutScene(assetManager, keyManager, sceneUtils))
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = {
    image = assetManager.getScaledImage("welcome.jpeg", new Dimension(newWidth, newHeight))
  }

  override def onEnter(): Unit = onResize(sceneUtils.width, sceneUtils.height)

  override def onExit(): Unit = ()
}

case class BreakoutScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneUtils: SceneUtils) extends Scene {

  val timer = new Timer()

  private case class Paddle(x: Int, y: Int, width: Int, height: Int, speed: Int)
  private case class Ball(x: Int, y: Int, radius: Int, speedX: Int, speedY: Int)
  private case class Brick(x: Int, y: Int, width: Int, height: Int, visible: Boolean)

  private var paddle: Paddle = Paddle(sceneUtils.width / 2, sceneUtils.height - 50, 80, 10, 5)
  private var ball: Option[Ball] = None
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
    _ <- ball match {
      case Some(b) => drawOval(b.x, b.y, b.radius, b.radius)
      case None => GraphicsOp.pure(())
    }
    _ <- bricks.flatten.foldLeft(GraphicsOp.pure(())) { (acc, brick) =>
      acc.flatMap(_ => if (brick.visible) drawRect(brick.x, brick.y, brick.width, brick.height) else GraphicsOp.pure(()))
    }
  } yield ()


  def update(delta: Double): Unit = {
    // Move paddle
    if (keyManager.isKeyPressed(GameKey.LEFT)) {
      paddle = paddle.copy(x = (paddle.x - paddle.speed * delta).toInt.max(0))
    }
    if (keyManager.isKeyPressed(GameKey.RIGHT)) {
      paddle = paddle.copy(x = (paddle.x + paddle.speed * delta).toInt.min(sceneUtils.width - paddle.width))
    }

    // Move ball
    ball = ball.map(b => b.copy(x = (b.x + b.speedX * delta).toInt, y = (b.y + b.speedY * delta).toInt))

    // Handle ball-wall collision
    ball = ball.map(b =>
      if (b.x < 0 || b.x + b.radius * 2 > sceneUtils.width)
        b.copy(speedX = -b.speedX)
      else
        b
    )

    ball = ball.map(b =>
      if (b.y < 0)
        b.copy(speedY = -b.speedY)
      else
        b
    )

    // Handle ball-paddle collision
    ball = ball.map(b =>
      if (b.y + b.radius >= paddle.y &&
        b.y <= paddle.y + paddle.height &&
        b.x + b.radius >= paddle.x &&
        b.x <= paddle.x + paddle.width)
        b.copy(speedY = -b.speedY)
      else
        b
    )

    // Handle ball-brick collision
    ball.foreach { b =>
      bricks = bricks.map(_.flatMap { brick =>
        if (b.y + b.radius >= brick.y &&
          b.y <= brick.y + brick.height &&
          b.x + b.radius >= brick.x &&
          b.x <= brick.x + brick.width) {
          // collision detected
          ball = Some(b.copy(speedY = -b.speedY))
          None // Remove brick
        } else {
          Some(brick) // Keep brick
        }
      })
    }

    // Handle ball going out of bounds
    if (ball.exists(b => b.y + b.radius * 2 > sceneUtils.height)) {
      ball = None // Remove the ball from the game
      timer.schedule(new TimerTask {
        def run(): Unit = {
          // Reset ball to initial position
          ball = Some(Ball(sceneUtils.width / 2, sceneUtils.height / 2, 10, 3, 3))
        }
      }, 3000) // Schedule ball reset in 3 seconds
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = {
    // delay the ball's movement
    new java.util.Timer().schedule(
      new java.util.TimerTask {
        override def run(): Unit = ball = Some(Ball(sceneUtils.width / 2, sceneUtils.height / 2, 10, 3, 3))
      },
      3000 // delay for 3 seconds
    )
  }

  override def onExit(): Unit = ()
}
