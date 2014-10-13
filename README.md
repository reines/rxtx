RXTX Loader
========

RXTX native loader, pulls in the [gnu.io RXTX library](http://rxtx.qbang.org) as a dependency. Supports Windows, Linux, OS X - x86, x86_64, and ARM.

[![Build Status](https://api.travis-ci.org/reines/rxtx.png)](https://travis-ci.org/reines/rxtx)
[![Coverage Status](https://coveralls.io/repos/reines/rxtx/badge.png?branch=master)](https://coveralls.io/r/reines/rxtx?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jamierf/rxtx/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.jamierf/rxtx)

RXTX Loader can be found in maven central.

## Installation

```xml
<dependency>
    <groupId>com.jamierf</groupId>
    <artifactId>rxtx</artifactId>
    <version>...</version>
</dependency>
```

## Usage

```java
RXTXLoader.load(); // Automatic detection of OS and Architecture
```
or
```java
RXTXLoader.load(OperatingSystem.LINUX, Architecture.ARMv6);
```

## License

Released under the [Apache 2.0 License](LICENSE).
