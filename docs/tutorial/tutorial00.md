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

