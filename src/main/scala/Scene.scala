package com.github.jarlah.scalagraphics

import com.github.jarlah.scalagraphics.{GameKeyManager, GraphicsOp}

import java.awt.event.{
  ComponentEvent,
  ComponentListener,
  KeyAdapter,
  KeyEvent,
  KeyListener
}
import java.awt.{Dimension, Graphics, Image}
import java.util.{Timer, TimerTask}

trait Scene {
  def update(delta: Double): Unit
  def render: GraphicsIO[Unit]
  def onResize(newWidth: Int, newHeight: Int): Unit

  def onEnter(): Unit

  def onExit(): Unit
}

trait SceneUtils {
  def setScene(scene: Scene): Unit
  def width: Int
  def height: Int
}

case class SceneManager(
    keyManager: GameKeyManager,
    var width: Int,
    var height: Int
) extends KeyListener,
      ComponentListener,
      SceneUtils {

  private var currentScene: Scene = _

  def setScene(scene: Scene): Unit = {
    if (currentScene != null) currentScene.onExit()
    currentScene = scene
    if (currentScene != null) currentScene.onEnter()
  }

  def render: GraphicsIO[Unit] = {
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
    currentScene.onResize(e.getComponent.getWidth, e.getComponent.getHeight)
    // set these variables first now to let currentScene read old dimension
    width = e.getComponent.getWidth
    height = e.getComponent.getHeight
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
