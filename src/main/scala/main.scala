package com.github.jarlah.scalagraphics

import com.github.jarlah.scalagraphics.{
  AssetManager,
  GameKeyManager,
  GraphicsIOWrapper,
  GraphicsOp
}
import com.github.jarlah.scalagraphics.GraphicsOp.*

import java.awt.event.{
  ComponentAdapter,
  ComponentEvent,
  ComponentListener,
  KeyListener
}
import java.awt.{Dimension, Graphics2D, Image}
import javax.swing.{ImageIcon, JFrame, WindowConstants}

val width = 800
val height = 600

@main
def main(): Unit = {
  val assetManager = new AssetManager
  val keyManager = new GameKeyManager
  val sceneManager = SceneManager(keyManager, width, height)

  sceneManager.setScene(BreakoutScene(assetManager, keyManager, sceneManager))

  val frame = createGameWindow(sceneManager, width, height)
  val ticker = new Ticker(
    sceneManager.update,
    render(frame, sceneManager)
  )

  ticker.start()

  sys.addShutdownHook(ticker.stop())
}

def render(frame: JFrame, sceneManager: SceneManager): Ticker => Unit =
  ticker => {
    val bs = frame.getBufferStrategy
    val g = bs.getDrawGraphics
    (for {
      _ <- sceneManager.render
      _ <- drawString(
        s"FPS: ${ticker.getFps.toString}",
        10,
        sceneManager.height - 10
      )
    } yield ()).run(GraphicsIOWrapper(g))
    g.dispose()
    bs.show()
  }

def createGameWindow[T <: KeyListener with ComponentListener](
    listener: T,
    width: Int,
    height: Int
): JFrame = {
  val frame = new JFrame("Test")
  frame.setSize(width, height)
  frame.setPreferredSize(new Dimension(width, height))
  frame.setLocationRelativeTo(null)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.setVisible(true)
  frame.pack()
  frame.setResizable(false)
  frame.createBufferStrategy(3)
  frame.addKeyListener(listener)
  frame.addComponentListener(listener)
  frame.setFocusable(true)
  frame.requestFocus()
  frame
}
