==============
Injector Scope
==============

:Date:   2018-05-09

The scope of the Guice Injector can be significant. A **@Singleton** is
described by Guice as "per application", and is therefore per Injector.

In most situations the injector scope is per JVM ClassLoader - the same
as a static variable.

Of course, different environments treat ClassLoaders in different ways.

The important part for Krail is the "per application" definition for
Guice.

Accessing the Injector
======================

It is normally considered bad practice to access the injector directly -
the whole point of IoC is to hand over control. But as always there are
special cases.

In Krail there are two scenarios where it is considered reasonable to
access the injector directly:

Deserialisation
---------------

``SerializationSupport`` is used to `re-inject Guice
supplied <userguide/serialisation>`__, **transient** dependencies
following deserialisation. Deserialisation occurs without any reference
to Guice of course, so without this intervention, transient dependencies
would be **null**.

View and UI Factory
-------------------

The ``MasterSitemap`` is a central component of Krail, and it uses
KrailView classes as part of the site definition. At the moment, the
most practical way to deal with this is to instantiate these views with
the injector, when they are needed.

When support for multiple views and UIs per route is implemented, dynamic
construction based on potentially any selection criteria will be
required. There may be a better way, but currently it is looking like
this will continue to need access to the injector as well.
( See issues `664 <https://github.com/davidsowerby/krail/issues/664>`__ and `665 <https://github.com/davidsowerby/krail/issues/665>`__)
