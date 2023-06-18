package game
import java.awt.event.{ComponentEvent, ComponentListener, KeyAdapter, KeyEvent, KeyListener}
import java.awt.{Dimension, Graphics, Image}

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

class SceneManager(keyManager: GameKeyManager, var width: Int, var height: Int) extends KeyListener, ComponentListener, SceneUtils {

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
}

class WelcomeScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneUtils: SceneUtils) extends Scene {
  private var image: Image = assetManager.getImage("welcome.jpeg")
  override def render: GraphicsOp[Unit] = for {
    _ <- drawImage(image, 0, 0)
    _ <- drawString("Welcome to the game!", 100, 100)
  } yield ()

  override def update(delta: Double): Unit = {
    if (keyManager.isKeyJustPressed(GameKey.ENTER)) {
      sceneUtils.setScene(new GameScene(assetManager, keyManager, sceneUtils))
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = {
    image = assetManager.getScaledImage("welcome.jpeg", new Dimension(newWidth, newHeight))
  }

  override def onEnter(): Unit = onResize(sceneUtils.width, sceneUtils.height)

  override def onExit(): Unit = ()
}

class GameScene(assetManager: AssetManager, keyManager: GameKeyManager, sceneUtils: SceneUtils) extends Scene {
  override def render: GraphicsOp[Unit] = for {
    _ <- clearRect(0, 0, sceneUtils.width, sceneUtils.height)
    _ <- drawString("THIS IS THE GAME", 200, 200)
  } yield ()

  override def update(delta: Double): Unit = {
    if (keyManager.isKeyJustPressed(GameKey.ENTER)) {
      sceneUtils.setScene(new WelcomeScene(assetManager, keyManager, sceneUtils))
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = ()

  override def onEnter(): Unit = ()

  override def onExit(): Unit = ()
}
