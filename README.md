#krail

Krail provides a framework for rapid Java web development by combining Vaadin, Guice, Apache Shiro, Apache Commons Configuration and others.  For more information, see the [Tutorial](http://krail.readthedocs.org/en/stable/tutorial01/) (still in development but will get you started)

This core library provides:

* Site navigation, using a sitemap configured by annotation, Guice or file (or any combination thereof)
* Authentication / Authorisation framework, including page access control
* Vaadin Server Push (with option to disable it)
* Event Bus
* Extensive I18N support
* User options
* Application configuration through ini files etc
* JSR 303 Validation (integrated with I18N)

Additional libraries, integrated and configured through Guice, provide:

* JPA persistence - [krail-jpa](https://github.com/davidsowerby/krail-jpa), using [Apache Onami persist](http://onami.apache.org/persist/) and EclipseLink
* Quartz scheduler - [krail-quartz](https://github.com/davidsowerby/krail-quartz), using, of course,  [Quartz Scheduler](http://www.quartz-scheduler.org/)


The [issues tracker](https://github.com/davidsowerby/krail/issues?milestone=7&state=open), [blog](http://rndjava.blogspot.co.uk/) and [documentation](https://sites.google.com/site/q3cjava/home) provide more information.


#Download
<a href='https://bintray.com/dsowerby/maven/krail/view?source=watch' alt='Get automatic notifications about new "krail" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>
##Gradle

```
repositories {
	jcenter()
}
```

```
'uk.q3c.krail:krail:0.9.4'
```
##Maven

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
	<version>0.9.4</version>
</dependency>
```
##Direct

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)

#Limitations

Not fully thread-safe where it needs to be

# Status

21st May 2015:

* Vaadin 7.4.6 is integrated with:
* Guice 4.0
* Shiro 1.2.3,
* MBassador (Event Bus)
* Apache Commons Configuration
* Guava cache


Krail is usable, though there is still work to ensure thread safety.  No major changes to the API expected.  Vaadin push is supported.  Tested on Tomcat 7 & 8


## demo

There is a [simple demo](https://github.com/davidsowerby/krail-demo) project

## testApp

There is a [functional test application](https://github.com/davidsowerby/krail-testApp) which can also be used to explore functionality - it actually contains more than the demo


#Project Build

Gradle is used (made a lot easier thanks to the [Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin).

If you want to build from source run 'gradle eclipse' or 'gradle idea' to generate the IDE files

#Acknowlegements

ej technologies for an open source licence for their java profiler, [JProfiler](http://www.ej-technologies.com/products/jprofiler/overview.html)<br>
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

