package game

object Ticker {
  private val NS_PER_UPDATE = 1000000000D / 60
}

class Ticker(update: Double => Unit, render: => Unit) extends Runnable {
  private var running = false

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
    var delta: Double = 0
    var timer = System.currentTimeMillis
    var frames = 0
    while (running) {
      val now = System.nanoTime
      delta += (now - lastTime) / Ticker.NS_PER_UPDATE
      lastTime = now
      while (delta >= 1) {
        update(delta)
        delta -= 1
      }
      if (running) {
        render
      }
      frames += 1
      if (System.currentTimeMillis - timer > 1000) {
        timer += 1000
        System.out.println("FPS: " + frames)
        frames = 0
      }
    }
  }
}