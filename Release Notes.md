### Release Notes for krail 0.9.2

This version upgrades to Vaadin 7.4.4 and provides improved DAO interfaces for Option and I18N patterns

#### Change log

-   [301](https://github.com/davidsowerby/krail/issues/301): Database source for I18N
-   [357](https://github.com/davidsowerby/krail/issues/357): Persistence for Option
-   [359](https://github.com/davidsowerby/krail/issues/359): Vaadin 7.4.3
-   [360](https://github.com/davidsowerby/krail/issues/360): Multiple data sources + instances
-   [362](https://github.com/davidsowerby/krail/issues/362): DefaultEventBusErrorHandler should throw exception
-   [365](https://github.com/davidsowerby/krail/issues/365): Make error view scrollable
-   [366](https://github.com/davidsowerby/krail/issues/366): BasicForest NPE when no children
-   [369](https://github.com/davidsowerby/krail/issues/369): Upgrade to Vaadin 7.4.4


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.11
   test compile dependency version changed to: q3c-testUtil:0.7.8

#### Detail

*Updated version information*


---
*Fix [369](https://github.com/davidsowerby/krail/issues/369) Upgrade to Vaadin 7.4.4*


---
*Fix [301](https://github.com/davidsowerby/krail/issues/301) Database source for I18N patterns*

The BundleReader interface has an implementation for a database source (simply using the generic PatternDao to access data), which can be configured within the I18NModule to provide another i18N pattern source.


---
*See [301](https://github.com/davidsowerby/krail/issues/301) Interfaces for I18NPattern DAOs*

JPA implementation provided by [krail-jpa 2](https://github.com/davidsowerby/krail-jpa/issues/2)


---
*Fix [360](https://github.com/davidsowerby/krail/issues/360) Fix [357](https://github.com/davidsowerby/krail/issues/357) Multiple persistence Units and persistence for Option*

The implementation of persistence is currently limited to JPA, through the krail-jpa library, but that does support multiple persistence units, identifiable by annotation.   Common generic DAO interfaces have been provided in core Krail to support alternative persistence sources as and when required.  (Some specifically for Option).     [krail-jpa 5](https://github.com/davidsowerby/krail-jpa/issues/5) and [krail-jpa 1](https://github.com/davidsowerby/krail-jpa/issues/1) provide the JPA implementations for these interfaces. Persistence sources may not be completely swappable, but changes required should be minimal.


---
*DAOs constructed, but no tests yet*


---
*SubPagePanel handling of Navigator initial state*

 Navigator may not have moved to a valid navigation state before the SubPagePanel.build() is called;  was failing because Navigator.getCurrentNode() was returning null.  The error was being hidden prior to [362](https://github.com/davidsowerby/krail/issues/362)


---
*Related to [362](https://github.com/davidsowerby/krail/issues/362), ServiceTest needs TestEventBusModule*

Re-thrown exceptions from the EventBusErrorHandler causing tests to fail incorrectly


---
*Fix [366](https://github.com/davidsowerby/krail/issues/366) NPE from BasicForest.getChildren()*

The underlying graph returns null when none found.  Now converted to an empty list


---
*Fix [365](https://github.com/davidsowerby/krail/issues/365) DefaultErrorView message area made scrollable*


---
*Fix [362](https://github.com/davidsowerby/krail/issues/362) DefaultEventBusHandler throws RuntimeException*

Previously  was just logging an error, but that can hide failures.


---
*Fix [359](https://github.com/davidsowerby/krail/issues/359) Upgrade to Vaadin 7.4.3*


---
*Fix [360](https://github.com/davidsowerby/krail/issues/360) Multiple persistence units*

DAOs provided to support multiple persistence units.  When combined with JPA support provided by [krail-jpa 5](https://github.com/davidsowerby/krail-jpa/issues/5) the developer can support multiple JPA persistence units within an application.  DAOs are generic, and the basis for an entity specific DAO also provided.


---
*See [357](https://github.com/davidsowerby/krail/issues/357) Changes to Option for persistence*

Modified the OptionDao to enable the persistence related elements to be better supported by persistence libraries


---
*Updated README for Guice 4*


---
