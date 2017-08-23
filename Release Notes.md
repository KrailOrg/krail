## Release Notes for krail 0.10.0.0

### Introduction

This release is a major refactor to extract some elements which can actually stand alone from Krail itself - I18N, Option and Application Configuration.  All have an API and an implementation project.
EventBus and Service packages have their APIs extracted into separate projects, but the implementation is still in the Krail core.

The aim is to prepare for upgrade to Vaadin 8, and then assess the possibility of moving to Eclipse Vert.x  

Vaadin 8 will follow shortly, but a Vaadin 7 branch will be maintained for a while.


### Dependency updates

All the major components have been updated to their latest version - Guice, Shiro and Vaadin 7, along with a number of smaller compile and tsestCompile dependencies.  


## The changes
Unfortunately there are a LOT of changes which will affect existing Krail apps. Many are limited to package changes, but some code changes were needed to achieve effective separation of concerns

### Testing and Mocks

A number of packages now contain Mocks and test support classes - this is to avoid recreating Mocks individually within a wide range of tests. This approach is still fairly ad-hoc, but it worth checking to see if there is a 'test' package before setting up mocks.


## Functional Code Changes

- `UserModule.bindUserHierarchies()` moved to `OptionModule` as a more relevant location 
- binding of `SimpleHierarchy` moved to `KrailOptionModule`, overriding a binding to new `DefaultUserHierarchy`, which removes dependency on Shiro
- `I18NModule` split into  new `I18NModule` in **i18n** and `VaadinI18NModule` in the core.  The latter overrides binding methods to use Vaadin related scopes
- `DefaultTranslate` updated to use "strictness" provided by `MessageFormat2`.  Existing code will behave as before.
- Scope of `DefaultOptionCache` is set in `OptionModule` instead of by annotation
- `uk.q3c.krail.option.OptionPermissionVerifier` used in `OptionBase` to separate Shiro from `Option`. `OptionPermission` is therefore only used with Shiro
- `OptionPermission` uses `OptionEditAction`.  `OptionPermission.Action is removed` 
- `OptionModule` binding of `OptionCache` scope removed to remove dependency on Vaadin
- `OptionContext.getOption()` changed to `OptionContext.optionInstance()`
- `OptionContext` is parameterised to enable response to different change event types.  
- `VaadinOptionContext` extends `OptionContext` and replaces most existing uses of `OptionContext`
- `OptionSource` and `DefaultOptionSource` have getContainer() method moved to `VaadinOptionSource` and `DefaultOptionSource`, to remove dependency on Vaadin
- `VaadinContainerProvider` is paramterised and moved to `uk.q3c.krail.persist` in **krail-persist-api**

## Name changes

- There were some annotations with names beginning with '@Default', which can cause a name clash, as most implementations are 'Defaultxxx'.  The following annotations changed:
    - `@DefaultUserHierarchy` renamed to  `@UserHierarchyDefault`
    - `@DefaultActiveOptionSource` renamed to `@ActiveOptionSourceDefault`
    - `@DefaultLocale` renamed to `@LocaleDefault`


- `uk.q3c.krail.i18n.I18NHostClassIdentifier` is now `uk.q3c.util.clazz.UnenhancedClassIdentifier`
- `AnnotationOptionList` had nothing to do with `Option`.  Renamed `AnnotationList` and moved to `uk.q3c.util.collection` in **q3c-util**
- `AnnotationOptionListConverter`, renamed `AnnotationListConverter` and moved to `uk.q3c.util.collection`
- `OptionElementConverter` renamed `DataConverter` and moved to `uk.q3c.util.data`.  `DataConverter` supports custom data item converters through Guice MapBinder 
- `DefaultOptionElementConverter` renamed `DefaultDataConverter` and moved to `uk.q3c.util.data`
- `OptionConverter` renamed `DataItemConverter` and moved to `uk.q3c.util.data` - it is used by `Option` but not specific to it
- `KrailPersistenceUnit` split into `I18NPersistenceEnabler` and `OptionPersistenceEnabler`
- `KrailPersistenceUnitHelper` split into `I18NPersistenceHelper` and `OptionPersistenceHelper`
- `ServicesClassGraph` becomes `ServiceClassGraph` 
- `ServicesGraph` becomes `ServiceGraph` 
- `ServicesInstanceGraph` becomes `ServiceInstanceGraph`
- `ServicesModel` becomes `ServiceModel` 
- `ServicesMonitor` becomes `ServiceMonitor` 
- `DefaultServicesMonitor` becomes `DefaultServiceMonitor` 
- `AbstractServicesGraph` becomes `AbstractServiceGraph` 
- `DefaultServicesClassGraph` becomes `DefaultServiceClassGraph` 
- `DefaultServicesInstanceGraph` becomes `DefaultServiceInstanceGraph` 
- `DefaultServicesModel` becomes `DefaultServiceModel` 
- `RelatedServicesExecutor` becomes `RelatedServiceExecutor` 

## Removals

