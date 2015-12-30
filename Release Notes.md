### Release Notes for krail 0.9.7

This version updates to Vaadin 7.5.10.  The Services model has been completely re-written, and introduces the ability to configure Service dependencies via Guice.
The Tutorial is complete. The demo has been removed (the Tutorial is far more useful). A number of small fixes / enhancements applied.  All major functionality is complete, and the aim is now to concentrate on a quality review prior to publishing the first release candidate 

#### Change log

-   [102](https://github.com/davidsowerby/krail/issues/102): i18N documentation
-   [111](https://github.com/davidsowerby/krail/issues/111): Document I18N in tables
-   [112](https://github.com/davidsowerby/krail/issues/112): Document I18N in Tutorial
-   [168](https://github.com/davidsowerby/krail/issues/168): Re-write tutorial
-   [240](https://github.com/davidsowerby/krail/issues/240): Loop detection for @Dependency
-   [262](https://github.com/davidsowerby/krail/issues/262): Complete Refining User Navigation
-   [320](https://github.com/davidsowerby/krail/issues/320): Example ValidationKeys
-   [331](https://github.com/davidsowerby/krail/issues/331): Update Option documentation
-   [332](https://github.com/davidsowerby/krail/issues/332): Document user hierarchies
-   [395](https://github.com/davidsowerby/krail/issues/395): elipse ide
-   [465](https://github.com/davidsowerby/krail/issues/465): MkDocs change format in mkdocs.yml file
-   [466](https://github.com/davidsowerby/krail/issues/466): ServicesMonitor to be active
-   [468](https://github.com/davidsowerby/krail/issues/468): Merge AbstractService & AbstractServiceI18N
-   [469](https://github.com/davidsowerby/krail/issues/469): Standardise composite Service.Status and make public
-   [473](https://github.com/davidsowerby/krail/issues/473): Remove need for start up logic in specific services
-   [474](https://github.com/davidsowerby/krail/issues/474): Vaadin 7.5.7
-   [477](https://github.com/davidsowerby/krail/issues/477): Vaadin 7.5.8
-   [478](https://github.com/davidsowerby/krail/issues/478): Vaadin 7.5.9
-   [480](https://github.com/davidsowerby/krail/issues/480): Use Guice to specify dependencies
-   [482](https://github.com/davidsowerby/krail/issues/482): Vaadin 7.5.10
-   [483](https://github.com/davidsowerby/krail/issues/483): Update README.md
-   [484](https://github.com/davidsowerby/krail/issues/484): Remove @Dependency from SitemapService
-   [485](https://github.com/davidsowerby/krail/issues/485): DefaultServicesMonitor to use GlobalBusProvider
-   [486](https://github.com/davidsowerby/krail/issues/486): Error 500 instead of Invalid URI
-   [488](https://github.com/davidsowerby/krail/issues/488): Service INITIAL to STOPPED
-   [489](https://github.com/davidsowerby/krail/issues/489): DefaultBroadcaster holds strong UI references
-   [490](https://github.com/davidsowerby/krail/issues/490): Push in build file not valid
-   [491](https://github.com/davidsowerby/krail/issues/491): @Dependency annotation params not consistent


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.16
   test compile dependency version changed to: q3c-testUtil:0.7.11

#### Detail

*Updated version and version description*


---
*Demo removed from the build*


---
*Tutorial is now complete and Dev Guide material migrated*

Close [332](https://github.com/davidsowerby/krail/issues/332)
Close [331](https://github.com/davidsowerby/krail/issues/331)
Close [320](https://github.com/davidsowerby/krail/issues/320)
Close [262](https://github.com/davidsowerby/krail/issues/262)
Close [168](https://github.com/davidsowerby/krail/issues/168)
Close [112](https://github.com/davidsowerby/krail/issues/112)
Close [111](https://github.com/davidsowerby/krail/issues/111)
Close [102](https://github.com/davidsowerby/krail/issues/102)


---
*Fix [491](https://github.com/davidsowerby/krail/issues/491) Dependency parameter 'required' changed to 'optional'*

Devguide files changed to meaningful names and references corrected


---
*Fix [395](https://github.com/davidsowerby/krail/issues/395) Manual merge of Eclipse chapter*


---
*Fix [486](https://github.com/davidsowerby/krail/issues/486) Corrected navigation response to invalid URI*

Handling of invalid URI moved from KrailErrorHandler to InvalidURIHandler injected directly into Navigator.  Binding for  InvalidURIHandler (formerly  InvalidURIExceptionHandler) moved to NavigationModule


---
*Fix [488](https://github.com/davidsowerby/krail/issues/488) Service with state INITIAL cannot be stopped or failed*

reset() added to Service (changes state from a stopped or failed state to INITIAL)


---
*Fix [489](https://github.com/davidsowerby/krail/issues/489) ScopedUI unregisters from Broadcaster*

Unregistered before it completes detach()


---
*Fix [490](https://github.com/davidsowerby/krail/issues/490) Push added as part of standard build*

Plus minor corrections to tutorial.


---
*Fix [484](https://github.com/davidsowerby/krail/issues/484) @Dependency in SitemapService replaced*

Replaced with Guice-specified dependency, ApplicationConfigurationService no longer injected


---
*Fix [485](https://github.com/davidsowerby/krail/issues/485) Use event bus providers*

Replaced all cases of @UIBus, @SessionBus and @GlobalBus annotated constructor parameters with UOIBusProvider, SessionBusProvider and GlobalBusProvider


---
*Corrected Tutorial index*


---
*Tutorial for Vaadin Push completed*

Corrected file names


---
*Tutorial file names changed to names instead of numbers*


---
*Services tutorial complete*


---
*EventBus tutorial complete.*

Plus minor Tutorial amends


---
*Javadoc updates only*


---
*Fix [482](https://github.com/davidsowerby/krail/issues/482) Vaadin 7.5.10*


---
*Fix [483](https://github.com/davidsowerby/krail/issues/483) Acknowledgements*

Manual merge and update


---
*Tutorial 11 (Guice scopes) added*


---
*typos in tutorial01*


---
*Fix [480](https://github.com/davidsowerby/krail/issues/480) Using Guice to define Service dependencies*

Design reworked again - design of [478](https://github.com/davidsowerby/krail/issues/478) did not work well with using Guice to define dependencies.  Now possible to define dependencies using annotations or Guice or any combination of them.  Service classes are represented using a ServiceKey to avoid problems with class names changing when enhanced by AOP (or any other enhancer).

Global, Session and UI BusProviders remove risk of annotated constructor paramenters being overriden by an event bus of the wrong scope.

ServiceController replaced by ServicesModel.  Although not implemented it would be perfectly possible to change dependencies or dependency types at run time by manipulating the ServiceModel


---
*Fix [478](https://github.com/davidsowerby/krail/issues/478) Vaadin 7.5.9*


---
*Fix [240](https://github.com/davidsowerby/krail/issues/240) Services redesigned*

Major rework of Services and the management of dependencies between them.

Fix [473](https://github.com/davidsowerby/krail/issues/473) ```AbstractService``` provides more robust handling of state changes, making sub-classing simpler.
Fix [466](https://github.com/davidsowerby/krail/issues/466) ServicesController added for active intervention (starting / stopping services), while ```ServiceMonitor``` stays passive
Fix [469](https://github.com/davidsowerby/krail/issues/469) Service.State defined in the Service interface, along with helper methods

Configuration of dependencies can still be applied using @Dependency, but requires implementing ```ServiceUsingDependencyAnnotation```
Dependencies can now also be configured through Guice.

The lifecycle now includes transitional states of STARTING and STOPPING.


---
*Fix [477](https://github.com/davidsowerby/krail/issues/477) Vaadin 7.5.8*


---
*Fix [468](https://github.com/davidsowerby/krail/issues/468) Merge AbstractService & AbstractServiceI18N*

AbstractService provides I18N name and description
@Dependency requiredAtStart=false behaviour clarified, and tests amended.  This setting makes the dependency optional.
ClassnameUtils renamed ClassNameUtils
ServiceUtils removed- functionality is in ClassNameUtils


---
*Fix [474](https://github.com/davidsowerby/krail/issues/474) Vaadin 7.5.7*

Upgrade to Vaadin 7.5.7


---
*Fix [465](https://github.com/davidsowerby/krail/issues/465) Change to MkDocs index file*

MkDocs have updated their file format for mkdocs.yml


---
