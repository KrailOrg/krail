# Serialization

<p id="info">Hello World</p>

TIP: Don't stick your tongue in the socket

When Vaadin serialises to the session, it serialises the entire UI.  This means anything contained within the UI is also serialized.  If you follow the Krail approach of constructor injection for Views and UIs, it will mean that those dependencies will also be serialized, unless, of course, they are marked as **transient**.

This clearly could affect the amount that needs to be serialized/deserialized - you may want to reduce that by making dependencies **transient** (or you may just have dependencies which cannot be serialized), but that in turn means you need a way to reconstruct the **transient** fields.

## Guice Deserialization

To make this easier, ```ViewBase``` and ```ScopedUI``` use ```SerializationSupport``` to make the management of this situation simpler.  

When instances of these classes are deserialized, they implement the standard Java ```readObject()``` method and use ```SerializationSupport``` to re-inject any **transient** fields using the Guice Injector.  Hooks are also provided to allow you to intervene with your own logic at various points.

To enable this to work, however, certain conditions apply:


Sub-classes of ```ViewBase``` and ```ScopedUI``` :

- must have non-Serializable fields must be marked **transient**, as normal
- will only attempt to re-inject transient fields which have a null value at the time it invokes ```SerializationSupport.injectTransientFields()``` - you may need to consider this if you have other fields to manage during deserialization.  See below for the hooks available, and the sequence of calls made
- may have constructor parameters with binding annotations, but for automatic re-injection to work, the associated field must have exactly the same binding annotation, but NO @Inject annotation. If you have @Inject on the field as well, Guice will inject the field twice in effect, once via the constructor and once directly to the field.  Your IDE may flag a warning that you have a binding annotation without @Inject, but that can be ignored.
- will raise an exception if, after completing the sequence of calls below,  there are still null **transient** fields

1. beforeDeserialization()
1. --default deserialization
1. beforeTransientInjection()
1. --injection by SerializationSupport
1. afterTransientInjection()
1. checkForNullTransients()


## Servlet and Vertx environments

You do not need to do anything different for these environments, as long as you set up the application as described in the [Bootstrap](userguide/userguide-bootstrap.md) section.  This is because ```SerializationSupport``` delegates to an environment specific implementation of ```InjectorLocator```.  


