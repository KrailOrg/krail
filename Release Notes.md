### Release Notes for krail 0.7.9

This version provides an upgrade to Vaadin 7.3.9,removes the Gradle project files from Git and provides some minor fixes and refactoring

#### Change log

-   [311](https://github.com/davidsowerby/krail/issues/311): Upgrade to Vaadin 7.3.9
-   [313](https://github.com/davidsowerby/krail/issues/313): Move ExampleUtil
-   [314](https://github.com/davidsowerby/krail/issues/314): Move util package to q3c-testUtil


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.6
   test compile dependency version changed to: q3c-testUtil:0.7.3

#### Detail

Release notes and version.properties generated

---
*Set version information*


---
*Updated README files to conform to q3c-gradle format.  See davidsowerby/krail-master#315.*


---
*Fix [314](https://github.com/davidsowerby/krail/issues/314) Moved util package to q3c-testUtil*


---
*Fix [313](https://github.com/davidsowerby/krail/issues/313) ExampleUtil moved to [q3c-testUtil](https://github.com/davidsowerby/q3c-testUtil) project*


---
*Fix [311](https://github.com/davidsowerby/krail/issues/311) Upgrade to Vaadin 7.3.9. Also corrects fix to [305](https://github.com/davidsowerby/krail/issues/305)*

Upgraded to Vaadin 7.3.9.  The earlier fix to [305](https://github.com/davidsowerby/krail/issues/305) was incomplete, but is now correct.  The registeredAnnotations and registeredvalueAnnotations are now bound using annotations independent of I18N and I18NValue.  This removes, for example, the conflict of having an I18N annotation on a class where there also needs to be another @BindingAnnotation


---
*#Fix 305. Removes spurious @BindingAnnotation's from I18N annotations*


---
