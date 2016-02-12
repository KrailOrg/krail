### Release Notes for krail 0.9.8

This version updates to Vaadin 7.6.2.  Most of the change are about improving quality through static code analysis and increased testing code coverage.
Packages have been re-organised and testUtil merged back into the core.  Thread safety has been addressed

#### Change log

-   [85](https://github.com/davidsowerby/krail/issues/85): Enable Shiro caching
-   [89](https://github.com/davidsowerby/krail/issues/89): login default button
-   [236](https://github.com/davidsowerby/krail/issues/236): Should ResourceUtils be singleton?
-   [244](https://github.com/davidsowerby/krail/issues/244): Check Singletons
-   [342](https://github.com/davidsowerby/krail/issues/342): Scope differences incorrectly handled, Navigator and UserStatus
-   [400](https://github.com/davidsowerby/krail/issues/400): Navigation components, confused when parent has all children hidden
-   [415](https://github.com/davidsowerby/krail/issues/415): ApplicationConfiguration override opposite order to Option
-   [416](https://github.com/davidsowerby/krail/issues/416): ApplicationConfiguration fluent interface to addConfig
-   [417](https://github.com/davidsowerby/krail/issues/417): ApplicationConfiguration should be an interface
-   [459](https://github.com/davidsowerby/krail/issues/459): DefaultOptionPopup not converting data types
-   [460](https://github.com/davidsowerby/krail/issues/460): Field mapping for In Memory OptionEntity incorrect
-   [492](https://github.com/davidsowerby/krail/issues/492): Vaadin 7.6.0
-   [493](https://github.com/davidsowerby/krail/issues/493): org.reflections 0.9.10
-   [494](https://github.com/davidsowerby/krail/issues/494): net.jodah:typetools 0.4.3
-   [496](https://github.com/davidsowerby/krail/issues/496): Vaadin 7.6.1
-   [497](https://github.com/davidsowerby/krail/issues/497): Acknowledge FindBugs
-   [499](https://github.com/davidsowerby/krail/issues/499): benefit in separate testUtil module?
-   [501](https://github.com/davidsowerby/krail/issues/501): ShiroMethodInterceptor using field injection
-   [502](https://github.com/davidsowerby/krail/issues/502): Review use of exceptionToThrow in ShiroMethodInterceptor
-   [503](https://github.com/davidsowerby/krail/issues/503): OptionList and AnnotationOptionList missing from DefaultOptionStringConverter.convertStringToValue
-   [507](https://github.com/davidsowerby/krail/issues/507): Make OptionCacheKey generic
-   [508](https://github.com/davidsowerby/krail/issues/508): Restructure packages
-   [509](https://github.com/davidsowerby/krail/issues/509): Reduce duplication in Option handling
-   [510](https://github.com/davidsowerby/krail/issues/510): Restrict OptionCache to VaadinSessionScoped?
-   [511](https://github.com/davidsowerby/krail/issues/511): Option.set method parameters reversed
-   [512](https://github.com/davidsowerby/krail/issues/512): OptionKey should be immutable
-   [514](https://github.com/davidsowerby/krail/issues/514): Vaadin 7.6.2
-   [515](https://github.com/davidsowerby/krail/issues/515): Cannot test "destroy context with null injector"
-   [517](https://github.com/davidsowerby/krail/issues/517): DefaultClassPatternDao test fails
-   [520](https://github.com/davidsowerby/krail/issues/520): Merge ViewBaseI18N into ViewBase
-   [521](https://github.com/davidsowerby/krail/issues/521): Improve NavigationState


#### Dependency changes


#### Detail

*Release notes and version.properties generated*


---
*Version files updated*


---
*Fix [521](https://github.com/davidsowerby/krail/issues/521) Improve NavigationState*

Addressed consistency, exception thrown if content accessed and state is inconsistent
Deprecated non-fluent setters
Logic for correct interpretation of URI structure still resides in URIFragmentHandler, so that can be implemented differently if a developer so requires


---
*See [500](https://github.com/davidsowerby/krail/issues/500) code coverage 90%*


---
*Fix [520](https://github.com/davidsowerby/krail/issues/520) Merged ViewBase18N into ViewBase*

Added interface NamedAndDescribed with name and description I18NKeys.  KrailView and Service extend this interface and ViewBase / AbstractService provide the translation.
This means that all views are now constructed with a Translate parameter
ScopedUI makes use of this to add view name to the page title
MockTranslate added to simplify testing with I18N


---
*See [500](https://github.com/davidsowerby/krail/issues/500), code coverage 88%*


---
*Code coverage 87%*


---
*Code coverage 86%*

Fix [459](https://github.com/davidsowerby/krail/issues/459) Data conversion for OptionPopup

Data conversion had been omitted, so was only working when the option value type and field value type were the same.  Corrected using Vaadin's DefaultConverterFactory, but expect that to be changed again under [518](https://github.com/davidsowerby/krail/issues/518).


---
*Fix [514](https://github.com/davidsowerby/krail/issues/514) Vaadin 7.6.2*


---
*Fix [517](https://github.com/davidsowerby/krail/issues/517) Tests corrected*

Needed   patternCacheKey.getKeyAsEnum() resolving from mock


---
*[q3c-testUtil 15](https://github.com/davidsowerby/q3c-testUtil/issues/15) separated q3c-testUtil from master build*


---
*Fix [515](https://github.com/davidsowerby/krail/issues/515) injector made package visible*

 Enables testing of null injector
 Gradle wrapper files re-instated


---
*[513](https://github.com/davidsowerby/krail/issues/513) backed out of separation from master*

Deferring 513 until later, reverting to centralised build under 'master'


---
*[513](https://github.com/davidsowerby/krail/issues/513) adding coverage via coveralls*


---
*[513](https://github.com/davidsowerby/krail/issues/513) handling I18NKey - Enum conversions*

Only casues a problem with Travis build


---
*[513](https://github.com/davidsowerby/krail/issues/513) specify java 8*


---
*[513](https://github.com/davidsowerby/krail/issues/513) Build fails with gradlew*

Despite being committed, Travis reports: "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"


---
*[513](https://github.com/davidsowerby/krail/issues/513) Separated krail core from master project*


---
*[513](https://github.com/davidsowerby/krail/issues/513) travis.yml missed the '.' prefix*


---
*[513](https://github.com/davidsowerby/krail/issues/513) travis.yml added*

Ticket cannot be closed until Travis build successful


---
*[krail-master 36](https://github.com/davidsowerby/krail-master/issues/36) rename build file*

reverted to 'build.gradle' to enable Travis


---
*Fix [499](https://github.com/davidsowerby/krail/issues/499) krail-testUtil merged back into core*


---
*Fix [400](https://github.com/davidsowerby/krail/issues/400) Navigation components aware of excluded nodes*

TreeCopy calls on NodeModifier for implementation specific responses for a node with no children (a 'leaf'), but also adds an 'override' so that TreeCopy can enforce a limited depth copy.
Net result is that navigation components correctly display regardless of whether nodes are excluded or not
Some unused classes deleted


---
*Fix [502](https://github.com/davidsowerby/krail/issues/502) exceptionToThrow removed from ShiroMethodInterceptor*

There was no value to the previous arrangement
Also corrected SourcePanel - missed from previous commit


---
*Fix [512](https://github.com/davidsowerby/krail/issues/512) OptionKey is immutable*

Only effect is on an experimental page, which can be addressed later


---
*Fix [509](https://github.com/davidsowerby/krail/issues/509) Reduced duplication of Option handling*

Restructured OptionDao to use a delegate for different persistence providers, currently just InMemory and JPA.  This removes duplicated logic in krail-jpa, and also enables Option tests to be inherited.
OptionStringConverter renamed OptionElementConverter


---
*Fix [503](https://github.com/davidsowerby/krail/issues/503) AnnotationOptionList and OptionList inlcuded in conversion*

OptionListConverter invoked directly from the OptionDao
renamed OptionStringConverter OptionElementConverter
AnnotationOptionList added to the OptionElementConverter


---
*Fix [511](https://github.com/davidsowerby/krail/issues/511) Option.set parameter order*

Changed to be in key-value order


---
*Fix [497](https://github.com/davidsowerby/krail/issues/497) Acknowledge FindBugs*


---
*Fix [342](https://github.com/davidsowerby/krail/issues/342) Multi-user testing*

Changes are actually in testApp


---
*Fix [460](https://github.com/davidsowerby/krail/issues/460) Incorrect InMemory mapping*

Completely replaced the unnecessarily complex InMemory store with a much simpler version.

Refactored classes that were missed under [508](https://github.com/davidsowerby/krail/issues/508) - a number of persistence related classes were in core i18n and option packages.


---
*Fix [510](https://github.com/davidsowerby/krail/issues/510) DefaultOptionCache is VaadinSessionScoped*

 Using annotation instead of binding.  Removed userId from OptionCacheKey


---
*Fix [508](https://github.com/davidsowerby/krail/issues/508) restructured packages*


---
*Fix [507](https://github.com/davidsowerby/krail/issues/507) OptionCacheKey is generic*


---
*Javadoc and documentation*


---
*[500](https://github.com/davidsowerby/krail/issues/500) Code coverage 83%*

DefaultOptionStringConverter corrected:  LocalDateTime format, recognition of I18NKey and Enum
StackTraceUtil replaced by commons.lang.ExceptionUtil


---
*Fix [501](https://github.com/davidsowerby/krail/issues/501) Removed field injection from ShiroMethodInterceptor*

Replaced by using providers to avoid the Guice Lifecycle conflict that the field injection was intended to avoid


---
*Pattern and Option Dao re-structure*

Needed to accommodate restucutruin of Daos, see [krail-jpa 18](https://github.com/davidsowerby/krail-jpa/issues/18)


---
*See [340](https://github.com/davidsowerby/krail/issues/340)  FindBugs analysis complete*


---
*Fix [417](https://github.com/davidsowerby/krail/issues/417) ApplicationConfiguration as an interface*

Also a few minor code changes from quality review, and improved test coverage


---
*Fix [416](https://github.com/davidsowerby/krail/issues/416) Fluent addConfig() added*


---
*Fix [415](https://github.com/davidsowerby/krail/issues/415) Config layers priority the same as Option*

Previously the configuration layers in ApplicationConfiguration were the opposite way round to that used in Option.  Both now have the lowest number as the highest priority


---
*Fix [496](https://github.com/davidsowerby/krail/issues/496) Vaadin 7.6.1*

Also Guava to 19.0


---
*Fix [244](https://github.com/davidsowerby/krail/issues/244) Singleton Thread safety*

All Singletons checked for thread safety

Further changes to Services design, partly to overcome deadlocks.  RelatedServicesExecutor is delegate to start/stop dependencies/dependants
The lifecycle is abbreviated with a Cause  added, mainly  for STOPPED and FAILED states


---
*See [244](https://github.com/davidsowerby/krail/issues/244).  MasterSitemap thread safety*

Introduced SitemapQueue, which is not yet necessary but will help with [254](https://github.com/davidsowerby/krail/issues/254), as it enables the switching of MasterSitemap versions.  A timeout has been added for loading of the MasterSitemap.

MasterSitemap is now locked after initial load, which removes the need for managing concurrent thread access, as it is now read-only once loaded.

A common BusProvider interface has been added

ApplicationConfiguration considered for making Threadsafe.   See [495](https://github.com/davidsowerby/krail/issues/495) for explanation.  Tutorial and Javadoc updated

A number of small changes made as part of quality review


---
*Fix [236](https://github.com/davidsowerby/krail/issues/236) Replaced static utilities*

ClassNameUtils and ResourceUtils are interfaces with default implementations, enabling replacement by developers.  Bindings are in UtilsModule


---
*Fix [89](https://github.com/davidsowerby/krail/issues/89) LoginView Submit button is default*

'Enter' submits credentials


---
*Fix [85](https://github.com/davidsowerby/krail/issues/85) Shiro cache added*

Cache implementation can be changed by overriding DefaultShiroModule.bindCacheManager()


---
*Fix [492](https://github.com/davidsowerby/krail/issues/492) Vaadin 7.6.0*


---
*Fix [494](https://github.com/davidsowerby/krail/issues/494) net.jodah update*

net.jodah:typetools:0.4.3


---
*Fix [493](https://github.com/davidsowerby/krail/issues/493) update org.reflections to 0.9.10*


---
