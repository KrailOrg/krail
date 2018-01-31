# Introduction

There are currently two forms of persistence made available in Krail:

- the [krail-jpa](https://github.com/davidsowerby/krail-jpa) module, and
- the "In Memory" classes - which are not strictly speaking persistent, but offer the same API to aid a fast development start up and some testability.

This section provides guidance on what a persistence implementation should provide to the Krail core.  Both the In Memory and JPA implementations can be viewed as a way of understanding how this works.

For ease of description, the persistence provider here is unimaginatively called "XXXX" - although that may mean something to Australian readers. 

# Terminology
 
Throughout this section the terms "Persistence Unit" is used in the manner defined by JPA.   

 
# Identity

Very often an application will use a single persistence unit.  However, this should not become a constraint, as other applications require multiple persistence untis.  Krail should therefore enable the selection and use of multiple persistence units, if that is what the application requires.

Each Persistence Unit, and is associated services, must therefore be identifiable by an Annotation.  The Annotation itself currently has no specific requirements.

# Multiple Persistence Units from the same provider

Ideally, the persistence provider will support multiple persistence units for the same source type - for example, the krail-jpa module supports multiple PUs, each identified by their own unique annotation.  The In Memory persistence provided by Krail, however, offers only a single PU, although that is identified by an annotation as required above.  At the time of writing, an OrientDb library is being considered, which would only provide a single PU - but this would still be required to carry an annotation as required above. 

# Option

Krail core uses the ```Option``` class extensively, and by default, ```Option``` values are stored in an "in memory", volatile store.  A persistence provider must provide support for ```Option```, accessible to both the Krail developer's application and the Krail core.  

```Option``` requires a DAO implementation to read and set ```Option``` values. 
To support the presentation of ```Option``` values to the end user, an implementation of ```OptionContainerProvider```is required. 
The Guice Module used to configure the PU must also provide a fluent method for the ```BindingManager``` to enable the ```Option``` support for the PU.

The detailed requirements are therefore:

1. ```XXXXOptionDao``` which **extends** the ```OptionDao``` interface, with a Guice binding to the Identity annotation 
1. ```DefaultXXXXOptionDao``` as the default implementation of ```XXXXOptionDao``` 
1.  The binding of  ```XXXXOptionDao``` to ```DefaultXXXXOptionDao``` must be available to the Krail developer to override
1. ```XXXXOptionContainerProvider``` which **extends** the ```OptionContainerProvider``` interface, with a Guice binding to the Identity annotation
1. ```DefaultXXXXOptionContainerProvider``` as the default implementation for ```XXXXOptionContainerProvider```
1. The binding of ```XXXXOptionContainerProvider```to ```DefaultXXXXOptionContainerProvider``` must be available to the Krail developer to override
1. The Guice module should be called ```XXXXModule```
1. The ```XXXXModule``` must provide a a fluent method ```provideOptionDao()``` which will create all the bindings listed above.
1. If the ```provideOptionDao()``` method for a PU is not invoked before the Guice Injector is created, the bindings listed above should NOT be created 

## Testing Bindings

The bindings for a PU enabled by invoking ```provideOptionDao()``` should return instances as defined below:


| Injecting                          | Provides instance of <br>
| --------------------               |:---------------------<br>
| **@XXXX1** OptionDao               | DefaultXXXXOptionDao <br>
| **@XXXX1** OptionContainerProvider | DefaultXXXXOptionContainerProvider <br>

# Pattern

The Krail core uses I18N patterns extensively, and by default are read from ```EnumResourceBundle``` instances. A persistence provider must provide support for reading and writing I18N patterns.  Support for writing is required in order to enable the copying of I18N patterns, and the provision of translations from within the Krail application.



# EntityProvider or EntityManagerProvider
 
# Generic DAO
