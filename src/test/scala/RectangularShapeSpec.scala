package com.github.jarlah.scalagraphics

import BreakoutScene.RectangularShape
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.awt.Color

class RectangularShapeSpec extends AnyFlatSpec with Matchers {

  "A RectangularShape" should "detect intersection with another RectangularShape correctly" in {

    val shape1 = new RectangularShape {
      val x = 0.0
      val y = 0.0
      val width = 5.0
      val height = 5.0
      val backgroundColor = Red
    }

    val shape2 = new RectangularShape {
      val x = 4.0
      val y = 4.0
      val width = 5.0
      val height = 5.0
      val backgroundColor = Red
    }

    shape1.intersects(shape2) should be(true)
  }

  it should "detect non-intersection with another RectangularShape correctly" in {

    val shape1 = new RectangularShape {
      val x = 0.0
      val y = 0.0
      val width = 5.0
      val height = 5.0
      val backgroundColor = Red
    }

    val shape2 = new RectangularShape {
      val x = 6.0
      val y = 6.0
      val width = 5.0
      val height = 5.0
      val backgroundColor = Red
    }

    shape1.intersects(shape2) should be(false)
  }

  // More tests can be added here for corner cases and more complex scenarios.
}
