### Release Notes for krail 0.8.1

This version upgrades to Vaadin 7.4.0, and provides a flexible configuration options through the use of UserHierarchy implementations.

#### Change log

-   [198](https://github.com/davidsowerby/krail/issues/198): Implement System Options
-   [298](https://github.com/davidsowerby/krail/issues/298): Add description to user option
-   [299](https://github.com/davidsowerby/krail/issues/299): Cache for UserOption
-   [328](https://github.com/davidsowerby/krail/issues/328): DefaultUserOption.get() check for null
-   [329](https://github.com/davidsowerby/krail/issues/329): Upgrade to Vaadin 7.3.10
-   [330](https://github.com/davidsowerby/krail/issues/330): Rename UserOption to Option
-   [333](https://github.com/davidsowerby/krail/issues/333): Upgrade to Vaadin 7.4


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.8
   test compile dependency version changed to: q3c-testUtil:0.7.5

#### Detail

*Updated version information*


---
*Fix [333](https://github.com/davidsowerby/krail/issues/333) Upgrade to Vaadin 7.4.0*

---

*Fix [298](https://github.com/davidsowerby/krail/issues/298) Option descriptions provided, reworked Option API*

An OptionDescriptor is provided to support, for example, the creation of a form allowing users to set option values, using I18NKeys for Option names and descriptions.
Simplified the Option API by using OptionKey rather than discrete values.


---
*Fix [299](https://github.com/davidsowerby/krail/issues/299) Option uses cache*

Using Guava cache, set up via a configuration object in OptionModule, with key construction supporting the use of highest, lowest and specific values from a UserHierarchy


---
*Fix [328](https://github.com/davidsowerby/krail/issues/328) Check DefaultOption is initialised*

A call to get() is checked to ensure that context has been set, and an explanatory exception raised if not.  Also renamed the configure() method to init()

---
Fix [198](https://github.com/davidsowerby/krail/issues/198) Options with UserHierarchy

UserHierarchy introduced to enable representation of any user-related hierarchy (org. structure, location etc).  This is now used by Option to provided hierarchical options where the topmost level (the user) overrides lower levels (which could be 'system' or anything else.  DefaultOptionStore has been modified to support the new structure.

Extended the null checking added in [328](https://github.com/davidsowerby/krail/issues/328) to all get, set and delete methods of DefaultOption

---
See [299](https://github.com/davidsowerby/krail/issues/299).  Cache added for ranked hierarchy

Modified the OptionCacheKey for use in highest, lowest and specific cases. API organised better for Dao and OptionStore.
Daos injected as providers. UserHierarchy added with support for highest and lowest rank.
Cache testing improved.  getLowest and getSpecifc added to Option.
OptionKey key now I18N.  BundleWriter outputs sorted alphabetically by key


---
*Fix [198](https://github.com/davidsowerby/krail/issues/198) Options with UserHierarchy*

UserHierarchy introduced to enable representation of any user-related hierarchy (org. structure, location etc).  This is now used by Option to provided hierarchical options where the topmost level (the user) overrides lower levels (which could be 'system' or anything else.  DefaultOptionStore has been modified to support the new structure.

Extended the null checking added in [328](https://github.com/davidsowerby/krail/issues/328) to all get, set and delete methods of DefaultOption


---
*Fix [328](https://github.com/davidsowerby/krail/issues/328) Check DefaultOption is initialised*

A call to get() is checked to ensure that context has been set, and an explanatory exception raised if not.  Also renamed the configure() method to init()


---
*Fix [330](https://github.com/davidsowerby/krail/issues/330) rename UserOption to Option*


---
*Fix [329](https://github.com/davidsowerby/krail/issues/329) Upgrade to Vaadin 7.3.10*


---
