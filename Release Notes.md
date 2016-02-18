### Release Notes for krail 0.9.9

This version updates to Vaadin 7.6.3.  Changes concentrate on separating the build from the master project to prepare move to continuous delivery.  Some small fixes included, Tutorial updated

#### Change log

-   [522](https://github.com/davidsowerby/krail/issues/522): Gitter tag on README
-   [524](https://github.com/davidsowerby/krail/issues/524): Check Tutorial
-   [525](https://github.com/davidsowerby/krail/issues/525): Travis build 13 fails
-   [526](https://github.com/davidsowerby/krail/issues/526): Move test base and helpers to enable JPA separation
-   [530](https://github.com/davidsowerby/krail/issues/530): ConfigurationModuleBase.addConfig() needs to be public
-   [532](https://github.com/davidsowerby/krail/issues/532): Translate must be visible in ViewBase sub-classes
-   [533](https://github.com/davidsowerby/krail/issues/533): Improve logging for push
-   [534](https://github.com/davidsowerby/krail/issues/534): Translation tests failing
-   [535](https://github.com/davidsowerby/krail/issues/535): Broadcaster.broadcast() needs to be easier to call
-   [536](https://github.com/davidsowerby/krail/issues/536): Tutorial should reference push config info in Vaadin documentation
-   [537](https://github.com/davidsowerby/krail/issues/537): Gradle (only) fails to find OptionDaoTestBase
-   [539](https://github.com/davidsowerby/krail/issues/539): Vaadin 7.6.3


#### Dependency changes


#### Detail

*version info updated*


---
*Fix [539](https://github.com/davidsowerby/krail/issues/539) Vaadin 7.6.3*


---
*Fix [537](https://github.com/davidsowerby/krail/issues/537) Gradle needs groovy files in groovy folder*


---
*Fix [536](https://github.com/davidsowerby/krail/issues/536) Tutorial reference to Vaadin Push notes*


---
*See [535](https://github.com/davidsowerby/krail/issues/535) Test amendment*

Should have been with previous commit


---
*Fix [535](https://github.com/davidsowerby/krail/issues/535) Call to Broadcaster.broadcast with component*

A bit more logging added in PushMessageRouter


---
*Fix [534](https://github.com/davidsowerby/krail/issues/534) Translation test failures*

TestLabelKey had been moved to testUtil, but its associated translations had not


---
*Fix [533](https://github.com/davidsowerby/krail/issues/533) Logging for push*

Removed clear text messages from logging, replaced with UIKey (sender) identifier and message id


---
*Fix [524](https://github.com/davidsowerby/krail/issues/524) Tutorial updates*

Updates to krail 0.9.9, for sections 1-12 inclusive


---
*See [524](https://github.com/davidsowerby/krail/issues/524) Tutorial updates*

Updates to krail 0.9.9, for sections 1-9 inclusive


---
*Fix [532](https://github.com/davidsowerby/krail/issues/532) ViewBase getTranslate() added*


---
*Fix [530](https://github.com/davidsowerby/krail/issues/530) ConfigurationModuleBase addConfig() made public*


---
*Fix [526](https://github.com/davidsowerby/krail/issues/526) Test base and helpers moved to testUtil*

Also refactored packages to mirror the core structure


---
*Fix [525](https://github.com/davidsowerby/krail/issues/525) Removed UnitTestFor annotation*

Not used consistently, serves no real purpose


---
*Readme date*


---
*See [513](https://github.com/davidsowerby/krail/issues/513) Badges added*

Travis and Coveralls badges added.  Item 8 complete


---
*See [513](https://github.com/davidsowerby/krail/issues/513) Separation from master*

Items 1, 2 & 3 completed


---
*See [513](https://github.com/davidsowerby/krail/issues/513) .travis.yml*

Items 4 & 5 done (should not actually copy coveralls.yml, the key is different)


---
*See [513](https://github.com/davidsowerby/krail/issues/513) Move Spock tests*

Item 6 complete, all Spock tests in the src/test/groovy folder


---
*Fix [522](https://github.com/davidsowerby/krail/issues/522) Gitter badge added*

Also added licence as badge


---
