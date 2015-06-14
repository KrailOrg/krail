### Release Notes for krail 0.9.5

This version updates to Vaadin 7.4.8, corrects some errors in the Shiro integration, provides some minor fixes and adds new sections to the Tutorial

#### Change log

-   [404](https://github.com/davidsowerby/krail/issues/404): Vaadin 7.4.7
-   [410](https://github.com/davidsowerby/krail/issues/410): NPE from SubjectIdentifier when no subject principal
-   [411](https://github.com/davidsowerby/krail/issues/411): Login status panel not correct after logout
-   [418](https://github.com/davidsowerby/krail/issues/418): StandardShiroModule improvements
-   [419](https://github.com/davidsowerby/krail/issues/419): Option permission javadoc
-   [421](https://github.com/davidsowerby/krail/issues/421): Shiro SecurityUtils.getSubject() mismatch with VaadinSession
-   [422](https://github.com/davidsowerby/krail/issues/422): optionNavTreeVisible=false in DefaultApplicationUI doesn't hide panel
-   [423](https://github.com/davidsowerby/krail/issues/423): Two optionPopup.popup on single view don't work
-   [424](https://github.com/davidsowerby/krail/issues/424): Vaadin 7.4.8


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.14
   test compile dependency version changed to: q3c-testUtil:0.7.10

#### Detail

*Corrects to tests, and remove casts from DefaultApplicationUI*


---
*Updated version information*


---
*Tutorial updates*


---
*Fix [422](https://github.com/davidsowerby/krail/issues/422) NavTree and SplitPanel disappear when NavTree option deselected*

The current solution requires the user to manually refresh the browser to activate changes to options.  Issue [426](https://github.com/davidsowerby/krail/issues/426) has been raised to look at making the changes immediate


---
*Fix [423](https://github.com/davidsowerby/krail/issues/423) OptionPopup can be re-used*

The same OptionPopup instance can be re-used for a different OptionContext


---
*Fix [411](https://github.com/davidsowerby/krail/issues/411) Login status panel shows correct state*


---
*Fix [419](https://github.com/davidsowerby/krail/issues/419) Javadoc correction*

Tutorial typos


---
*Tutorial 06 update from review comment*


---
*Tutorial 07 complete*


---
*Fix [424](https://github.com/davidsowerby/krail/issues/424) Vaadin 7.4.8*


---
*Fix [421](https://github.com/davidsowerby/krail/issues/421) Subject and VaadinSession fixed*

The MethodInterceptor implementations for all the Shiro annotations have been replaced in order to use SubjectProvider.get() instead of SecurityUtils.getSubject().  The ShiroAopModule is no longer used, and is replaced by the KrailShiroAopModule.

SubjectProvider is an interface now, to overcome circular dependencies in Guice

DefaultSubjectProvider now properly manages the relationship between VaadinSession and Subject.

NotAUserException and NotAGuestException added to more clearly identify cause of authorisation failure, if required.  Handlers for both added, and incorproated into KrailErrorHandler.

Fix [418](https://github.com/davidsowerby/krail/issues/418) StandardShiroModule is now DefaultShiroModule, and a fluent bindRealm() added


---
*Updated acknowledgements in README*


---
*Tutorial 06 showConfig instead of loadConfig*


---
*Tutorial 06 completed*


---
*Tutorial 05 amended*


---
*Merge branch 'master' into develop*

Conflicts:
	README.md


---
*Tutorial 05 completed (Options)*


---
*Fix [404](https://github.com/davidsowerby/krail/issues/404) Upgrade to Vaadin 7.4.7*


---
*Fix [410](https://github.com/davidsowerby/krail/issues/410) SubjectIdentifier handles null principal*


---
