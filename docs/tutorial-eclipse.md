# Eclipse Set up as Vaadin-Project

##Acknowledgement

Thanks to [Dirk Lietz](https://github.com/Odhrean) for contributing this chapter.  If you have any questions regarding this chapter please refer them to the contributor
 
##Introduction
A short how-to set up krail as a library in a new Vaadin-Project in Eclipse

## Install Vaadin-Plugin
Install the [Vaadin Plugin for Eclipse](https://vaadin.com/eclipse)

## Create a new Vaadin Project
File -> New -> Other ...
Vaadin -> Vaadin 7 Project

Give it a Name and select the Target-Runtime (Apache Tomcat v8) and Java 1.8
Select the Deployment configuration : Servlet (default)

Hit Button Finish (or Next to configure some more Details like Pakage Names)

A new Vaadin-Project will now be created with [ivy-dependency Management](http://ant.apache.org/ivy/) set up

## Apply Krail-Dependency

Open ```ivysettings.xml``` and add the jcenter repository in the ```<resolvers>``` section:
```xml
	  <!-- jcenter -->
    <ibiblio name="jcenter" root="http://jcenter.bintray.com" m2compatible="true"/>
```

Open ```ivy.xml``` and add the krail-library in the ```<dependencies>``` section
```xml
	  <!-- The core of krail -->
  	<dependency org="uk.q3c.krail" name="krail" rev="0.9.3" />
```

The whole ```ivy.xml``` file could look like (with krail-kpa add on set up):
```xml 
<?xml version="1.0"?>
<!DOCTYPE ivy-module [
	<!ENTITY vaadin.version "7.4.6">
	<!ENTITY krail.version "0.9.3">
	<!ENTITY krail-jpa.version "0.8.8">
]>
<ivy-module version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://ant.apache.org/ivy/schemas/ivy.xsd">
	<info organisation="com.example" module="tutorial" />
	<configurations>
		<!-- The default configuration, which should be deployed to the server -->
		<conf name="default" />
		<!-- A configuration only needed when compiling the widget set. Should 
			not be deployed to the server -->
		<conf name="widgetset-compile" />
		<!-- A configuration used in compilation of server side classes only.
			Should be deployed to the server -->
		<conf name="nodeploy" />
	</configurations>
	<dependencies defaultconf="default" defaultconfmapping="default->default">
		<!-- The core of krail -->
		<dependency org="uk.q3c.krail" name="krail" rev="&krail.version;" />

		<!-- Add-On krail-jpa -->
		<dependency org="uk.q3c.krail" name="krail-jpa" rev="&krail-jpa.version;" />

		<!-- The core server part of Vaadin -->
		<dependency org="com.vaadin" name="vaadin-server" rev="&vaadin.version;" />

		<!-- Vaadin themes -->
		<dependency org="com.vaadin" name="vaadin-themes" rev="&vaadin.version;" />

		<!-- Push support -->
		<dependency org="com.vaadin" name="vaadin-push" rev="&vaadin.version;" />

		<!-- Servlet 3.0 API -->
		<dependency org="javax.servlet" name="javax.servlet-api" rev="3.0.1" conf="nodeploy->default" />

		<!-- Precompiled DefaultWidgetSet -->
		<dependency org="com.vaadin" name="vaadin-client-compiled"
			rev="&vaadin.version;" />

		<!-- Vaadin client side, needed for widget set compilation -->
		<dependency org="com.vaadin" name="vaadin-client" rev="&vaadin.version;"
			 conf="widgetset-compile->default" />

		<!-- Compiler for custom widget sets. Should not be deployed -->
		<dependency org="com.vaadin" name="vaadin-client-compiler"
			rev="&vaadin.version;" conf="widgetset-compile->default" />
	</dependencies>
</ivy-module>
```
