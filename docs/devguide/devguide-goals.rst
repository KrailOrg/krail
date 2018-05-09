=====
Goals
=====

Terminology
===========

-  **Krail developer** - someone developing an application based on
   Krail

-  **Krail team** - the team which writes Krail itself

Goals
=====

1. To produce a framework which enables a Krail developer to produce a
   reliable and complete business application quickly which:

   a. is easy to change and adapt to changing requirements

   b. enables the Krail developer to concentrate on business
      requirements rather than technical requirements

   c. re-uses existing, well proven code wherever possible

   d. supports a microservices architecture

   e. supports a traditional servlet based architecture

Objectives
==========

1. provide a microservices architecture using Eclipse Vert.x

2. enable the use of the same code in a servlet environment with minimal
   configuration changes

3. allow the Krail developer to develop in just Java or Kotlin as much
   as possible, minimising the need for CSS, HTML, XML etc

Priorities
==========

1. A Vert.x solution is a higher priority than the Servlet solution. If
   compromises are absolutely necessary, then it is the Servlet solution
   which should be adjusted.

Krail Team Goals
================

These goals relate only to Krail itself, not the application developed
on Krail. They are still goals, but considered less important than the
"business" goals described above.

1. Everything in a single language - the current mix of Java, Kotlin and
   Groovy should migrate eventually to Kotlin only. This applies to
   source, test code and build scripts.

2. A common test framework. There is currently a mix of Junit Java,
   JUnit Kotlin and Spock. Ideally this will all migrate to Spek
   (Kotlin) - but may have to include JUnit Kotlin to enable Vertx
   testing.

3. Kotlin based build. This could be either Gradle with Kotlin script
   (in place of Groovy scripts) or
   `Kobalt <https://github.com/cbeust/kobalt>`__. Kobalt needs to be
   assessed before making a switch

4. A single development lifecycle, but with optional steps. This is
   currently provided by the `KayTee
   plugin <https://github.com/davidsowerby/kaytee-plugin>`__, but that
   is not at production standard. It is, however, a companion product to
   Krail

Priorities
----------

1. Migrating to an all Kotlin solution is not urgent. It can be carried
   out when the opportunity arises.

2. New code can be accepted in Java if it offers new or improved
   functionality.

3. New tests should ideally be in Spek but note the limitation below.
   JUnit tests in Kotlin or Java can also be accepted if really
   necessary.

Spek Limitation
---------------

The current version of Spek (1.1.5) does not play well with JUnit.
Specifically this causes the Spek tests not to execute, when JUnit is
used in the same test run. This can lead to false positives, and the
only solution is to hold Spek tests separately.

It is hoped that version 2.0 of Spek will resolve this

Documentation
=============

1. Documentation should be kept with and maintained at the same time as
   its associated code. This is not currently achieved because all the
   documentation is in the main Krail repository. Since the move to
   GitBook, it may be achievable using an
   `include <https://www.npmjs.com/package/gitbook-plugin-include>`__

2. Javadoc (or Kotlin equivalent) does not need to state the obvious.
   But, if you think you would benefit from some notes when you come
   back to the code in 5 years' time - then write those notes.
