package game

import org.scalatest.funsuite.AnyFunSuite
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*

import java.awt.Graphics

class BreakoutSceneSpec extends AnyFunSuite {

  test("Ball changes direction when it hits the top of the screen") {

    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)

    // Assume there is no bricks left
    scene.bricks = Array.empty
    // Assume ball is at top of the screen and moving upwards
    scene.ball = scene.ball.copy(y = 0, speedY = -3, moving = true)

    scene.update(1.0)

    // Verify that the ball's speedY has been inverted to move downwards
    assert(scene.ball.speedY == 3)
  }

  test("Ball changes direction when it hits the left side of the screen") {

    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)

    // Assume there are no bricks left
    scene.bricks = Array.empty
    // Assume ball is at left side of the screen and moving leftwards
    scene.ball = scene.ball.copy(x = 0, speedX = -3, moving = true)

    scene.update(1.0)

    // Verify that the ball's speedX has been inverted to move rightwards
    assert(scene.ball.speedX == 3)
  }

  test("Ball changes direction when it hits the right side of the screen") {

    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)

    // Assume there are no bricks left
    scene.bricks = Array.empty
    // Assume ball is at right side of the screen and moving rightwards
    scene.ball = scene.ball.copy(
      x = sceneUtilsMock.width - scene.ball.radius,
      speedX = 3,
      moving = true
    )

    scene.update(1.0)

    // Verify that the ball's speedX has been inverted to move leftwards
    assert(scene.ball.speedX == -3)
  }

  test(
    "Ball is placed on paddle and paddle is centered when ball hits the bottom of the screen"
  ) {
    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)

    // Assume there are no bricks left
    scene.bricks = Array.empty
    // Assume ball is at bottom of the screen and moving downwards
    scene.ball = scene.ball.copy(
      y = sceneUtilsMock.height - scene.ball.radius,
      speedY = 3,
      moving = true
    )

    scene.update(1.0)

    // Verify that the ball's is placed on the paddle
    assert(scene.ball.y == scene.paddle.y - 10)
    assert(scene.ball.x == scene.paddle.x + scene.paddle.width / 2)

    // Verify that the paddle is centered
    assert(
      scene.paddle.x + scene.paddle.width / 2 == sceneUtilsMock.width / 2
    )

    // Verify that the ball is not moving
    assert(!scene.ball.moving)
  }

  test("Ball is rendered with expected graphics operation") {
    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])
    val graphicsMock = mock(classOf[Graphics])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)
    scene.ball = scene.ball.copy(x = 100, y = 100, radius = 10)

    // Create wrapper around the mock graphics object
    val graphicsWrapper = new GraphicsIOWrapper(graphicsMock)

    // Call render method
    scene.render.run(graphicsWrapper)

    // Verify that drawOval was called with the correct parameters
    verify(graphicsMock, times(1)).drawOval(90, 90, 20, 20)
  }
}
