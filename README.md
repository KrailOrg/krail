# V7

## Introduction
V7 is a reference application for Java / web development combining a number of popular open source frameworks, currently including Vaadin, Guice and Apache Shiro. My background is in business applications, which naturally influences the shape of this project.

## Motivation
Whenever I start a new application it feels like I cover the same ground - getting the basic application architecture in place first, and trying to get the various parts working together.  Of course, when I start looking for answers to integration questions, I find that there are many others out there asking the same questions.  So I thought I should build a basic reference application, using common OSS components, and use that as a start point for any applications I want to put together in future.  And since I am using only OSS components, it seems only fair that I should share the results with any one who wishes to use it.  With any luck, others will also find it useful.

## Objectives

1. Provide a structure which can be easily re-used as the basis for a new application, covering key aspects of functionality needed for most applications
1. Provide an application which actually does something (even if simple), to help others to understand how the components work together.
1. Use only open source components which are licensed in a way which enables the end result to be used in any way people want to.  (That usually means an Apache 2.0 licence).
1. Keep the architecture as pluggable as possible
1. Document code which relates to specific integration, to enable others to extract what they want.
Status

8th Feb 2013:  Vaadin 7.0.0 is integrated with Guice 3.0.  Shiro is fully working using annotations, although I still have to find a solution to path based security, which Shiro provides for standard web applications. 

The issues tracker and [blog](http://rndjava.blogspot.co.uk/) provide some more detail. 



[Contributions](https://sites.google.com/site/q3cjava/#TOC-Contributions) would be very welcome.
