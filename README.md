# Krail

## Introduction
Krail (previously known as V7) is a platform for rapid Java web development by combining Vaadin, Guice, Apache Shiro, Apache configuration and others.

It provides site navigation and the means to authenticate and authorise users. 
Building an application is assisted by specifying the site structure using a Sitemap.  There are 3 methods for specifying the Sitemap: using files(s), annotations, or directly coded in Guice modules (these can also be mixed)


The [issues tracker](https://github.com/davidsowerby/v7/issues?milestone=7&state=open), [blog](http://rndjava.blogspot.co.uk/) and [documentation](https://sites.google.com/site/q3cjava/home) provide more information.  

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail/images/download.svg) ](https://bintray.com/dsowerby/maven/krail/_latestVersion)

##Limitations

Fails on Tomcat 8

## Status

6th November 2014:


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


## Motivation
Whenever I start a new application it feels like I cover the same ground - getting the basic application architecture in place first, and trying to get the various parts working together.  Of course, when I start looking for answers to integration questions, I find that there are many others out there asking the same questions.  So I thought I should build a basic reference application, using common OSS components, and use that as a start point for any applications I want to put together in future.  And since I am using only OSS components, it seems only fair that I should share the results with any one who wishes to use it.  With any luck, others will also find it useful.

## Objectives

1. Provide a structure which can be easily re-used as the basis for a new application, covering key aspects of functionality needed for most applications
1. Provide an application which actually does something (even if simple), to help others to understand how the components work together.
1. Use only open source components which are licensed in a way which enables the end result to be used in any way people want to.  (That usually means an Apache 2.0 licence).
1. Keep the architecture as pluggable as possible
1. Document code which relates to specific integration, to enable others to extract what they want.







[Contributions](https://sites.google.com/site/q3cjava/#TOC-Contributions) would be very welcome.
