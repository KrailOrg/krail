# Guice and Serialisation

A Krail application would originally have only required to support serialisation in a high availability (HA) environment.  In general, using sticky sessions in a clustered environment would be sufficient.

In addition HA, running a Krail application on Vert.x places a further requirement for serialisation support.  This introduces all the usual Java serialisation issues of non-Serializable classes.

Specifically, Vaadin is designed in such a way that it holds the entire UI state to memory, and therefore needs to serialise it to session when HA or other circumstances need to move a session.


## Scope of impact

Given that the entire UI state needs to be serialised, this includes anything which implements the `UI` or `KrailView`, plus, of course, anything which they in turn require.

Even though components themselves should be `Serializable`, the largest impact is on views.  The emphasis in these notes is on views, but applies equally to `UI` implementations. 

The design pattern for Krail has been to use Guice constructor injection of many and varied dependencies into implementations of `KrailView`. More of these dependencies could undoubtedly implement `Serializable`, but there will always be cases where this is not possible.

The use of Guice introduces a further challenge - in order to be certain that dependencies are resolved consistently, Guice should be used to re-construct any transient dependencies after deserialisation.

# Objectives

1. Use Guice to re-construct any transient dependencies after deserialisation
1. Must allow for transients which are either reconstructed by Guice or by developer code  (a @NotGuice annotation?)
1. Make the process as simple and clear as possible for Krail application developers
1. If possible, make transition to a non-native serialisation process an easy option.


# Options and Obstacles

In order to meet objective 1), any potential resolution requires that the Guice Injector is available via a static reference

## Use of Injector.injectMembers

This would be very easy to implement in a readObject - a simply call:

```java
private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    InjectorHolder.getInjector().injectMembers(this)
}
```


### Obstacles

- This will only work with field or method injection.  Assuming the use of Field injection, all transient fields would then need to be annotated with @Inject, along with any other binding annotations.
- This would mean abandoning or modifying constructor injection for all the current `KrailView` implementations - the Field injection would simply overwrite the constructor injection during normal instance creation.
- Field injection has its own limitations, and most see it as a less attractive option.  Difficulty of testing is usually overstated especially with the introduction of [Bound Fields](https://github.com/google/guice/wiki/BoundFields), and the choices are discussed in the [Guice documentation](https://github.com/google/guice/wiki/Injections).  
- Even if Field injection were considered a good option, it would remove the choice from a Krail application developer. 


## Bespoke transient field initialiser

It would seem possible to create a routine to populate transient fields by reflection, using an Injector, as part of readObject.  Some preliminary code below shows how this might look - this is not fully tested, but copes with mainstream cases including generics and could cope with binding annotations.

 
```java
private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    Field[] fields = this.getClass().getDeclaredFields();
    final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
    List<Class> types = Arrays.asList(constructor.getParameterTypes());
    for (Field field : fields) {
        if (Modifier.isTransient(field.getModifiers())) {
            if (types.contains(field.getType())) {

                try {
                    field.setAccessible(true);
                    Object value = InjectorHolder.getInjector().getInstance(field.getType());
                    field.set(this, value);

                } catch (IllegalAccessException e) {
                    // TODO

                }
            }
        }
    }
}
```
        
### Obstacles

- Edge cases will be difficult to encompass
- Fields would have to be annotated with binding annotations where there are two instances of the same type being injected - there is no other way to match a constructor parameter to the field it is assigned to.
- It may introduce inconsistency with Guice - even though the injector is used, this has to cope with multi-bindings and assisted inject
- It is likely to be fairly complex code, with the maintenance that goes with it


## Proxy serialisation

An [old post](https://groups.google.com/forum/#!topic/google-guice/T9VMiv6pgLw) the author made a long time ago, may also provide an answer.  The relevant part is copied below:

> If you can hook the objects that you want serialized by adding a writeReplace() method, then you can use a serializable proxy to ship each over the wire.  The proxy would contain the Key corresponding to the binding of the object.  The trick here is establishing the reverse mapping from { object => key }, but it's possible through use of either the provision API or plain java code (I've done the latter myself).
  
> The proxy would have a readResolve() method that can find a handle to the injector on the JVM (several options exist for how to do this), and then it would return "injector.getInstance(key)."  This solution would allow you to have non-serializable types as "private final transient" instance vars on a serializable object.  Looks strange, but it's possible.

> As another solution, if you could only hook the deserialization code, you could then declare the non-serializable types as non-final and use member injection when types are deserialized (via "injector.injectMembers(deserializedObject)").


### Obstacles

- `Key` is not serialisable, and nor is TypeLiteral, which would be an alternative
- Defining a proxy would need to cater for generics, which is complicated by `Key` not being serialisable
- Reflection is still required
- Fields would have to be annotated with binding annotations where there are two instances of the same type being injected - there is no other way to match a constructor parameter to the field it is assigned to.

 
 
# Conclusion
Of these 3 not very attractive choices Proxy Serialisation looks the most promising