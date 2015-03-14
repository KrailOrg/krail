### Release Notes for krail 0.9.0

This version introduces an Event Bus and replaces the event listener type of Observer pattern used previously, with Publish Subscribe.  All Krail observer patterns are now implemented using the Event Bus.

This should be the last major change to the API.

The MasterSitemapNode is now immutable, and some scope corrections made.  There are likely still to be some changes around allocating scope correctly and ensuring Krail is tread-safe in the right places.

#### Change log

-   [264](https://github.com/davidsowerby/krail/issues/264): Remove UserStatus,
-   [293](https://github.com/davidsowerby/krail/issues/293): krail.testUtils
-   [304](https://github.com/davidsowerby/krail/issues/304): switch to logback
-   [327](https://github.com/davidsowerby/krail/issues/327): UserSitempapNode to use copy of MasterSitemapNode
-   [338](https://github.com/davidsowerby/krail/issues/338): Exceptions not logged
-   [339](https://github.com/davidsowerby/krail/issues/339): Invalid message length from Atmosphere
-   [341](https://github.com/davidsowerby/krail/issues/341): shiro to version 1.2.3
-   [342](https://github.com/davidsowerby/krail/issues/342): Scope differences incorrectly handled, Navigator and UserStatus
-   [345](https://github.com/davidsowerby/krail/issues/345): Use event bus to replace LocaleChangeListener
-   [346](https://github.com/davidsowerby/krail/issues/346): Use event bus to replace ViewChangeListener
-   [348](https://github.com/davidsowerby/krail/issues/348): eventBus to replace ServiceChangeListener
-   [349](https://github.com/davidsowerby/krail/issues/349): Auto register with service bus ?
-   [351](https://github.com/davidsowerby/krail/issues/351): Replace UserSitemapListener with EventBus


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.9
   test compile dependency version changed to: q3c-testUtil:0.7.6

#### Detail

*Updated README*


---
*Updated version information*


---
*Fix [351](https://github.com/davidsowerby/krail/issues/351) UserSitemap listeners replaced by Event Bus*


---
*Fix [346](https://github.com/davidsowerby/krail/issues/346) ViewChangeListeners replaced by Event Bus*

View changes events passed through @UIBus. @SubscribeTo is now @Inherited


---
*Sync / Async bus creation was the wrong way round*


---
*Fix [348](https://github.com/davidsowerby/krail/issues/348) Services uses Event Bus instead of listeners*

All the Service messages are routed through a @GlobalBus scope Event Bus.  All Services are automatically subscribed in order to manage dependencies between services.


---
*Fix [293](https://github.com/davidsowerby/krail/issues/293) Duplicated test utility classes removed*

Consolidated into krail-testUtil


---
*See [348](https://github.com/davidsowerby/krail/issues/348) ServiceListener replaced by event bus*

All services (through AbstractService) are set up to publish status changes through the @GlobalBus.  The ServicesMonitorModule is renamed ServiceModule, as it now does more to initialise services - an injection listener intercepts and injects the global event bus, and subscribes the service to the bus.

There is no longer a need to register services with the ServicesMonitor, it is done during service init().


---
*Fix [349](https://github.com/davidsowerby/krail/issues/349) Auto subscribe to Event Bus*

The EventBusModule auto-subscribes objects annotated with @Listener to a specific bus, using an InjectionListener.  The bus is selected by logic in EventBusModule, or can be overridden by a @SubscribeTo.  This means that the bus does not need to be injected to a subscriber, only to a publisher.


---
*Change to error message only, no functional change*


---
*Fix [345](https://github.com/davidsowerby/krail/issues/345) Locale change listeners replaced by event bus*

Locale change listeners using the publish-subscribe model of the event bus (using a Vaadin Session scoped bus)


---
*Fix [264](https://github.com/davidsowerby/krail/issues/264) EventBus replaces UserStatus*

Integrated an event bus (MBassador) to replace the Observer pattern of UserStatus.


---
*Fix [342](https://github.com/davidsowerby/krail/issues/342) Scopes corrected*

DefaultNavigator made @UIScoped
DefaultSitemapService made @Singleton

Handling of standard pages in the sitemap improved.  There is no longer a need to explicitly add standard pages, they are detected from the page key.

UserSitemap always holds all standard pages keys, plus an additional mapping of uri to page key.  This is required because not all nodes will be in the UserSitemap, and the uri() method may not return a result.

The is***Uri methods move to Sitemap from UserSitemap


---
*Fix [327](https://github.com/davidsowerby/krail/issues/327) Immutable MasterSitemapNode*

Rather than make multiple copies of MasterSitemapNodes (one per UserSitemap), MasterSitemapNode is now immutable.  This has required some changes to the process of constructing them, as the logic for that is dispersed.  In the process of testing this, found a bug in the way the Navigator scope is handled (see [342](https://github.com/davidsowerby/krail/issues/342)).  This commit therefore fails the Login_Navigation_Rule test in TestApp, pending a fix for [342](https://github.com/davidsowerby/krail/issues/342)


---
*See [340](https://github.com/davidsowerby/krail/issues/340) Annotation added for quality review*


---
*Fix [341](https://github.com/davidsowerby/krail/issues/341) Shiro updated to version 1.2.3*


---
*Fix [339](https://github.com/davidsowerby/krail/issues/339) Invalid message length from Atmosphere*

Minor change to krail.gradle but problem actually fixed by cleaning up version conflicts in master build, see [krail-master 15](https://github.com/davidsowerby/krail-master/issues/15)


---
*Fix [338](https://github.com/davidsowerby/krail/issues/338) Exceptions not handled*

The ErrorHandler needed to be explicitly set in VaadinSession as well as the UI


---
*Fix [304](https://github.com/davidsowerby/krail/issues/304) Change to logback*

Logback incorporated into the build, LogMonitor updated and configuration files changed.


---
