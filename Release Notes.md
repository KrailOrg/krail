### Release Notes for krail 0.9.6

This version updates to Vaadin 7.5.3.  Integration of ```Option``` and I18N with persistence is substantially simplified, and a substantial number of small fixes and enhancements included.

#### Change log

-   [377](https://github.com/davidsowerby/krail/issues/377): Remove File based sitemap loader 
-   [429](https://github.com/davidsowerby/krail/issues/429): fluent supportedLocale() & defaultLocale() in I18NModule
-   [430](https://github.com/davidsowerby/krail/issues/430): BundleWriter for Database
-   [432](https://github.com/davidsowerby/krail/issues/432): Export I18NKeys as part of System Admin 
-   [433](https://github.com/davidsowerby/krail/issues/433): Vaadin 7.5.0
-   [438](https://github.com/davidsowerby/krail/issues/438): OptionPopup not scrollable
-   [439](https://github.com/davidsowerby/krail/issues/439): Vaadin 7.5.1
-   [444](https://github.com/davidsowerby/krail/issues/444): DefaultOptionSource - should not need to be a @Singleton
-   [446](https://github.com/davidsowerby/krail/issues/446): InMemory "database" reader could be provided
-   [447](https://github.com/davidsowerby/krail/issues/447): Explicit Vaadin version fixed
-   [448](https://github.com/davidsowerby/krail/issues/448): OptionDao not valid with zero config
-   [449](https://github.com/davidsowerby/krail/issues/449): Vaadin 7.5.2
-   [450](https://github.com/davidsowerby/krail/issues/450): Revisit Option and Pattern (persistence) defaults
-   [452](https://github.com/davidsowerby/krail/issues/452): Shiro 1.2.4
-   [454](https://github.com/davidsowerby/krail/issues/454): Option conversion for all core Option types
-   [455](https://github.com/davidsowerby/krail/issues/455): Vaadin 7.5.3


#### Dependency changes

   test compile dependency version changed to: q3c-testUtil:0.7.11
   test compile dependency version changed to: krail-testUtil:1.0.15

#### Detail

*Pre-release - update version descriptions and version properties*


---
*Tutorial step 09 completed*


---
*Tutorial step 08a verified and updated*


---
*Tutorial step 08 verified and updated*


---
*Tutorial step 07 verified*


---
*Tutorial step 06 verified and updated*


---
*Tutorial step 05 verified and updated*


---
*Tutorial step 04 verified*


---
*Tutorial step 03 verified*


---
*Tutorial step 02 verified*


---
*Tutorial step 01 verified*


---
*Fix [455](https://github.com/davidsowerby/krail/issues/455) Vaadin 7.5.3*


---
*Fix [454](https://github.com/davidsowerby/krail/issues/454) String conversion provided for all core Option data types*

OptionList and AnnotationOptionList added.  Both retain a predictable order, and with ```DefaultOptionStringConverter``` OptionList will take any element type that is also supported by ```DefaultOptionStringConverter```


---
*Fix [449](https://github.com/davidsowerby/krail/issues/449) Vaadin 7.5.2*


---
*Fix [452](https://github.com/davidsowerby/krail/issues/452) Shiro 1.2.4*


---
*Fix [450](https://github.com/davidsowerby/krail/issues/450) Pattern and Option defaults*

Significant change to the way I18N patterns are managed through to persistence.  PatternDao implementations replace the BundleReader/BundleWriter implementations
Support for properties files as persistence for Krail core dropped, but there is an open issue [451](https://github.com/davidsowerby/krail/issues/451) to provide utility support.
I18NModule now enables simpler selection of I18N pattern sources, and also add targets for use with auto-stub.
PatternSourceProvider added to simplify injection of pattern related Guice configuration
PatternUtility completely re-implemented
Option defaults not changed
Test coverage improved


---
*Fix [447](https://github.com/davidsowerby/krail/issues/447) Tutorial updated*

No longer need to specify the Vaadin version.
Tutorial intro includes the need for Gradle 2.5, as required by the plugin update


---
*Fix [448](https://github.com/davidsowerby/krail/issues/448) InMemory option dao available by default*

The ```DefaultBindingManager``` calls InMemoryModule.provideOptionDao() in addPersistenceModules.
GuiceModuleTestBase created for Spock tests


---
*Fix [446](https://github.com/davidsowerby/krail/issues/446) InMemoryBundleReader and Writer implemented by default*

Developer does not need to provide implementations now.
Tests added, and merged into a single Spock test, coverage improved


---
*Fix [444](https://github.com/davidsowerby/krail/issues/444) Removed @Singleton from DefaultOptionSource*


---
*Fix [432](https://github.com/davidsowerby/krail/issues/432) Validation keys added to export*

Validation reworked.  ValidationKey provides a complete set of I18NKey to replace the Apache BVal string based keys, and there is now no need for the developer to provide substitution.
 The translations provided by BVal have been transposed.
 Exporting ValidationKey added to PatternUtility and the Systems Management pages
 The option to include the field name in the validation message has been removed - as this is based on the property name it also requires translation, which is better done outside of the validation process.


---
*See [432](https://github.com/davidsowerby/krail/issues/432) Export keys added to PatternUtility*

Does not yet include Validation keys, and is still to be added to a Sys Admin page

Fix [430](https://github.com/davidsowerby/krail/issues/430) BundleWriter for Database.  Much of the BundleWriter was actually done earlier

Dev guide - correction to I18N documentation

See [432](https://github.com/davidsowerby/krail/issues/432) Export keys

Export of LabelKey, DescriptionKey and MessageKey made available in @Experimental page in SystemAdminPages.  ValidationKeys still to do
 Translate API extended to include option to disable check for supported locales

See davidsowerby/krail[432](https://github.com/davidsowerby/krail/issues/432) ValidationKey added

Keys defined for all the JSR303 and the 2 additional BVal validation messages.  Translations for en, es,de,it transferred from BVal


---
*Fix [439](https://github.com/davidsowerby/krail/issues/439) Vaadin 7.5.1*


---
*Fix [377](https://github.com/davidsowerby/krail/issues/377) Removed FileSitemap*


---
*refactoring krail-jpa*


---
*IDE incorrectly removing "unnecessary" cast.  Using explicit intermediate variable to overcome*

Cast had gone missing from ```DatabaseBundleWriterBase```
Javadoc correction to ```Dao```


---
*See [krail-jpa 14](https://github.com/davidsowerby/krail-jpa/issues/14) Pattern persistence separated from Option*

 PatternEntity  renamed PatternEntity_LongInt to reflect that it is only for use with Long id and integer version
 Changed to principle of sub-classing ```DatabaseBundleReaderBase``` and ```DatabaseBundleWriterBase```, so that the sub-classes can be added to the bundle sources in ```18NModule```
 CombinedContainerProvider removed


---
*OptionModule active source set by default*

to maintain principle of working "out-of-the-box", active source set to InMemory in ```DefaultBindingManager```


---
*See [krail-jpa 12](https://github.com/davidsowerby/krail-jpa/issues/12)  OptionDao.write returns saved entity*


---
*Separated OptionContainer from Pattern*

Data now loads correctly but issue with Jpa method of storing entity (davidsowerby/krail-jpa#12)


---
*Fix [438](https://github.com/davidsowerby/krail/issues/438) OptionPopup scrolls*

Used Panel to hold baseLayout


---
*OptionView shows active and selected options sources*

Renaming to DefaultOptionSource
OptionKey is no longer immutable, but there is a setter only for the default value.  This is useful for setting a common default for groups of options (see ```SourcePanel``` for an example)


---
*CombinedContainerProvider added, enables a single point of selection for Option or Pattern containers.  Use with caution*


---
*InMemoryContainer added.  ContainerType move to krail core from krail-jpa*


---
*VaadinContainerProvider returns DefaultJpaContainerProvider with correct EntityManagerProvider injected*


---
*Dynamic selection of OptionDao  (see #434) and PatternDao (see #435)*

Both these DAOs now use annotations for InMemory and JPA persistence units - even an unannotated JPA PU actually gets an annotated (@CoreDao)  DAO.

 These allows the implementation used by  Krail core to be changed externally, and dynamically through CoreOptionDaoProvider and CorePatternDaoProvider

 Many of the changes in this commit are a result of  enforcing a more consistent apprach tot he use ofOptional in the Option framework.  All values to and from Option are natively typed.  All values to and from OptionCache, DefaultOptionCacheLoader,  OptionDao are wrapped in Optional.

 Selection of the active Dao (and therefore persistence source) is via the activeDao() methods of the OptionModule and I18NModule


---
*Fix [433](https://github.com/davidsowerby/krail/issues/433) Vaadin 7.5.0*


---
*Tutorial update for use of @CoreDao*


---
*Persistence Integration and JPA  improvements*

Some small changes in Krail core to make Dao structure simpler
Major changes in krail-jpa to provide a single, simplified, transaction-managed generic Dao (```BaseJpaDao```).  krail-jpa also has a simplified API for instance configuration.  This includes removing BlockDao and StatementDao which were not very useful in practice.
Generic Dao can be instantiated for any persistence unit, using annotation identification.
The test-app  adds tests for Dao providers to ensure that bindings are operating correctly

All DAOs now are annotated (for krail core the annotation is @CoreDao).  This makes it possible to configure any future persistence module to provide persistence for Krail core.    All persistence modules should now implement KrailPersistenceModule

  The persistence elements of Option and I18N have been taken out of their respective Guice modules, and placed in a separate InMemoryModule - the latter now represents the equivalent of a persistence module and is therefore readily interchangeable.

  A lot of tests were changed to incorporate TestPersistenceModule (which is just InMemoryModule with some pre-configuration)


---
*Changed Person<Integer,Integer> to Person<Long,Integer>  to use with StandardJPADao*


---
*Tutorial 08a complete*


---
*Javadoc only*


---
*PatternDao binding moved to I18NModule*

PatternDao binding was in DataModule, but this caused a massive and inappropriate dependency on DataModule


---
*Additional debug statements in DefaultI18NFiledScanner, no code changes*


---
*Default PatternDao added*


---
*Tests added, no changes needed*


---
*Tutorial 08a standard components done*


---
*Tutorial 08 completed*


---
*Separated InMemoryPatternStrore, Singleton scoped*


---
*Added cleanup() to clearCache() methods for immediacy*


---
*Tutorial 08 additions*


---
*I18N integration improvements*

Redundant check for default locale in supported locales removed from DefaultCurrentLocale
Added database bundle reader to I18NModule
Created InMemoryPatternDao
DataModule cleaned up and fluent method added


---
*Fix [429](https://github.com/davidsowerby/krail/issues/429)  Fluent methods in I18NModule*

All relevant methods now fluent, so this module could now be fully configured without sub-classing.
Also made naming more consistent - the terms 'source' and 'reader' had been mixed up
Some breaking changes because of the renaming


---
*See [429](https://github.com/davidsowerby/krail/issues/429) I18NModule changes*

Removed the code relating to 'excludeDrillDown' - this was made redundant by [389](https://github.com/davidsowerby/krail/issues/389) but got left behind


---
*Tutorial 08 single language only*


---
*Tutorial 02 - removed creation of Caption, it is not used until Tutorial 08*


---
