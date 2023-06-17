import java.awt.event.{ComponentEvent, ComponentListener}
import java.awt.{Dimension, Graphics, Image}
import javax.swing.{ImageIcon, JFrame}

trait GraphicsIO {
  def drawImage(img: Image, x: Int, y: Int): Boolean
  def drawString(str: String, x: Int, y: Int): Unit
}

class GraphicsIOWrapper(g: Graphics) extends GraphicsIO {
  def drawImage(img: Image, x: Int, y: Int): Boolean =
    g.drawImage(img, x, y, null)

  def drawString(str: String, x: Int, y: Int): Unit =
    g.drawString(str, x, y)
}

case class GraphicsOp[A](run: GraphicsIO => A) {
  def map[B](f: A => B): GraphicsOp[B] =
    GraphicsOp(g => f(run(g)))

  def flatMap[B](f: A => GraphicsOp[B]): GraphicsOp[B] =
    GraphicsOp(g => f(run(g)).run(g))
}

object GraphicsOp {
  def pure[A](value: A): GraphicsOp[A] = GraphicsOp(g => value)
  def liftIO[A](f: GraphicsIO => A): GraphicsOp[A] = GraphicsOp(f)
}

def drawImage(image: Image, x: Int, y: Int): GraphicsOp[Boolean] =
  GraphicsOp.liftIO(_.drawImage(image, x, y))

def drawString(str: String, x: Int, y: Int): GraphicsOp[Unit] =
  GraphicsOp.liftIO(_.drawString(str, x, y))

trait Scene {
  def render: GraphicsOp[Unit]
}

class WelcomeScene extends Scene {
  private val image: Image = new ImageIcon("assets/welcome.jpeg").getImage
  override def render: GraphicsOp[Unit] = for {
    _ <- drawImage(image, 0, 0)
    _ <- drawString("Welcome to the game!", 100, 100)
  } yield ()
}


@main
def main(): Unit = {
  val frame = new JFrame("Test")
  frame.setSize(800, 600)
  frame.setPreferredSize(new Dimension(800, 600))
  frame.setLocationRelativeTo(null)
  frame.setVisible(true)
  frame.pack()
  frame.createBufferStrategy(3)

  val scene = new WelcomeScene

  while (true) {
    val bs = frame.getBufferStrategy
    val g = bs.getDrawGraphics
    scene.render.run(new GraphicsIOWrapper(g))
    g.dispose()
    bs.show()

    Thread.sleep(1000 / 60) // Let's aim for ~60 FPS
  }
}