==============
Guice & Scopes
==============

For this short section we will not be actually adding to the Tutorial
application - it is time for just a little bit of theory.


Introduction
============

As a developer you will be familiar with the idea of scope, even if you
have never used Guice before - Guice scopes are no different in
principle than considering the scope, for example, of a local vs global
variable. The `Guice
documentation <https://github.com/google/guice/wiki>`__ is an excellent
place to start for understanding Guice itself, and the `section on
scopes <https://github.com/google/guice/wiki/Scopes>`__ is particularly
relevant.

In this section we will consider the way in which scopes are implemented
by Krail.

Singleton
=========

A Singleton has only one instance in the application. Krail uses the
standard Guice Singleton with no changes. All Singletons must be thread
safe.

VaadinSessionScoped
===================

The unique environment\* of a ``VaadinSession`` requires a custom Guice
scope of **@VaadinSessionScoped** - and is generally equivalent to a
browser instance. Classes of this scope should be thread safe, as a
Vaadin Session may use multiple threads.

UIScoped
========

The `Vaadin UI <https://vaadin.com/api/com/vaadin/ui/UI.html>`__ is
generally equivalent to a browser tab, and requires a custom Guice scope
of **@UIScoped**. Classes of this scope do not need to be thread safe.

Applying a scope
================

All of the above scopes may be applied as described
`here <https://github.com/google/guice/wiki/Scopes#applying-scopes>`__\ <br>
<br> <br>

<br>
====

**Note that the standard web annotation of \*@SessionScoped** appears to
work except when using with Vaadin Push - but has not been tested
thoroughly.
