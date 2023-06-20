# Scala Graphics Game Demo

This project will contain multiple games/demos to showcase how the Scala Graphics library (https://github.com/jarlah/scalagraphics) can be used.

Current playable game is a simple Breakout game:

![image](https://github.com/jarlah/scalagraphics-demo/assets/404102/d793f84a-8ff1-4110-85ea-588c4083c979)

Which for obvious reasons (not being made by a designer) looks like a heap of donkeys kidneys :trollface:

## Current release process

`sbt clean assembly`

`/usr/lib/jvm/java-20-openjdk/bin/jpackage --type app-image --name MyGame --input target/scala-3.3.0 --main-jar scala2dgame-assembly-0.1.0-SNAPSHOT.jar`

`tar -czvf MyGame.tar.gz MyGame`
