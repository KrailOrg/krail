### Release Notes for krail 0.8.0

This version integrates JSR303 validation with Krail's I18N framework.  I18N has been simplified by the use of annotated annotations.  Navigation responses to login / logout have been made more flexible with navigation rules.

#### Change log

-   [35](https://github.com/davidsowerby/krail/issues/35): Validation
-   [286](https://github.com/davidsowerby/krail/issues/286): File load behaves differently on Windows
-   [313](https://github.com/davidsowerby/krail/issues/313): Move ExampleUtil
-   [316](https://github.com/davidsowerby/krail/issues/316): Review I18NModule javadoc
-   [318](https://github.com/davidsowerby/krail/issues/318): Navigator and DefaultNavigator shoul not handle userStatusChanged
-   [321](https://github.com/davidsowerby/krail/issues/321): Annotated annotations for I18N
-   [322](https://github.com/davidsowerby/krail/issues/322): Login fails if the first page
-   [323](https://github.com/davidsowerby/krail/issues/323): Test failure ClassBundleReaderTest
-   [326](https://github.com/davidsowerby/krail/issues/326): Test Failures when run through IDE on Ubuntu_de


#### Dependency changes

   test compile dependency version changed to: krail-testUtil:1.0.7
   test compile dependency version changed to: q3c-testUtil:0.7.4

#### Detail

*Fix [326](https://github.com/davidsowerby/krail/issues/326) Corrected relative file path in tests*

Using TestResource to obtain the correct file path.  This has also made q3c-testUtil TestUtil redundant.  See [q3c-testUtil 2](https://github.com/davidsowerby/q3c-testUtil/issues/2)


---
*Fix [323](https://github.com/davidsowerby/krail/issues/323) Resolve failure on non-English platform*

Class bundle now TestLbls_en added to enable tests to run when CurrentLocale is not Locale.UK as it usually is.  Noted that errors also occur if there is a properties file and a class of the same (fully qualified name).  This has been raised as [325](https://github.com/davidsowerby/krail/issues/325) but may not be worth fixing.  A note has been made in the documentation.

Some logging has been added, and some Javadoc corrctions also made
DefaultFileSitemapLoaderTest modified - it had been using a lable key which now clashes with renamed test resources


---
*Javadoc correction*


---
*Update version information*


---
*fix [322](https://github.com/davidsowerby/krail/issues/322) Rectified failure after login*

The UserSitemap correctly does not contain the login node in its map after login, but the NavigationRule was attempting to use it.  Provided some utility methods in UserSitemap to assist, isLoginUri(), isLogoutUri etc, and also modified the DefaultNavigator.currentNode() method to check specifically for the login node.

Also added some debug statements
Added equals() methods to Master and UserSitemapNode
Made DefaultMasterSitemap threadsafe (see #244)


---
*Fix [321](https://github.com/davidsowerby/krail/issues/321) Simpler I18N - annotated Annotations*

A much improved implementation removes the need for a quite few I18N support classes (I18NReader and the various Flex classes), and also makes it easier for a Krail developer to provide their own annotations.  @I18N & @I18NValue have been replaced by @Caption, @Description and @Value.  I18NModule can configure package prefixes to exclude from I18N drill down (for example 'com.vaadin'). Documentation has been updated.


---
*Fix [316](https://github.com/davidsowerby/krail/issues/316) Clarify terminology for I18N, no functional changes*

The javadoc, method naming and annotation naming were a bit confused between source, bundle and reader.  No functional changes have been made, but some lements have been re-named, and the javadoc revised.


---
*Fix [313](https://github.com/davidsowerby/krail/issues/313) Single Subject instance for duration of VaadinSession*

Previously new Subject instance created every time provider.get() was called, until authenticated, and only then was it stored in the VaadinSession.  Now stored in VaadinSession at first use, and therefore same instance always returned within a session.  SubjectProvider is no longer a Singleton.


---
*Fix [318](https://github.com/davidsowerby/krail/issues/318). Introduce navigation rules to make Navigator response to login & logout more flexible*

Create a Guice NavigationModule.  Move Navigator & URIHandler bindings from UIModule to NavigationModule.  Change UserStatusListener API to provide change of status with login / logout already identified.
The source of login / logout is passed to the rules to enable decisions based on what the source is.  The logic of the login rule is slightly different to the original code, it no longer expects current page to be the login page, and thus supports an embedded login component.


---
*Fix [35](https://github.com/davidsowerby/krail/issues/35) JSR303 (Apache Bval) integrated with Krail I18N*

Integration allows the use of existing javax and bval annotations without change, but also enables the use of Krail I18NKeys either in custom validators or by substitution;  these are implemented through KrailInterpolator.  BeanFieldGroup redeveloped to enable use with KrailInterpolator.  Documentation also completed at https://sites.google.com/site/q3cjava/validation#TOC-Move-all-translations-to-one-source


---
*Fix [286](https://github.com/davidsowerby/krail/issues/286) Corrected tests which fail on Windows only*

There were two different problems.  LoadReportBuilderTest.buildReport() was affected by differences in line ending characters. That was solved by using a line by line comparison (FileTestUtil.compare)

DefaultFileSitemapLoader2.multipleInputFiles was using a String constant to compare an expected file path - so suffered from differences in path separators in Windows. Solved by using a File object to retrieve the path.


---
*See [286](https://github.com/davidsowerby/krail/issues/286) .gitattributes added*

This should fix the problem identified by [286](https://github.com/davidsowerby/krail/issues/286) but cannot check without committing.


---
