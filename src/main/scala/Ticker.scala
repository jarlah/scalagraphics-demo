package com.github.jarlah.scalagraphics

import scala.util.Try

object Ticker {
  private val NS_PER_UPDATE = 1000000000d / 60
}

class Ticker(update: Double => Unit, render: Ticker => Unit) extends Runnable {
  private var running = false
  private var fps: Int = 0

  def start(): Unit = {
    val thread = new Thread(this)
    running = true
    thread.start()
  }

  def stop(): Unit = {
    running = false
  }

  override def run(): Unit = {
    var lastTime = System.nanoTime
    var timer = System.currentTimeMillis
    var frames = 0
    while (running) {
      val now = System.nanoTime
      val delta = (now - lastTime) / Ticker.NS_PER_UPDATE
      lastTime = now
      update(delta)
      if (running) {
        render(this)
      }
      frames += 1
      if (System.currentTimeMillis - timer > 1000) {
        timer += 1000
        fps = frames
        frames = 0
      }
      // Sleep for a bit to reduce CPU usage
      Try(Thread.sleep(1))
    }
  }

  def getFps: Int = fps
}
