# Scala Graphics Game Demo

This project will contain multiple games/demos to showcase how the Scala Graphics library (https://github.com/jarlah/scalagraphics) can be used.

Current state of the game:

![Screenshot from 2023-06-20 21-40-20](https://github.com/jarlah/scalagraphics-demo/assets/404102/edc8890e-ac16-481d-a347-bb65930b1433)
![Screenshot from 2023-06-20 21-40-27](https://github.com/jarlah/scalagraphics-demo/assets/404102/64641d62-2cf9-48b3-8105-10faa085b16f)
![Screenshot from 2023-06-20 21-40-39](https://github.com/jarlah/scalagraphics-demo/assets/404102/d9707a17-0d93-4a3e-8bbb-fbfcb7b21054)

## Current release process

`sbt clean assembly`

`/usr/lib/jvm/java-20-openjdk/bin/jpackage --type app-image --name MyGame --input target/scala-3.3.0 --main-jar scala2dgame-assembly-0.1.0-SNAPSHOT.jar`

`tar -czvf MyGame.tar.gz MyGame`
