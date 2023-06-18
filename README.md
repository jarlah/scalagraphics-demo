`sbt clean assembly`

`/usr/lib/jvm/java-20-openjdk/bin/jpackage --type app-image --name MyGame --input target/scala-3.3.0 --main-jar scala2dgame-assembly-0.1.0-SNAPSHOT.jar`

`tar -czvf MyGame.tar.gz MyGame`
