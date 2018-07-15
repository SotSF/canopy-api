Requirements
---

* java 8 (pixelpusher-java doesn't work on 10)
* maven (used for dependencies, install on osx with `brew install maven`)
* java deps from pom.xml (`mvn package`)

Once the jar is built, you can run it with `java -jar [path-to-jar]`. It's probably located at `target/canopy-1.0-SNAPSHOT-jar-with-dependencies.jar` within the project, which makes the command:

```
java -jar target/canopy-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Building pixelpusher.jar
---

pixelpusher.jar is built from the official PixelPusher-java repo. From the root of a checkout of Pixelpusher-java:

```
cd bin/
jar cf pixelpusher.jar *
```

This is copied into our project under `src/main/resources/pixelpusher.jar`.
