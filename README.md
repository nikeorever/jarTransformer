jarTransformer
===========

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.nikeo.jar-transformer/jar-transformer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.nikeo.jar-transformer/jar-transformer)

Read and Transform the contents of jar file entry in the jar file.

Sample
------
```kotlin
 transformJar(File("input.jar"), File("output.jar")) { inputJarEntry, outputJarEntryInputStream ->
      when {
          inputJarEntry.isClassFile() -> {
              // handle class file
              val clazz = ClassPool().makeClass(outputJarEntryInputStream)
              // modify bytecode
              if (clazz.name == "androidx.activity.R") {
                  clazz.addField(CtField(CtClass.intType, "generated_int_field", clazz), "100")
              }
              clazz.toBytecode()
          }
          inputJarEntry.isDirectory -> {
              outputJarEntryInputStream.readBytes()
          }
          else -> {
              outputJarEntryInputStream.readBytes()
          }
      }
 }

```

Or Use DSL API:
```kotlin
 transformJarDsl(File("input.jar"), File("output.jar")) {
      handleClassEntry { outputJarEntryInputStream ->
          // handle class file
          val clazz = ClassPool().makeClass(outputJarEntryInputStream)
          // modify bytecode
          if (clazz.name == "androidx.activity.R") {
              clazz.addField(CtField(CtClass.intType, "generated_int_field", clazz), "100")
          }
          clazz.toBytecode()
      }
 }
```

Download
--------

#### Gradle Kotlin Script
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("cn.nikeo.jar-transformer:jar-transformer:1.0.0")
}
```

#### Maven
```xml
<dependency>
  <groupId>cn.nikeo.jar-transformer</groupId>
  <artifactId>jar-transformer</artifactId>
  <version>1.0.0</version>
</dependency>
```

License
-------

Apache License, Version 2.0, ([LICENSE](https://github.com/nikeorever/jarTransformer/blob/trunk/LICENSE) or [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0))

