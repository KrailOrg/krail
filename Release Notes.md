# Release notes for 0.16.12.0

## Forms

Forms support added, currently just SimpleForm

## Changes to KrailView

A ``NavigationStateExt`` is passed in the ``KrailView.beforeBuild`` and stored in ``ViewBase``.  this makes passing the ``ViewChangedMessage`` redundant in ``buildView()``, ``doBuild()`` and ``afterBuild()``, as the same information is available within the view from ``NavigationStateExt``

The methods with a parameter have therefore been deprecated and replaced with parameterless versions. 


## Ported to Kotlin

MasterSitemapNode, UserSitemapNode, NodeRecord and NavigationCommand
A couple of tests which mock UserSitemapNode (Mockito cannot mock final classes, Mockk can)

## Refactoring

- DataModule becomes ConverterModule and is moved to uk.q3c.krail.core.form
- ConverterFactory and associated classes moved to uk.q3c.krail.core.form

## Tests

Tests brought back in from krail-spek - build now enables the combined running of Spek, JUnit and Spock tests 

