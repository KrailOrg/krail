## Release Notes for krail 0.10.0.0

This release is a major refactor to extract some elements which can actually stand alone from Krail itself - notably this includes I18N and Options - and also to improve separation of concerns.

Unfortunately there are a LOT of changes which will affect existing Krail apps. Many are limited to package changes, but there are some code changes
were needed to achieve effective separation of concerns

## Name changes

- `uk.q3c.krail.i18n.I18NHostClassIdentifier` is now `uk.q3c.util.clazz.UnenhancedClassIdentifier`
- `AnnotationOptionList` had nothing to do with `Option`.  Renamed `AnnotationList` and moved to `uk.q3c.util.collection`
- `AnnotationOptionListConverter`, renamed `AnnotationListConverter` and moved to `uk.q3c.util.collection`
- `OptionElementConverter` renamed `DataConverter` and moved to `uk.q3c.util.data`.  `DataConverter` supports custom data item converters through Guice MapBinder 
- `DefaultOptionElementConverter` renamed `DefaultDataConverter` and moved to `uk.q3c.util.data`
- `OptionConverter` renamed `DataItemConverter` and moved to `uk.q3c.util.data` - it is used by `Option` but not specific to it
- `KrailPersistenceUnit` split into `I18NPersistenceEnabler` and `OptionPersistenceEnabler`
- `KrailPersistenceUnitHelper` split into `I18NPersistenceHelper` and `OptionPersistenceHelper`

## Removals

- `BigDecimalConverter` deleted.  Conversion is already handled by `DataConverter` and is therefore redundant
- `TestByteEnhancementModule` (in test folder) - use `uk.q3c.util.test.AOPTestModule` instead
- `OptionList` removed, use `uk.q3c.util.data.collection.DataList` instead


## Deprecated

- `MessageFormat`. Its replacement is no longer a static utility, but an interface `uk.q3c.util.text.MessageFormat2` with implementation `uk.q3c.util.text.DefaultMessageFormat`.  It also has new "strictness" functionality
- `ReflectionUtils`.  `org.reflections` should be used instead
- `OptionList` & `OptionListConverter` - this is not practical for anything except in memory store

## Package changes

These can be dealt with by the usual method of deleting failed import statements and letting the IDE find the new location.

### I18N

projects: **i18n**, **i18n-api**

Generic I18N classes moved from `uk.q3c.krail.core.i18n` to `uk.q3c.krail.i18n` and sub-packages.  Some (mostly Vaadin specific) classes remain in `uk.q3c.krail.core.i18n`


### Option
projects: **option**, **option-api**

### Data

- `uk.q3c.krail.core.data` moved to `uk.q3c.krail.persist` except:
- `uk.q3c.krail.core.data.DataModule` moved to `uk.q3c.krail.core.vaadin.DataModule`
- `uk.q3c.krail.core.data.I18NKeyConverter` moved to  `uk.q3c.krail.i18n.I18NKeyConverter`
- `uk.q3c.krail.core.data.Select` moved to  `uk.q3c.krail.i18n.jpa.Select`

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
