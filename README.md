# gettext

[![Build Status](https://travis-ci.org/mini2Dx/gettext.svg?branch=master)](https://travis-ci.org/mini2Dx/gettext)

GNU gettext Internationalization (i18n) for Java-based video games/applications

## Usage

### Java Library

The Java libary provides classes for reading .po files and accessing translations. 
See [wiki](https://github.com/mini2Dx/gettext/wiki) for a usage guide.

__Gradle__
```
compile "org.mini2Dx:gettext-lib:1.0.0"
```

__Maven__
```
<dependency>
    <groupId>org.mini2Dx</groupId>
    <artifactId>gettext-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle Plugin

The Gradle plugin can parse files and generate a .pot based on the source code. 
By default it is configured to parse Java files (searching for _GetText._ usage) but
can be configured to search other file types and regex patterns.

## Purpose

This implementation uses only a single dependency (antlr4-runtime) and 
avoids usage of reflection and ResourceBundle to allow for cross-compilation to o
ther platforms. Though made for video games, it is possible to use it any Java-based 
application. The parser is implemented as an ANTLR grammar so it is also possible to 
compile it to other languages.