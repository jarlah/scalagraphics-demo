package com.github.jarlah.scalagraphics

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util
import scala.collection.mutable

class GameKeyManager extends KeyListener {

  private val keys = mutable.Map[GameKey, Boolean]().withDefaultValue(false)
  private val justPressed =
    mutable.Map[GameKey, Boolean]().withDefaultValue(false)
  private val cantPress =
    mutable.Map[GameKey, Boolean]().withDefaultValue(false)

  private def convertToGameKey(keyCode: Int) = keyCode match {
    case KeyEvent.VK_UP     => GameKey.Up
    case KeyEvent.VK_DOWN   => GameKey.Down
    case KeyEvent.VK_RIGHT  => GameKey.Right
    case KeyEvent.VK_LEFT   => GameKey.Left
    case KeyEvent.VK_ENTER  => GameKey.Enter
    case KeyEvent.VK_ESCAPE => GameKey.Escape
    case KeyEvent.VK_SPACE  => GameKey.Space
    case _                  => null
  }
  override def keyTyped(e: KeyEvent): Unit = {

// Optional implementation
  }
  override def keyPressed(e: KeyEvent): Unit = {
    val gameKey = convertToGameKey(e.getKeyCode)
    if (gameKey != null) keys.put(gameKey, true)
  }
  override def keyReleased(e: KeyEvent): Unit = {
    val gameKey = convertToGameKey(e.getKeyCode)
    if (gameKey != null) {
      keys.put(gameKey, false)
      cantPress.put(gameKey, false)
    }
  }
  def update(): Unit = {
    for (key <- GameKey.values) {
      if (cantPress.getOrElse(key, false) && !keys.getOrElse(key, false))
        cantPress.put(key, false)
      else if (justPressed.getOrElse(key, false)) {
        cantPress.put(key, true)
        justPressed.put(key, false)
      }
      if (!cantPress.getOrElse(key, false) && keys.getOrElse(key, false))
        justPressed.put(key, true)
    }
  }
  def isKeyPressed(key: GameKey): Boolean = keys.getOrElse(key, false)
  def isKeyJustPressed(key: GameKey): Boolean =
    justPressed.getOrElse(key, false)
}
