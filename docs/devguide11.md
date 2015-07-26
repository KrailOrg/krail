#Introduction

This section considers Guice and in particular its relationship with Vaadin

To understand Guice itself, the [Guice documentation](https://github.com/google/guice/wiki) is a good place to start.  This documentation only addresses points which relate to its use in Krail


#Guice Scopes

If you think you are not are not familiar with the idea of scopes, actually you probably are - at its simplest level, the principles are no different to thinking of variables having scope.  


##Vaadin Environment

The [Vaadin architecture](https://vaadin.com/book/vaadin7/-/page/architecture.html) is significantly different to a typical Web environment. There are three scopes used by Krail to reflect Vaadin's design:

###UI Scope

UI Scope represents a Vaadin UI instance, and is generally equivalent to a browser tab. To give a class this scope, apply the **@UIScoped** annotation to the class and instantiate with Guice. 

###Vaadin Session Scope

Vaadin session scope represents a ```VaadinSession``` and is generally equivalent to a browser instance.  To give a class this scope, apply the **@VaadinSessionScoped** annotation to the class and instantiate with Guice.

###Singleton

A Singleton has only one instance in the application. To give a class this scope, apply the **@Singleton** annotation to the class and instantiate with Guice.

Singleton classes must be thread safe.



#AOP

[Guice AOP](https://github.com/google/guice/wiki/AOP) is used by Krail, and if you are not familiar with it the main points to note are:
 
- Guice AOP works only on method interception
- It does **NOT** work on private, static or final methods - this is very easy to forget when stubbing methods with an IDE!
- For Guice AOP to work, Guice must instantiate the object