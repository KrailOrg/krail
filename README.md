# Krail

Krail (previously known as V7) provides a framework for rapid Java web development by combining Vaadin, Guice, Apache Shiro, Apache Commons Configuration and others.

It provides site navigation and the means to authenticate and authorise users. 
Building an application is assisted by specifying the site structure using a Sitemap.  There are 3 methods for specifying the Sitemap: using files(s), annotations, or directly coded in Guice modules (these can also be mixed)

The [issues tracker](https://github.com/davidsowerby/krail/issues?milestone=7&state=open), [blog](http://rndjava.blogspot.co.uk/) and [documentation](https://sites.google.com/site/q3cjava/home) provide more information.


## Download
### From Central Repository
#### Gradle:
```
repositories {
    jcenter()
}

'uk.q3c.krail:krail:0.7.1'
```
#### Maven (not yet available in Maven Central):
```
<repository>
  <id>jcenter</id>
  <url>http://jcenter.bintray.com</url>
</repository>


<dependency>
   <groupId>uk.q3c.krail</groupId>
   <artifactId>krail</artifactId>
   <version>0.7.1</version>
</dependency>
```
### Direct
[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)


##Limitations

Fails on Tomcat 8

## Status

10th November 2014:


* Vaadin 7.3.4 is integrated with:
* Guice 3.0, 
* Shiro 1.2.1, 
* Apache Commons Configuration
* Quartz Scheduler (as an optional library)

It also supports:

* Vaadin Server Push (with option to disable it)
* Extensive I18N support
* User options


Krail is usable, though there are still some bugs and further developments needed.  Vaadin push is now supported.


### demo

There is a [simple demo](https://github.com/davidsowerby/krail-demo) project

### testApp

There is a [functional test application](https://github.com/davidsowerby/krail-testApp) which can also be used to explore functionality

### quartz

An [integration with Quartz Scheduler](https://github.com/davidsowerby/krail-quartz), constructed as an optional Krail add-on.

##Project Build

Gradle is used (made a lot easier thanks to the [Gradle Vaadin plugin](https://github.com/johndevs/gradle-vaadin-plugin).  If you are an Eclipse user, and download the source, you will need to run 'gradle eclipse' to generate .classpath and other Eclipse specific files. 

