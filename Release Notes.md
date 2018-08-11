# Release notes for 0.16.12.0

## Forms

Forms support added.  MasterSitemapNode extended to include a configuration, so that a View or Form class can be declared, and its behaviour modified by its configuration

The Form class is supported by FormBuilders - currently the standard form builder provides a table (Grid) or detail view depending on whether the URL has an entity id parameter

## Changes to KrailView

A ``NavigationStateExt`` is passed in the ``KrailView.beforeBuild`` and stored in ``ViewBase``.  this makes passing the ``ViewChangedMessage`` redundant in ``buildView()``, ``doBuild()`` and ``afterBuild()``, as the same information is available within the view from ``NavigationStateExt``

The methods with a parameter have therefore been deprecated and replaced with parameterless versions. 

## Navigator

- Has 2 additional methods to access sub-pages and the breadcrumb to the current page

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

