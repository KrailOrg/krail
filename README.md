#krail

Krail (previously known as V7) provides a framework for rapid Java web development by combining Vaadin, Guice, Apache Shiro, Apache Commons Configuration and others.

This core library provides:

* Site navigation, using a sitemap configured by annotation, Guice or file (or any combination thereof)
* Authentication / Authorisation framework, including page access control
* Vaadin Server Push (with option to disable it)
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
'uk.q3c.krail:krail:0.8.1
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
	<version>0.8.1</version>
</dependency>
```
##Direct

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)

#Limitations

Fails on Tomcat 8

# Status

1st March 2015:

* Vaadin 7.4.0 is integrated with:
* Guice 3.0,
* Shiro 1.2.1,
* Apache Commons Configuration
* Guava cache


Krail is usable, though there are still some bugs and further developments needed.  Vaadin push is supported.


## demo

There is a [simple demo](https://github.com/davidsowerby/krail-demo) project

## testApp

There is a [functional test application](https://github.com/davidsowerby/krail-testApp) which can also be used to explore functionality


#Project Build

Gradle is used (made a lot easier thanks to the [Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin).

If you want to build from source run 'gradle eclipse' or 'gradle idea' to generate the IDE files

