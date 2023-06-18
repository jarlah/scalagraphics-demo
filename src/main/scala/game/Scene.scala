package game
import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.{Dimension, Graphics, Image}

trait Scene {
  def update(delta: Double): Unit
  def render: GraphicsOp[Unit]
  def onResize(newWidth: Int, newHeight: Int): Unit

  def onEnter(): Unit

  def onExit(): Unit
}

class SceneManager(keyManager: GameKeyManager) extends KeyAdapter {

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
}

class WelcomeScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneManager: SceneManager) extends Scene {
  private var image: Image = assetManager.getImage("welcome.jpeg")
  override def render: GraphicsOp[Unit] = for {
    _ <- drawImage(image, 0, 0)
    _ <- drawString("Welcome to the game!", 100, 100)
  } yield ()

  override def update(delta: Double): Unit = {
    if (keyManager.isKeyJustPressed(GameKey.ENTER)) {
      println("Clicked ENTER")
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = {
    image = assetManager.getScaledImage("welcome.jpeg", new Dimension(newWidth, newHeight))
  }

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
