### Release Notes for krail 0.9.3

This version upgrades to Vaadin 7.4.6 and Guice 4.0.  The I18NProcessor has been updated to reduce the amount it has to process, and clarify when drill down occurs.  The amount of setting up for a simple application has been reduced.  

#### Change log

-   [378](https://github.com/davidsowerby/krail/issues/378): Guice 4.0
-   [379](https://github.com/davidsowerby/krail/issues/379): Vaadin 7.4.5
-   [388](https://github.com/davidsowerby/krail/issues/388): Vaadin 7.4.6


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.12
   test compile dependency version changed to: q3c-testUtil:0.7.9

#### Detail

*Placeholder for documentation*


---
*Updated version info*


---
*Fix [388](https://github.com/davidsowerby/krail/issues/388) Vaadin 7.4.6*


---
*Fix [378](https://github.com/davidsowerby/krail/issues/378) Guice 4.0*


---
*Fix [379](https://github.com/davidsowerby/krail/issues/379) Vaadin 7.4.5*


---
*Updated javadoc and corrected key detection*

processTable and processGrid were looking for a LabelKey in columns - corrected to I18NKey


---
*Bintray upload changes*

dryRun=true by default (set by krail-master), unless overridden by individual projects


---
