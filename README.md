
# gettext

![Continuous Integration](https://github.com/mini2Dx/gettext/workflows/Continuous%20Integration/badge.svg)

GNU gettext Internationalization (i18n) for Java-based video games/applications

## Usage

### Java Library

The Java libary provides classes for reading .po files and accessing translations. 
See the [wiki](https://github.com/mini2Dx/gettext/wiki) for a usage guide.

Javadoc can be found [here](https://mini2dx.github.io/gettext/javadoc/1.5.0/index.html)

__Gradle__
```gradle
compile "org.mini2Dx:gettext-lib:1.8.0"
```

__Maven__
```xml
<dependency>
    <groupId>org.mini2Dx</groupId>
    <artifactId>gettext-lib</artifactId>
    <version>1.8.0</version>
</dependency>
```

### shaded jar

There is a shaded jar available that contains the lib including shaded versions of its transitive dependencies. 
Use this if you experience issues with having multiple versions of antlr on the classpath.

__Gradle__
```gradle
implementation "org.mini2Dx:gettext-lib-all:1.9.2"
```

__Maven__
```xml
<dependency>
    <groupId>org.mini2Dx</groupId>
    <artifactId>gettext-lib-all</artifactId>
    <version>1.9.2</version>
</dependency>
```

### Gradle Plugin

The Gradle plugin can parse files and generate a .pot based on the source code.  
See the [wiki](https://github.com/mini2Dx/gettext/wiki) for a usage guide.

## Purpose

This implementation uses only a single dependency (antlr4-runtime) and 
avoids usage of reflection and ResourceBundle to allow for cross-compilation to o
ther platforms. Though made for video games, it is possible to use it any Java-based 
application. The parser is implemented as an ANTLR grammar so it is also possible to 
compile it to other languages.
