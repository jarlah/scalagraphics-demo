package game

import game.GraphicsOp.{drawImage, drawString}

import java.awt.{Dimension, Image}

case class MenuScene(
    assetManager: AssetManager,
    keyManager: GameKeyManager,
    sceneUtils: SceneUtils
) extends Scene {
  private var image: Image = assetManager.getImage("welcome.jpeg")
  override def render: GraphicsOp[Unit] = for {
    _ <- drawImage(image, 0, 0)
    _ <- drawString("Welcome to the game!", 100, 100)
  } yield ()

  override def update(delta: Double): Unit = {
    if (keyManager.isKeyJustPressed(GameKey.ENTER)) {
      sceneUtils.setScene(
        new BreakoutScene(assetManager, keyManager, sceneUtils)
      )
    }
  }

  override def onResize(newWidth: Int, newHeight: Int): Unit = {
    image = assetManager.getScaledImage(
      "welcome.jpeg",
      new Dimension(newWidth, newHeight)
    )
  }

  override def onEnter(): Unit = onResize(sceneUtils.width, sceneUtils.height)

  override def onExit(): Unit = ()
}
