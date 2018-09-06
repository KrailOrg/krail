# Release notes for 0.16.12.0

## Overview

The original objective for this release was to provide a meaningful level of Forms support.  This required the ability to use a `FormConfiguration`, which necessitated some changes to `KrailView`

The release was also extended to include a simpler, lightweight UI as well (`SimpleUI`). This inspired some improvements in handling I18N for components

## Breaking Changes

### KrailView

A number of methods in `KrailView` passed `BeforeViewChangeBusMessage` or `AfterViewChangeBusMessage` as a parameter.  These have now all had the parameter removed.  The same information is now available in an instance of `NavigationStateExt`, which is passed via the *beforeBuild* method 

### Refactoring / renaming

- `DataModule` becomes `ConverterModule` and is moved to uk.q3c.krail.core.form
- `ConverterFactory` and associated classes moved to uk.q3c.krail.core.form
- `UserSitemapSorter` becomes `UserSitemapNodeComparator`
- `SubPagePanel` and `Breadcrumb` replaced by `PageNavigationPanel`


### Navigator
- error() method has been removed - this does not play well when there is an error in the Sitemap (see also the notes on MessageBox and KrailErrorHandler)


## Other Changes

### SimpleUI

A lightweight UI, `SimpleUI`, is available.  It is suggested that this is used where the primary user audience is expected to be using mobile.

There is still more work to do on this, see issues #736 and #737 

### ScopedUI

Previously the view display panel was always set to size full.  This proved to be inappropriate in some cases but could not be changed.  A protected property `viewDisplayPanelSizeFull` has been added, and the panel size is set only when this property is true.

To maintain existing use, this property is true by default

### Forms

Forms support added.  MasterSitemapNode extended to include a configuration, so that a View or Form class can be declared, and its behaviour modified by its configuration.
Views themselves do not yet support the use of configuration objects.

The Form class is supported by FormBuilders - currently the standard form builder provides a table (Grid) or detail view depending on whether the URL has an entity id parameter

Although the configuration supports sub-sections, the Form itself does not do so yet (see #712)

### KrailErrorHandler

- Handles uncaught exceptions directly (by logging and popup message box) instead of calling Navigator.error().  The latter did not work well when an error occurs in Sitemap code.
- binding moved from ShiroVaadinModule to ErrorModule
- introduces [SystemErrorNotification] and [SystemErrorNotificationGroup] to improve flexibility in defining how to report errors


### Message Box

- The [MessageBox addon](https://vaadin.com/directory/component/messagebox) was intended to be used as a normal addon, but ButtonOption is not serializable.  The only solution unfortunately was to import the source.  There were no tests with it.   [Open Issue](https://github.com/KrailOrg/krail/issues/722) to review

### NavigationBar

A new component used in `SimpleUI` - a minimal form of application men bar

### Navigator

- Has 2 additional methods to access sub-pages and the breadcrumb to the current page
- error() method has been removed - this does not play well when there is an error in the Sitemap (see also the notes on MessageBox and KrailErrorHandler)

### SitemapFinisher

Redundant checks removed.  Only the redirect loop check remains

### TranslatableComponents

Added to simplify the I18N management of dynamically created components.  It can also be a simpler alternative to using @Caption.  Used in `NavigationBar`
  
### UserStatusPanel

- Replaced by `UserStatusComponents` - but this is temporary (see #751).  Functionally this is the same as `UserStatusPanel`, but is not a component - improves flexibility of use.


## Ported to Kotlin

New classes are in Kotlin. Quite a few classes were ported, this is just a few of them:

- MasterSitemapNode, UserSitemapNode, NodeRecord, SitemapFinisher and NavigationCommand
- A couple of tests which mock UserSitemapNode (Mockito cannot mock final classes, Mockk can)
- All in package uk.q3c.krail.core.navigate.sitemap.comparator
- KrailView



## Tests

Tests brought back in from krail-spek - build now enables the combined running of Spek, JUnit and Spock tests 

