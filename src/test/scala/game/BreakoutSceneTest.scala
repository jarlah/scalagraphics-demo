package game

import org.scalatest.funsuite.AnyFunSuite
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

class BreakoutSceneTest extends AnyFunSuite {

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
    scene.ball = scene.ball.copy(x = sceneUtilsMock.width - scene.ball.radius, speedX = 3, moving = true)

    scene.update(1.0)

    // Verify that the ball's speedX has been inverted to move leftwards
    assert(scene.ball.speedX == -3)
  }

  test("Ball is placed on paddle and paddle is centered when ball hits the bottom of the screen") {

    val assetManagerMock = mock(classOf[AssetManager])
    val keyManagerMock = mock(classOf[GameKeyManager])
    val sceneUtilsMock = mock(classOf[SceneUtils])

    when(sceneUtilsMock.width).thenReturn(800)
    when(sceneUtilsMock.height).thenReturn(600)

    val scene = BreakoutScene(assetManagerMock, keyManagerMock, sceneUtilsMock)

    // Assume there are no bricks left
    scene.bricks = Array.empty
    // Assume ball is at bottom of the screen and moving downwards
    scene.ball = scene.ball.copy(y = sceneUtilsMock.height - scene.ball.radius, speedY = 3, moving = true)

    scene.update(1.0)

    // Verify that the ball's is placed on the paddle
    assert(scene.ball.y == scene.paddle.y - scene.ball.radius)
    assert(scene.ball.x == scene.paddle.x + scene.paddle.width / 2)

    // Verify that the paddle is centered
    assert(scene.paddle.x == sceneUtilsMock.width / 2)

    // Verify that the ball is not moving
    assert(!scene.ball.moving)
  }
}