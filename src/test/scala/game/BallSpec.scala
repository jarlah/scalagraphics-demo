package game

import org.scalatest.funsuite.AnyFunSuite

class BallSpec extends AnyFunSuite {

  import BreakoutScene._

  test("Ball intersects with a rectangle when it should") {
    val ball = Ball(50, 50, 10, 0, 0, moving = false)
    val rectangle = Paddle(40, 40, 20, 20, 0)

    assert(ball.intersects(rectangle))
  }

  test("Ball does not intersect with a rectangle when it shouldn't") {
    val ball = Ball(100, 100, 10, 0, 0, moving = false)
    val rectangle = Paddle(40, 40, 20, 20, 0)

    assert(!ball.intersects(rectangle))
  }

  test("Ball intersects with a rectangle on the edge") {
    val ball = Ball(60, 50, 10, 0, 0, moving = false)
    val rectangle = Paddle(40, 40, 20, 20, 0)

    assert(ball.intersects(rectangle))
  }

  test(
    "Ball does not intersect with a rectangle when just outside its boundaries"
  ) {
    val ball = Ball(61, 50, 10, 0, 0, moving = false)
    val rectangle = Paddle(40, 40, 20, 20, 0)

    assert(!ball.intersects(rectangle))
  }
}
