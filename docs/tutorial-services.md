#Services

#Introduction

The Guice documentation strongly recommends making Guice modules [fast and side effect free](https://github.com/google/guice/wiki/ModulesShouldBeFastAndSideEffectFree).  It also provides an example interface for starting and stopping services.

Krail extends that idea with a more comprehensive lifecycle for a Service, and also adds dependency management.  For example, in order to start a Database Service, it may be necessary to load configuration values from a file or web service first.


##Lifecycle

The lifecycle is defined by ```Service.State``` and the standard cycle comprises states:

- INITIAL, STARTING, RUNNING, STOPPING, STOPPED plus a state of FAILED

The transient states of STARTING and STOPPING are there because some services may take a while to fully start or stop.

###Causes

```Service``` also defines ```Cause```, which is used to identify the cause of a state change (mostly for stopping / failing):

- FAILED, STOPPED, FAILED_TO_START, FAILED_TO_STOP, DEPENDENCY_STOPPED, STARTED, DEPENDENCY_FAILED, FAILED_TO_RESET

The [developer guide](devguide-services.md#state-changes-and-causes) gives more information about how the various method calls to Service affect state and cause;


##Dependencies
Krail provides different dependency types:

- **always required** - if service A always requires service B, then service B must be running for service A to start - and if service B stops, service A should also stop
- **required only at start** - if service A requires service B only at the start, then if service B subsequently stops, service A will keep running
- **optional** - if service A optionally depends on service B, then service A must be able to deal with the absence of service B

##Defining Dependencies

Dependencies can be defined by:

- Guice modules
- **@Dependency** annotations

In this Tutorial we will use both for the purposes of demonstration, but in practice it may be better to stick to one method.

##State changes

Whenever the state of a ```Service``` changes, a```ServiceBusMessage``` is published via the GlobalBus (see the [EventBus section](tutorial-event-bus.md)).  This could easily be used to provide a service monitor (a simplistic version of one is provided by Krail, the ```DefaultServicesMonitor```) - or it could be used to automatically send failure notifications to your boss at 2 o'clock in the morning.

#The Example

Our scenario is of 4 Services with dependencies between them.  We will configure the dependencies using Guice and annotations. We will then demonstrate how the dependencies interact.

##Create the Services

- create a new package *com.example.tutorial.service*
- in that package, create the following 4 service classes.  This is the simplest way of creating a Service, by extending ```AbstractService```.  (Note: if you want to copy these in the IDE, these 4 services are the same except for the class name and name key)

```
package com.example.tutorial.service;


import com.example.tutorial.i18n.LabelKey;
import com.google.inject.Inject;
import uk.q3c.krail.eventbus.GlobalBusProvider;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.ServiceModel;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

@Singleton
public class ServiceA extends AbstractService {
    
    @Inject
    protected ServiceA(Translate translate, ServicesModel serviceModel, GlobalBusProvider globalBusProvider) {
        super(translate, serviceModel, globalBusProvider);
    }

    @Override
    protected void doStop() throws Exception {
        
    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.ServiceA;
    }
}
```

```
package com.example.tutorial.service;


import com.example.tutorial.i18n.LabelKey;
import com.google.inject.Inject;
import uk.q3c.krail.eventbus.GlobalBusProvider;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.ServiceModel;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

@Singleton
public class ServiceB extends AbstractService {

    @Inject
    protected ServiceB(Translate translate, ServicesModel serviceModel, GlobalBusProvider globalBusProvider) {
        super(translate, serviceModel, globalBusProvider);
    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.ServiceB;
    }
}

```

```
package com.example.tutorial.service;


import com.example.tutorial.i18n.LabelKey;
import com.google.inject.Inject;
import uk.q3c.krail.eventbus.GlobalBusProvider;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.ServiceModel;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

@Singleton
public class ServiceC extends AbstractService {

    @Inject
    protected ServiceC(Translate translate, ServicesModel serviceModel, GlobalBusProvider globalBusProvider) {
        super(translate, serviceModel, globalBusProvider);
    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.ServiceC;
    }
}

```

```
package com.example.tutorial.service;


import com.example.tutorial.i18n.LabelKey;
import com.google.inject.Inject;
import uk.q3c.krail.eventbus.GlobalBusProvider;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.ServiceModel;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

@Singleton
public class ServiceD extends AbstractService {

    @Inject
    protected ServiceD(Translate translate, ServicesModel serviceModel, GlobalBusProvider globalBusProvider) {
        super(translate, serviceModel, globalBusProvider);
    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doStart() throws Exception {

    }

    @Override
    public I18NKey getNameKey() {
        return LabelKey.ServiceD;
    }
}

```

Note that each has a different name key - this is also used by getServiceKey(), which is used to uniquely identify a Service class.  This approach is used to overcome the changes in class name which occur when using enhancers such as Guice AOP.  This means that each Service class must have a unique name key.

As Services often are, these are all Singletons, although they do not have to be.

##Registering Services
 
All Service classes must be registered. We can do that very simply by sub-classing ```AbstractServiceModule``` and using the methods it provides


- create a new class ```TutorialServicesModule``` in *com.example.tutorial.service*
- copy the code below

```
package com.example.tutorial.service;

import com.example.tutorial.i18n.LabelKey;
import uk.q3c.krail.service.AbstractServiceModule;
import uk.q3c.krail.service.Dependency;

public class TutorialServicesModule extends AbstractServiceModule {

    @Override
    protected void registerServices() {
        registerService(LabelKey.ServiceA, ServiceA.class);
        registerService(LabelKey.ServiceB, ServiceB.class);
        registerService(LabelKey.ServiceC, ServiceC.class);
        registerService(LabelKey.ServiceD, ServiceD.class);
    }

    @Override
    protected void defineDependencies() {
       
    }
}
```
- include the module in the ```BindingManager```:

```
@Override
protected void addAppModules(List<Module> baseModules) {
    baseModules.add(new TutorialServicesModule());
}

```

##Monitor the Service status

Fur the purposes of the Tutorial, we will create a simple page to monitor the status of the Services.

- In ```MyOtherPages``` add the entry:

```java
addEntry("services", ServicesView.class, LabelKey.Services, PageAccessControl.PUBLIC);
```
- create ```ServicesView``` in the *com.example.tutorial.pages* package

```
package com.example.tutorial.pages;

import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.example.tutorial.i18n.LabelKey;
import com.example.tutorial.service.ServiceA;
import com.example.tutorial.service.ServiceB;
import com.example.tutorial.service.ServiceC;
import com.example.tutorial.service.ServiceD;
import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.eventbus.GlobalBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.service.ServiceBusMessage;
import uk.q3c.krail.core.view.Grid3x3ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

@Listener
@SubscribeTo(GlobalBus.class)
public class ServicesView extends Grid3x3ViewBase {

    private ServiceA serviceA;
    private ServiceB serviceB;
    private final ServiceC serviceC;
    private final ServiceD serviceD;

    @Caption(caption = LabelKey.Start_Service_A, description = DescriptionKey.Start_Service_A)
    private Button startABtn;
    @Caption(caption = LabelKey.Stop_Service_D, description = DescriptionKey.Stop_Service_D)
    private Button stopDBtn;
    @Caption(caption = LabelKey.Stop_Service_C, description = DescriptionKey.Stop_Service_C)
    private Button stopCBtn;
    @Caption(caption = LabelKey.Stop_Service_B, description = DescriptionKey.Stop_Service_B)
    private Button stopBBtn;
    private Translate translate;
    @Caption(caption = LabelKey.State_Changes,description = DescriptionKey.State_Changes)
    private TextArea stateChangeLog;
    @Caption(caption = LabelKey.Clear,description = DescriptionKey.Clear)
    private Button clearBtn;

    @Inject
    protected ServicesView(Translate translate,ServiceA serviceA, ServiceB serviceB, ServiceC serviceC, ServiceD serviceD) {
        super(translate);
        this.translate = translate;
        this.serviceA = serviceA;
        this.serviceB = serviceB;
        this.serviceC = serviceC;
        this.serviceD = serviceD;
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        super.doBuild(busMessage);
        createButtons();
        createStateMonitor();

    }

    private void createStateMonitor() {
        stateChangeLog = new TextArea();
        stateChangeLog.setSizeFull();
        stateChangeLog.setRows(8);
        getGridLayout().addComponent(stateChangeLog,0,1,2,1);
        clearBtn = new Button();
        clearBtn.addClickListener(click->stateChangeLog.clear());
        setBottomCentre(clearBtn);
    }

    @Handler
    protected void handleStateChange(ServiceBusMessage serviceBusMessage) {
        String serviceName = translate.from(serviceBusMessage.getService()
                                                             .getNameKey());
        String logEntry = serviceName + " changed from " + serviceBusMessage.getFromState()
                                                                            .name() + " to " + serviceBusMessage.getToState().name()+", cause: " +
                serviceBusMessage.getCause();
        String newline = stateChangeLog.getValue().isEmpty() ? "" : "\n";
        stateChangeLog.setValue(stateChangeLog.getValue()+newline+logEntry);
    }

    private void createButtons() {
        startABtn = new Button();
        startABtn.addClickListener(click -> serviceA.start());

        stopDBtn = new Button();
        stopDBtn.addClickListener(click -> serviceD.stop());

        stopCBtn = new Button();
        stopCBtn.addClickListener(click -> serviceC.stop());

        stopBBtn = new Button();
        stopBBtn.addClickListener(click -> serviceB.stop());

        Panel panel = new Panel();
        VerticalLayout layout = new VerticalLayout(startABtn, stopDBtn, stopCBtn, stopBBtn);
        panel.setContent(layout);
        setTopLeft(panel);
    }
}
```
- create the enum constants

Here we set up some buttons to start and stop services in ```createButtons()```<br>
We use the [Event Bus](tutorial-event-bus.md) to create a simple monitor for state changes in ```createStateMonitor()```

- run the application and try pressing 'Start Service A' - a message will appear in the state changes log

##Defining Dependencies

So far, all the Services operate independently - there are no dependencies specified.  Let us assume we want service A to depend on the other 3 services, each with a different one of the 3 dependency types.  We will also mix up using Guice and **Dependency** annotations, though you would probably use only one method to avoid confusion. 

###Dependencies with Guice

- add the following to the ```defineDependencies()``` method in the ```TutorialServicesModule```:

```java
addDependency(LabelKey.ServiceA,LabelKey.ServiceB, Dependency.Type.ALWAYS_REQUIRED);
addDependency(LabelKey.ServiceA,LabelKey.ServiceC, Dependency.Type.REQUIRED_ONLY_AT_START);
```

###Dependencies by Annotation

In ```ServiceA``` we inject ```ServiceD``` and store in a field in order to annotate it as a dependency (which you would need anyway if you wish to access ```ServiceD```).

- Modify ServiceA

```java

    @Dependency(required = false)
    private ServiceD serviceD;

    @Inject
    protected ServiceA(Translate translate, ServicesModel serviceModel, GlobalBusProvider globalBusProvider, ServiceD serviceD) {
        super(translate, serviceModel, globalBusProvider);
        this.serviceD = serviceD;
    }

```

This marks the dependency, ServiceD, as optional

##Testing Dependencies

- run the application
- navigate to the 'Services' page
- press 'Start Service A'
- Note that all 4 services show in the state changes log as 'STARTED' - ```ServiceA``` has automatically called all its dependencies to start.  The order they start in is arbitrary, as they are started in parallel threads, but ```ServiceA``` will not start until all its required dependencies have started.
- press 'Clear'
- press 'Start Service A' again - nothing happens.  Attempts to start/stop a service which is already started/stopped are ignored. 
- press 'Stop ServiceD' - only ```ServiceD``` stops
- press 'Stop ServiceC' - only ```ServiceC``` stops
- press 'Stop ServiceB' - ```ServiceB``` and ```ServiceA``` stop.  ```ServiceA``` has cause of DEPENDENCY_STOPPED

When ```ServiceD``` and ```ServiceC``` are stopped they do not affect ```ServiceA```, as they are declared as "optional" and "required only at start".
When ```ServiceB``` is stopped, however, ```ServiceA``` also stops because that dependency was declared as "always required"

#Summary

- We have created services by sub-classing ```AbstractService```
- We have defined dependencies between services using Guice
- We have defined dependencies between services using the **@Dependency** annotation
- We have demonstrated the interaction between services, when starting and stopping services with different dependency types

#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step11**