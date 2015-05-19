# Introduction

This Tutorial will take you through some of the basic steps of getting an application up and running, quickly, with Krail.

Krail encourages prototyping, by providing a lot of default functionality so that you can see early results.  Most of that functionality can then be modified or replaced.  The aim is to give the Krail developer the best of both worlds - quick results, but still the freedom to modify things however they wish.

# Creating a Krail application with Gradle

## Preparation
This tutorial assumes that you have Gradle already installed.  The Krail build was done wtih Gradle 2.1, though other versions should work.  (There has been one report of an issue with Gradle 2.4)

It is also assumed that you will be using Git for version control, and have it installed

## Create a build file

### Linux

1. Change to your Git root directory, for example:

    ````sh
    cd /home/david/git
    ````
1. Create a directory for your project (called "tutorial" in this case), and initialise it for git.

    ```sh
mkdir tutorial
cd tutorial
git init
gedit build.gradle
```
1. You will now have an empty build file open.  Cut and paste the following into the file & save it

    ```groovy
apply from: 'http://plugins.jasoft.fi/vaadin-groovy.plugin?version=0.9.7'  
apply plugin: 'eclipse'  
apply plugin: 'idea'  

sourceCompatibility = '1.8'  

repositories {  
    jcenter()  
}  

dependencies {  
    'uk.q3c.krail:krail:0.9.3'  
}
```

    The first entry is for a [Vaadin Gradle plugin](https://github.com/johndevs/gradle-vaadin-plugin), and provides some valuable Vaadin specific Gradle tasks

    The other 'eclipse' and 'idea' plugins are optional, but useful for generating IDE specific files.

    Krail requires Java 8, hence the line "sourceCompatibility = '1.8'"

    And, of course, you cannot do without Krail ...

1. Now save the file and add it to Git

    ```sh
git add build.gradle
```
1. And finally, create a Gradle wrapper:

    ```sh
gradle wrapper
```


### Windows

tbd

## Create the Project

The Vaadin Gradle plugin makes things easier for us.  From the command line:

```sh
gradle vaadinCreateProject
```
Gradle will prompt for a number of entries - for the purposes of the tutorial, we will use these:

Application Name: Tutorial
Application Package: com.example.tutorial

## Import the Project to your IDE

### IDEA

In IDEA:

1. Start the import

    > File | Open and select the tutorial/tutorial.ipr

1. In the import dialog:

    - Ensure that JDK 1.8 is selected
    - Use "default gradle wrapper"
    - Select "Create directories for empty content roots automatically"

1. IDEA may prompt you to add the project VCS root - say yes if it does.

1. Delete the src/main/groovy folder completely - we will only be using Java for this Tutorial

1. There are a number of files which have not been added to Git - normally we would probably exclude a number of them from version control, but to keep things simple, right click on the project folder and select Git | Add to add all files to Git.

# Introduction

This Tutorial will take you through some of the basic steps of getting an application up and running, quickly, with Krail.

Krail encourages prototyping, by providing a lot of default functionality so that you can see early results.  Most of that functionality can then be modified or replaced.  The aim is to give the Krail developer the best of both worlds - quick results, but still the freedom to modify things however they wish.

# Creating a Krail application with Gradle

## Preparation
This tutorial assumes that you have Gradle already installed.  The Krail build was done wtih Gradle 2.1, though other versions should work.  (There has been one report of an issue with Gradle 2.4)

It is also assumed that you will be using Git for version control, and have it installed

## Create a build file

### Linux

1. Change to your Git root directory, for example:

    ````sh
    cd /home/david/git
    ````
1. Create a directory for your project (called "tutorial" in this case), and initialise it for git.

    ```sh
mkdir tutorial
cd tutorial
git init
gedit build.gradle
```
1. You will now have an empty build file open.  Cut and paste the following into the file & save it

    ```groovy
apply from: 'http://plugins.jasoft.fi/vaadin-groovy.plugin?version=0.9.7'  
apply plugin: 'eclipse'  
apply plugin: 'idea'  

sourceCompatibility = '1.8'  

repositories {  
    jcenter()  
}  

dependencies {  
    compile 'uk.q3c.krail:krail:0.9.3'  
}
```

    The first entry is for a [Vaadin Gradle plugin](https://github.com/johndevs/gradle-vaadin-plugin), and provides some valuable Vaadin specific Gradle tasks

    The other 'eclipse' and 'idea' plugins are optional, but useful for generating IDE specific files.

    Krail requires Java 8, hence the line "sourceCompatibility = '1.8'"

    And, of course, you cannot do without Krail ...

1. Now save the file and add it to Git

    ```sh
git add build.gradle
```
1. And finally, create a Gradle wrapper:

    ```sh
gradle wrapper
```


### Windows

tbd

## Create the Project

The Vaadin Gradle plugin makes things easier for us.  From the command line:

```sh
gradle vaadinCreateProject
```
Gradle will prompt for a number of entries - for the purposes of the tutorial, we will use these:

Application Name: Tutorial  
Application Package: com.example.tutorial

## Import the Project to your IDE

### IDEA

In IDEA:

1. Start the import

    > File | Open and select the tutorial/tutorial.ipr

1. In the import dialog:

    - Ensure that JDK 1.8 is selected
    - Use "default gradle wrapper"
    - Select "Create directories for empty content roots automatically"

1. IDEA may prompt you to add the project VCS root - say yes if it does.

1. Delete the src/main/groovy and src/test/groovy folders completely - we will only be using Java for this Tutorial

1. There are a number of files which have not been added to Git - normally we would probably exclude a number of them from version control, but to keep things simple, right click on the project folder and select Git | Add to add all files to Git.

## Krail preparation

### Guice and DI

This tutorial does not attempt to describe Guice, or Dependency Injection - which is what Krail is based on - but even if you are not familiar with either you may find that Krail is a good way to become so.  The [Guice documentation](https://github.com/google/guice/wiki/Motivation) is a very good introduction to the principles - and for reference, Krail uses [constructor injection](https://github.com/google/guice/wiki/Injections) with one or two specific exceptions.

### Setting up the application

1. Let's keep all the application configuration in onle place and create a package under src/main/java:

   >com.example.tutorial.app

    You may have noticed when you deleted the groovy folders, that a TutorialServlet had been generated.  We do need one, but not that one!

2. In the com.example.tutorial.app package, create a class TutorialServlet, extended from BaseServlet:

    ````java
    package com.example.tutorial.app;
    
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.q3c.krail.core.guice.BaseServlet;
import uk.q3c.krail.core.ui.ScopedUIProvider;

@Singleton
public class TutorialServlet extends BaseServlet {
    
    @Inject
    public TutorialServlet(ScopedUIProvider uiProvider) {
        super(uiProvider);
    }
}
```

1. If you are already familiar with Vaadin, you will be familiar with widgetsets.  However, if you are not, they can seem a bit of a mystery.  The [Vaadin documentation](https://vaadin.com/book/vaadin7/-/page/intro.html) is generally very good, but one thing which does not seem to be clear is when to use the in-built widgetset and when to specify your own.  We recommend starting with the assumption that you will want to generate your own at some point, and it is just easier to start that way, because Vaadin sometimes has trouble finding the in-built widgetset.  To set this up, we need to modify the Servlet:

```java
    @Singleton
    public class TutorialServlet extends BaseServlet {
    
    @Inject
    public TutorialServlet(ScopedUIProvider uiProvider) {
        super(uiProvider);
    }
    
     @Override
    protected String widgetset() {
        return "com.example.tutorial.widgetset.tutorialWidgetset";
    }
    }
```

1. and in the build.gradle file, add a vaadin closure:
    
```groovy
    dependencies {  
        compile 'uk.q3c.krail:krail:0.9.3'
    }
    vaadin {
        widgetset 'com.example.tutorial.widgetset.tutorialWidgetset'
    }
```
1.  create a web.xml file in src/main/webapp/WEB-INF

```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         id="WebApp_ID" version="3.0">
    <display-name>Krail Demo</display-name>
    <context-param>
        <description>
            Vaadin production mode
        </description>
        <param-name>productionMode</param-name>
        <param-value>false</param-value>
    </context-param>


    <filter>
        <filter-name>guiceFilter</filter-name>
        <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
        <async-supported>true</async-supported>
    </filter>
    <filter-mapping>
        <filter-name>guiceFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <listener>
        <listener-class>uk.q3c.cpas.app.BindingManager</listener-class>
    </listener>

    </web-app>
```


    






