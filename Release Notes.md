# Release notes for 0.16.12.0

Forms support added

## Ported to Kotlin

MasterSitemapNode, UserSitemapNode, NodeRecord and NavigationCommand
A couple of tests which mock UserSitemapNode moved to krail-spek (Mockito cannot mock final classes, Mockk can)

## Refactoring

- DataModule becomes ConverterModule and is moved to uk.q3c.krail.core.form
- ConverterFactory and associated classes moved to uk.q3c.krail.core.form

