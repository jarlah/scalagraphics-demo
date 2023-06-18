package game

import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener, KeyListener}
import java.awt.{Dimension, Image}
import javax.swing.{ImageIcon, JFrame, WindowConstants}

@main
def main(): Unit = {
  val assetManager = new AssetManager
  val keyManager = new GameKeyManager
  val sceneManager = new SceneManager(keyManager)

  sceneManager.setScene(new WelcomeScene(assetManager, keyManager, sceneManager))

  val frame = createGameWindow(sceneManager)
  val ticker = new Ticker(
    sceneManager.update,
    render(frame, sceneManager)
  )

  ticker.start()

  sys.addShutdownHook(ticker.stop())
}

def render(frame: JFrame, sceneManager: SceneManager): Unit = {
  val bs = frame.getBufferStrategy
  val g = bs.getDrawGraphics
  sceneManager.render.run(new GraphicsIOWrapper(g)) match {
    case Left(e) => e.printStackTrace()
    case _ =>
  }
  g.dispose()
  bs.show()
}

def createGameWindow[T <: KeyListener with ComponentListener](sceneManager: T): JFrame = {
  val frame = new JFrame("Test")
  frame.setSize(800, 600)
  frame.setPreferredSize(new Dimension(800, 600))
  frame.setLocationRelativeTo(null)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setVisible(true)
  frame.pack()
  frame.createBufferStrategy(3)
  frame.addKeyListener(sceneManager)
  frame.addComponentListener(sceneManager)
  frame.setFocusable(true)
  frame.requestFocus()
  frame
}
