HTTP API for Pixelpusher rendering
===

API
---

| method | endpoint           | description                                                                              |
|--------|--------------------|------------------------------------------------------------------------------------------|
| GET    | `/api/stats`       | Returns some stats about the PP registry, including a list of pixelpushers.              |
| POST   | `/api/render`      | Renders to the PPs. Request body should include a base64-encoded byte array (3 bytes per pixel * pixels per strip * number of strips). Sending too little data will render as much as was sent. Sending too much will render as much as it can. If the byte array length after base64-decoding is not a multiple of 3, the request fails. |
| POST   | `/api/renderbytes` | Same as `/api/render`, but accepts a raw byte array rather than a base64-encoded string. |

Requirements for running
---

* java 8 (pixelpusher-java doesn't work on 10)
* maven (used for dependencies, install on osx with `brew install maven`)
* java deps from pom.xml (`mvn package`)

Once the jar is built, you can run it with `java -jar [path-to-jar]`. It's probably located at `target/canopy-1.0-SNAPSHOT-jar-with-dependencies.jar` within the project, which makes the command:

```
java -jar target/canopy-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Note: the Pixelpusher-java code is included under src/ and gets compiled with the main project.

