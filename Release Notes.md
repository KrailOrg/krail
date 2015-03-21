### Release Notes for krail 0.9.1

This version upgrades to Vaadin 7.4.2 and closes a number of unrelated tasks and bugs.

#### Change log

-   [38](https://github.com/davidsowerby/krail/issues/38): dirty view handling
-   [152](https://github.com/davidsowerby/krail/issues/152): providedCompile for javax.servlet
-   [250](https://github.com/davidsowerby/krail/issues/250): Acknowledge flag icons
-   [257](https://github.com/davidsowerby/krail/issues/257): NavTree and NavMenu sort differently
-   [260](https://github.com/davidsowerby/krail/issues/260): Fails on Tomcat 8
-   [294](https://github.com/davidsowerby/krail/issues/294): UIModule content review
-   [324](https://github.com/davidsowerby/krail/issues/324): Time comparison failure - uk.q3c.krail.core.services.ServiceTest.monitorLogsStatusChange
-   [347](https://github.com/davidsowerby/krail/issues/347): OptionCache not picking up configuration
-   [352](https://github.com/davidsowerby/krail/issues/352): EventBusModule - scope of GlobalBus
-   [353](https://github.com/davidsowerby/krail/issues/353): Upgrade to Vaadin 7.4.1


#### Dependency changes

   test compile dependency version changed to: q3c-testUtil:0.7.7
   test compile dependency version changed to: krail-testUtil:1.0.10

#### Detail

*Version update information*


---
*READEM updates*


---
*Added JProfiler to the README acknowledgements*


---
*Fix [257](https://github.com/davidsowerby/krail/issues/257) Sort order of UserNavigationMenu*

The compareTo method was using the node label for comparison.  This has been corrected to use the collation key


---
*Javadoc only*


---
*Added an application-provided id for event buses*


---
*Fix [347](https://github.com/davidsowerby/krail/issues/347) OptionCache configuration through OptionModule*


---
*Fix [250](https://github.com/davidsowerby/krail/issues/250) Acknowledgements added*


---
*Fix [324](https://github.com/davidsowerby/krail/issues/324) Intermittent test failure in ServiceTest*

Corrected the use of LocalDateTime.now() in ServiceMonitor.serviceStatusChange() which was introducing a possibility of small, but incorrect, time differences


---
*Fix [294](https://github.com/davidsowerby/krail/issues/294) refactor UIModule*

Moved DefaultConverterFactory to a new module, DataModule
Moved binding for UserStatusPanel to StandardComponentModule
Renamed StandardComponentModule to DefaultComponentModule


---
*Fix [353](https://github.com/davidsowerby/krail/issues/353) Vaadin 7.4.2*

Ticket was for 7.4.1 but 7.4.2 has just been released.  Had to undo the fix of [152](https://github.com/davidsowerby/krail/issues/152), as it was causing mock to fail for VaadinSession.  Raised a new ticket [354](https://github.com/davidsowerby/krail/issues/354).
rechecked and eliminated some 'force' statements in the Gradle ResolutionStrategy


---
*Fix [260](https://github.com/davidsowerby/krail/issues/260) Tomcat 8*

Was previously failing on Tomcat 8, but now works.  No specific action taken to fix it, but suspect it may have been earlier classpath version conflicts.  Updated README


---
*Fix [152](https://github.com/davidsowerby/krail/issues/152) ProvidedCompile*

Used the war plugin for providedCompile


---
*Fix [38](https://github.com/davidsowerby/krail/issues/38) Dirty view handling*

A ViewChangeRule provides an interception point for a Krail developer to apply logic to allow / disallow navigation to a new View.  Typically this would be used to check for a dirty view before allowing progress.  The default implementation always allows progression.


---
*Fix [352](https://github.com/davidsowerby/krail/issues/352) Corrected scope*

Added facilities to UIScope and VaadinSessionScope to verify whether they contain a specific instance.  Helps check Guice configuration.


---
