# Release notes for 0.16.12.0

## ScopedUI

### Display panel size

Previously the view display panel was always set to size full.  This proved to be inappropriate in some cases but could not be changed.  A protected property `viewDisplayPanelSizeFull` has been added, and the panel size is set only when this property is true.

To maintain existing use, this property is true by default

## Forms

Forms support added.  MasterSitemapNode extended to include a configuration, so that a View or Form class can be declared, and its behaviour modified by its configuration

The Form class is supported by FormBuilders - currently the standard form builder provides a table (Grid) or detail view depending on whether the URL has an entity id parameter

## Changes to KrailView

A ``NavigationStateExt`` is passed in the ``KrailView.beforeBuild`` and stored in ``ViewBase``.  this makes passing the ``ViewChangedMessage`` redundant in ``buildView()``, ``doBuild()`` and ``afterBuild()``, as the same information is available within the view from ``NavigationStateExt``

The methods with a parameter have therefore been deprecated and replaced with parameterless versions. 

## Navigator

- Has 2 additional methods to access sub-pages and the breadcrumb to the current page
- error() method has been removed - this does not play well when there is an error in the Sitemap (see also the MessageBox and KrailErrorHandler)

## KrailErrorHandler

- Handles uncaught exceptions directly (by logging and popup message box) instead of calling Navigator.error().  The latter does not work well when an error occurs in Sitemap code.
- binding moved from ShiroVaadinModule to ErrorModule

## Message Box

- The [MessageBox addon](https://vaadin.com/directory/component/messagebox) was intended to be used as a normal addon, but ButtonOption is not serializable.  The only solution unfortunately was to import the source.  There were no tests with it.   [Open Issue](https://github.com/KrailOrg/krail/issues/722) to review  
 

## Ported to Kotlin

Includes, but is not limited to:

- MasterSitemapNode, UserSitemapNode, NodeRecord and NavigationCommand
- A couple of tests which mock UserSitemapNode (Mockito cannot mock final classes, Mockk can)
- All in package uk.q3c.krail.core.navigate.sitemap.comparator

## Refactoring / renaming

- DataModule becomes ConverterModule and is moved to uk.q3c.krail.core.form
- ConverterFactory and associated classes moved to uk.q3c.krail.core.form
- UserSitemapSorter becomes UserSitemapNodeComparator
- SubPagePanel and Breadcrumb replaced by PageNavigationPanel

## Tests

Tests brought back in from krail-spek - build now enables the combined running of Spek, JUnit and Spock tests 

