package game

import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener}
import java.awt.{Dimension, Graphics, Image}
import javax.swing.{ImageIcon, JFrame, WindowConstants}
import scala.util.Try

trait GraphicsIO {
  def drawImage(img: Image, x: Int, y: Int): Boolean
  def drawString(str: String, x: Int, y: Int): Unit
  def clearRect(x: Int, y: Int, width: Int, height: Int): Unit
  def drawRect(x: Int, y: Int, width: Int, height: Int): Unit
  def drawOval(x: Int, y: Int, width: Int, height: Int): Unit
}

class GraphicsIOWrapper(g: Graphics) extends GraphicsIO {
  def drawImage(img: Image, x: Int, y: Int): Boolean =
    g.drawImage(img, x, y, null)

  def drawString(str: String, x: Int, y: Int): Unit =
    g.drawString(str, x, y)

  def clearRect(x: Int, y: Int, width: Int, height: Int): Unit =
    g.clearRect(x, y, width, height)

  def drawOval(x: Int, y: Int, width: Int, height: Int): Unit =
    g.drawOval(x, y, width, height)

  def drawRect(x: Int, y: Int, width: Int, height: Int): Unit =
    g.drawRect(x, y, width, height)
}

case class GraphicsOp[A](run: GraphicsIO => Either[Throwable, A]) {
  def map[B](f: A => B): GraphicsOp[B] =
    GraphicsOp(g => run(g).map(f))

  def flatMap[B](f: A => GraphicsOp[B]): GraphicsOp[B] =
    GraphicsOp(g => run(g).flatMap(a => f(a).run(g)))
}

object GraphicsOp {
  def pure[A](value: A): GraphicsOp[A] = GraphicsOp(g => Right(value))
  def liftIO[A](f: GraphicsIO => A): GraphicsOp[A] = GraphicsOp(g => Try(f(g)).toEither)
}

def drawImage(image: Image, x: Int, y: Int): GraphicsOp[Boolean] =
  GraphicsOp.liftIO(_.drawImage(image, x, y))

def drawString(str: String, x: Int, y: Int): GraphicsOp[Unit] =
  GraphicsOp.liftIO(_.drawString(str, x, y))

def clearRect(x: Int, y: Int, width: Int, height: Int): GraphicsOp[Unit] =
  GraphicsOp.liftIO(_.clearRect(x, y, width, height))

def drawOval(x: Int, y: Int, width: Int, height: Int): GraphicsOp[Unit] =
  GraphicsOp.liftIO(_.drawOval(x, y, width, height))

def drawRect(x: Int, y: Int, width: Int, height: Int): GraphicsOp[Unit] =
  GraphicsOp.liftIO(_.drawRect(x, y, width, height))