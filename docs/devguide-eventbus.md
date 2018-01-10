#Introduction

Krail uses a publish - subscribe Event Bus in place of the common Event Listener based Observer pattern.   The [Guava EventBus page](https://code.google.com/p/guava-libraries/wiki/EventBusExplained) gives a good summary of the reasons for choosing this approach (under One Minute Guide).  In fact, the idea was originally to use the Guava implementation, but [MBassador](https://github.com/bennidi/mbassador) was chosen in its place because:

- it supports both weak and strong references (weak by default).  Guava supports only strong references.
- it supports synchronous and asynchronous buses

#Overview

Most of the work is done in the ```EventBusModule```, which binds bus instances to match the [Krail scopes](tutorial-guice-scopes.md) of **@Singleton**, **@VaadinSessionScope** and **@UIScope**.  You don't have to use all three, but there is a natural correlation between a bus and a scope.

The 3 implementations are represented by 3 binding annotations **@GlobalMessageBus**, **@SessionBus** and **@UIBus** respectively - each of which also is used to bind implementations for bus configuration, bus configuration error handlers and publication error handlers.

This means that by simple configuration changes in EventBusModule, you have 3 possible bus implementations matching Guice scopes, with each having individual configuration objects, individual error handlers if required and a choice between synchronous and asynchronous messaging.

The EventBusModule also uses Guice AOP to automatically subscribe classes annotated with @Listener.

The **@GlobalMessageBus** is asynchronous by default, as most of the publishers and subscribers are likely to be **@Singletons**, and therefore thread-safe.  The other 2 buses are synchronous.

All of the Guice configuration can of course be changed by replacing / sub-classing EventBusModule and updating your ```BindingManager``` in the usual way.

#Publishing Messages

Simply inject the **@GlobalMessageBus**, **@SessionBus** or **@UIBus** you want:
```
public class MyClassWithPublish {

    private final eventBus;

    @Inject
    protected MyClassWithPublish(@SessionBus PubSubSupport eventBus){
        this.eventBus=eventBus;
    }
}
```

Use an existing ```BusMessage``` implementation,  or create your own message class - which can be anything which implements the ```BusMessage``` interface.  (There are no methods in the interface, it is there for type safety and to help identify message classes).  At the appropriate point in your code, publish your message:
```
public void someMethod(){
    .... do stuff ...
    eventBus.publish (new MyBusMessage(this, someMoreInfo));
}
```

#Subscribing to Messages

Annotate the class with **@Listener** (which can also specify strong references)

Annotate the method within the listener class which will handle the message with **@Handler**.  The method must have a single parameter of the type of message you want to receive
```
public void MyMessageHandlerMethod(MyBusMessage busMessage){

    MyClassWithPublish sender = busMessage.getSender();
    
    .... etc ...
    
}
```

There are some other very useful features such as Filters and Priority .. see the [MBassador documentation](https://github.com/bennidi/mbassador).

#Automatic Subscription

During object instantiation, Guice AOP uses an InjectionListener in the ```EventBusModule``` to intercept all objects whose class is annotated with @Listener. The rules defining which bus to subscribe to are defined in an implementation of EventBusAutoSubscriber, which you can of course replace by binding a different implementation. The default implementation uses the @SubscribeTo to complement the association rules:

if a **@SubscribeTo** annotation is present, the buses defined by the annotation are used, and no others (Services are an exception, see below)
if a **@SubscribeTo** annotation has no arguments, it is effectively the same as saying "do not subscribe to anything even though this class is marked with a @Listener"
if there is no **@SubscribeTo** annotation
a Singleton is subscribed to the **@GlobalMessageBus** 
anything else is subscribed to **@SessionBus**
Note that **@Listener** and **@SubscribeTo** are inherited, so can be used on super-classes, but be overridden if re-defined in a sub-class.

#Services and Messages

Service implementations make use of the Event Bus to automate starting / stopping and restarting interdependent services.  Many Service implementations are **@Singletons** (though they do not have to be), so the **@GlobalMessageBus** is used and ALL Service objects are automatically subscribed though ```AbstractService``` to the **@GlobalBus**.  It is probably unwise to change that.  