- `BigDecimalConverter` deleted.  Conversion is already handled by `DataConverter` and is therefore redundant
- `TestByteEnhancementModule` (in test folder) - use `uk.q3c.util.test.AOPTestModule` instead
- `OptionList` removed, use `uk.q3c.util.data.collection.DataList` instead
- `CoreDao` - never used
- `OptionCollection` removed, use `uk.q3c.util.data.collection.DataList` instead
- `OptionPermission.Action` removed, use `OptionEditAction` instead

## Deprecated

- `MessageFormat`. Its replacement is no longer a static utility, but an interface `uk.q3c.util.text.MessageFormat2` with implementation `uk.q3c.util.text.DefaultMessageFormat`.  - `Translate` (and `DefaultMessageFormat` if used directly) offers 3 levels of strictness when handling mis-matches between arguments and parameters, **STRICT**, **STRICT_EXCEPTION** and **LENIENT**. The default, **STRICT**, behaves the same way as the previous version.
- `ReflectionUtils`.  `org.reflections` should be used instead
- `OptionList` & `OptionListConverter` - this is not practical for anything except in memory store

## Package changes, additional classes

These can be dealt with by the usual method of deleting failed import statements and letting the IDE find the new location.

### I18N

projects: **i18n**, **i18n-api**

Generic I18N classes moved from `uk.q3c.krail.core.i18n` to `uk.q3c.krail.i18n` and sub-packages.  Some (mostly Vaadin specific) classes remain in `uk.q3c.krail.core.i18n`

- `uk.q3c.krail.core.testutil.i18n` moved to `uk.q3c.krail.i18n.test` (in src folder)
- `MockTranslate` provided in `uk.q3c.krail.i18n.test` (in src folder) with more complete response (includes locale and arguments) and a bit more functionality as a Mock
- bindings for `I18NProcessor` and`I18NFieldScanner` moved from `I18NModule` to `VaadinI18NModule`
- `DefaultPatternUtility` remains in core with binding in `VaadinI18NModule` (see [open issue](https://github.com/davidsowerby/krail/issues/614))
- relevant `LabelKey` and `DescriptionKey` constants moved to **I18n** and renamed `PatternLabelKey` and `PatternDescriptionKey` (used primarily in `OptionKey` definitions)
- Use `KrailI18NModule` instead of `I18NModule`.  The latter is for use outside Krail

### Option
projects: **option**, **option-api**

All classes relating to the provision of In Memory "persistence", are within **option** and **option-api**, primarily for use in testing

- `OptionContainerProvider` moves to `uk.q3c.krail.option.persist.OptionContainerProvider`
- `uk.q3c.krail.core.testutil.option` moved to `uk.q3c.krail.option.test`
- `uk.q3c.krail.option.test.MockOptionPermissionVerifier` added
- `SimpleUserHierarchy` moved to `uk.q3c.krail.core.option.hierarchy.SimpleUserHierarchy`
- new `DefaultUserHierarchy`, removes dependency on Shiro
- Use `KrailOptionModule` instead of `OptionModule`.  The latter is for use outside Krail

### Data

- `uk.q3c.krail.core.data` moved to `uk.q3c.krail.persist` except:
- `uk.q3c.krail.core.data.DataModule` moved to `uk.q3c.krail.core.vaadin.DataModule`
- `uk.q3c.krail.core.data.I18NKeyConverter` moved to  `uk.q3c.krail.i18n.I18NKeyConverter`
- `uk.q3c.krail.core.data.Select` moved to  `uk.q3c.krail.i18n.jpa.Select`

### persist

- added `PersistenceConfigurationException`
- "InMemory" storage for both Option and I18N patterns are in the **krail-option** project
- `TestPersistenceModule` becomes `TestPersistenceModuleVaadin`


### services

- root package becomes `service` instead of `services`, and most of the classes also become `Servicexxxx` instead of `Servicesxxxx`

### uk.q3c.util

Project **q3c-util** contains all of the `uk.q3c.util` package

Moved from `uk.q3c.util` to `uk.q3c.krail.core.vaadin`

- `ID`
- `SourceTreeWrapper_VaadinTree`
- `TargetTreeWrapper_MenuBar`
- `TargetTreeWrapper_VaadinTree`
- `UserSitemapNodeCaption`

 

These all moved from `uk.q3c.util` to `uk.q3c.util.forest` in **q3c-util**:

- `BasicForest`
- `CaptionReader`
- `DefaultNodeModifier`
- `NodeFilter`
- `NodeModifier`
- `SourceTreeWrapper`
- `SourceTreeWrapper_BasicForest`
- `TargetTreeWrapper`
- `TargetTreeWrapper_BasicForest`
- `TargetTreeWrapperBase`
- `TreeCopy`
- `TreeCopyException`
- `TreeCopyExtension`

Others:
- `GuavaCacheConfiguration` moved from `uk.q3c.krail.core.persist.cache.common` to `uk.q3c.util.guava`
- `ReflectionUtils` moved from `uk.q3c.util` to `uk.q3c.util.reflect`

The following moved into a new package `uk.q3c.util.test`, to make test helper classes generally available

- `AOPTestModule` from `uk.q3c.util`
- new `EnhancedClass`
- `NotOnWeekends`  from `uk.q3c.util`
- `WeekendBlocker`  from `uk.q3c.util` - was in test folder, now in src

- new `UtilModule` for Guice bindings


## Tutorial

The tutorial has yet to be updated