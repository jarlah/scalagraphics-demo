package com.github.jarlah.scalagraphics

import BreakoutScene.{Ball, Paddle}
import org.scalatest.funsuite.AnyFunSuite

class BallSpec extends AnyFunSuite {

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
    val rectangle = Paddle(40, 40, 20, 20, 0)
    val ballRadius = 10
    val ball = Ball(
      rectangle.right + ballRadius + 1,
      50,
      ballRadius,
      0,
      0,
      moving = false
    )

    assert(!ball.intersects(rectangle))
  }
}
