# krail

![License](http://img.shields.io/:license-apache-blue.svg)
[![Gitter](https://badges.gitter.im/davidsowerby/krail.svg)](https://gitter.im/davidsowerby/krail?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build Status](https://travis-ci.org/davidsowerby/krail.svg?branch=master)](https://travis-ci.org/davidsowerby/krail)
[![Coverage Status](https://coveralls.io/repos/github/davidsowerby/krail/badge.svg?branch=master)](https://coveralls.io/github/davidsowerby/krail?branch=master)

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
* Forms support through [EasyBinder](https://github.com/ljessendk/easybinder)
* User notifications

Additional libraries, integrated and configured through Guice, provide:

* JPA persistence - [krail-jpa](https://github.com/davidsowerby/krail-jpa), using [Apache Onami persist](http://onami.apache.org/persist/) and EclipseLink
* Quartz scheduler - [krail-quartz](https://github.com/davidsowerby/krail-quartz), using, of course,  [Quartz Scheduler](http://www.quartz-scheduler.org/)


The [issues tracker](https://github.com/davidsowerby/krail/issues?milestone=7&state=open), [blog](http://rndjava.blogspot.co.uk/) and [Tutorial](http://krail.readthedocs.org/en/master/) provide more information.


---

## Notice

---

Krail has now been updated to Vaadin 8.  The next step is to try migrating to [Eclipse Vert](http://vertx.io/).  If that is successful, from that point, the emphasis will be on providing user interface platform designed for use within a distributed environment, and specifically on vertx.

Normally the versions of the various libraries are independent, but to simplify the transition to Vaadin 8, they are being brought temporarily into line:

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
'uk.q3c.krail:krail:0.14.0.0'
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
	<version>0.14.0.0</version>
</dependency>
```
## Direct

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)

# Limitations

Would not work in a [clustered environment](https://github.com/davidsowerby/krail/issues/425)

# Status

* Vaadin 8.2.0 is integrated with:
* Guice 4.1.0
* Shiro 1.4.0,
* MBassador (Event Bus)
* Apache Commons Configuration
* Guava cache


Krail is usable, though there is still work to ensure thread safety.  No major changes to the API expected.  Vaadin push is supported.


## testApp

There is a [functional test application](https://github.com/davidsowerby/krail-testApp) which can also be used to explore functionality - though the [Tutorial](http://krail.readthedocs.org/en/latest/) may be better for that


# Project Build

Gradle is used (made a lot easier thanks to the [Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin).

# Acknowledgements

Thanks to:
 
[Dirk Lietz](https://github.com/Odhrean) for his review and feedback for the Tutorial<br>
[Mike Pilone](http://mikepilone.blogspot.co.uk/) for his blog post on Vaadin Shiro integration<br>

[EasyBinder](https://github.com/ljessendk/easybinder)
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
[spock](https://github.com/spockframework/spock)
[FindBugs](http://findbugs.sourceforge.net/)

