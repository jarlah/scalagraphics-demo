package com.github.jarlah.scalagraphics

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util
import scala.collection.mutable

class GameKeyManager extends KeyListener {

  private val keys =
    mutable.Map[GameKey, Boolean]().withDefaultValue(false)
  private val justPressed =
    mutable.Map[GameKey, Boolean]().withDefaultValue(false)
  private val cantPress =
    mutable.Map[GameKey, Boolean]().withDefaultValue(false)

  private def convertToGameKey(keyCode: Int): Option[GameKey] = keyCode match {
    case KeyEvent.VK_UP     => Some(GameKey.Up)
    case KeyEvent.VK_DOWN   => Some(GameKey.Down)
    case KeyEvent.VK_RIGHT  => Some(GameKey.Right)
    case KeyEvent.VK_LEFT   => Some(GameKey.Left)
    case KeyEvent.VK_ENTER  => Some(GameKey.Enter)
    case KeyEvent.VK_ESCAPE => Some(GameKey.Escape)
    case KeyEvent.VK_SPACE  => Some(GameKey.Space)
    case _                  => None
  }
  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit =
    convertToGameKey(e.getKeyCode)
      .foreach(gameKey => keys.put(gameKey, true))

  override def keyReleased(e: KeyEvent): Unit =
    convertToGameKey(e.getKeyCode)
      .foreach { gameKey =>
        keys.put(gameKey, false)
        cantPress.put(gameKey, false)
      }

  def update(): Unit = {
    for (key <- GameKey.values) {
      if (cantPress(key) && !keys(key))
        cantPress.put(key, false)
      else if (justPressed(key)) {
        cantPress.put(key, true)
        justPressed.put(key, false)
      }
      if (!cantPress(key) && keys(key))
        justPressed.put(key, true)
    }
  }

  def isKeyPressed(key: GameKey): Boolean = keys(key)

  def isKeyJustPressed(key: GameKey): Boolean = justPressed(key)
}
