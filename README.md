# krail

![License](http://img.shields.io/:license-apache-blue.svg)
[![Gitter](https://badges.gitter.im/davidsowerby/krail.svg)](https://gitter.im/davidsowerby/krail?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build Status](https://travis-ci.org/davidsowerby/krail.svg?branch=master)](https://travis-ci.org/davidsowerby/krail)
[![Coverage Status](https://coveralls.io/repos/github/davidsowerby/krail/badge.svg?branch=master)](https://coveralls.io/github/davidsowerby/krail?branch=master)

==========================================================
# NOTICE

28 Sep 2019

It is with great regret that I have decided not to develop Krail any further.  A lot of work went into it, and although it received some interest, sadly not enough to make it worth maintaining.

==========================================================




Krail provides a framework for rapid Java web development by combining Vaadin, Guice, Apache Shiro, Apache Commons Configuration and others.  For more information, see the comprehensive [Tutorial](http://krail.readthedocs.org/en/master/), which also makes a reasonable demo.  (You can clone directly from the [Tutorial repo](https://github.com/davidsowerby/krail-tutorial)).  Note that the tutorial is based on the Vaadin 7 version of Krail - it should still be possible to follow it, but it will not be updated until the move to Eclipse Vert.x is done (or fails!) 


This core library provides:

* Site navigation, using a sitemap configured by annotation or Guice
* Authentication / Authorisation framework, including page access control
* Vaadin Server Push (with option to disable it)
* Event Bus
* Extensive I18N support
* User options
* Application configuration through ini files, database etc
* JSR 303 Validation (integrated with I18N)
* Forms support
* User notifications

The framework is highly configurable, and virtually any element of it can be replaced by using Guice bindings.  

## Development Focus

The current focus for development is to provide full Forms support, running on Vertx.  So far, thanks to the great contribution from [Marco Collovati](https://github.com/mcollovati), running on Vertx has shown no specific issues.

It is intended that the same code can be run both on Vertx and a conventional web server environment, and a bootstrapping mechanism is provided for that purpose.


The extensive [Documentation](http://krail.readthedocs.io/en/master/), includes a Tutorial, user Guide and Developer Guide.  The Tutorial is currently limited to the Vaadin 7 version, and needs rewriting to include new features. 


## Source Code

Originally written in Java the source is gradually being migrated to Kotlin.  Tests are a mix of JUnit, Spock and Spek.  The intention is to end up with an all-Kotlin solution, but porting is usually only done when code changes are required for other reasons.

## Versions

| Krail version  |   Vaadin    | 
|---------|------------|
|0.12.x.x | version 7  |
|0.13.x.x-v7compat | version 8, running version 7 compatibility  |
|0.14.x.x | and beyond, pure Vaadin 8


---


# Download
<a href='https://bintray.com/dsowerby/maven/krail/view?source=watch' alt='Get automatic notifications about new "krail" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>

## Gradle

```
repositories {
	jcenter()
}
```

```
'uk.q3c.krail:krail:0.16.12.0'
```
## Maven

```
<repository>
	<id>jcenter</id>
	<url>http://jcenter.bintray.com</url>
</repository>

```

```
<dependency>
	<groupId>uk.q3c.krail</groupId>
	<artifactId>krail</artifactId>
	<version>0.16.12.0</version>
</dependency>
```
## Direct

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)

# Limitations

Testing on Vertx has been limited, particularly on clustered Vertx.  Having said that, functional tests run on Vertx have shown no issues except that Push does not correctly disable.

# Status

* Vaadin 8.3.3 is integrated with:
* Guice 4.1.0
* Shiro 1.4.0,
* MBassador (Event Bus)
* Apache Commons Configuration
* Guava cache


## Functional Test Application

There is a [functional test application](https://github.com/davidsowerby/krail-testApp) which can also be used to explore functionality - though the [Tutorial](http://krail.readthedocs.org/en/latest/) may be better for that


# Project Build

Gradle is used (made a lot easier thanks to the [Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin).

# Acknowledgements

Thanks to:

[Marco Collovati](https://github.com/mcollovati) for his work on Vaadin - Vertx integration, and support for core Krail development<br>
[Dirk Lietz](https://github.com/Odhrean) for his review and feedback for the Tutorial<br>
[Mike Pilone](http://mikepilone.blogspot.co.uk/) for his blog post on Vaadin Shiro integration<br>


[ej technologies](http://www.ej-technologies.com/index.html) for an open source licence of [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html)<br>
[Vaadin](https://vaadin.com/home)<br>
[Guice](https://github.com/google/guice)<br>
[Apache Shiro](http://shiro.apache.org/)<br>
[JUnit](http://junit.org/)<br>
[Guava](https://github.com/google/guava) (cache and utilities)<br>
[MBassador Event Bus](https://github.com/bennidi/mbassador)<br>
[Flag Icons](http://www.icondrawer.com/)<br>
[Apache Commons Configuration](http://commons.apache.org/proper/commons-configuration)<br>
[Gradle](http://gradle.org/)<br>
[Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin)<br>
[Gradle Docker Plugin](https://github.com/bmuschko/gradle-docker-plugin)<br>
[Gradle Bintray Plugin](https://github.com/bintray/gradle-bintray-plugin)<br>
[Bintray](https://bintray.com)<br>
[Docker](https://www.docker.com/)<br>
[Logback](http://logback.qos.ch/)<br>
[slf4j](http://www.slf4j.org/)<br>
[AssertJ](http://joel-costigliola.github.io/assertj/)<br>
[Mycila](https://github.com/mycila)<br>
[Mockito](https://github.com/mockito/mockito)<br>
[spock](https://github.com/spockframework/spock)<br>
[Spek](http://spekframework.org/)<br>
[FindBugs](http://findbugs.sourceforge.net/)
[Mockk](https://mockk.io/)
