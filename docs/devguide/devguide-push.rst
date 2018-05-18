====
Push
====

Background
==========

The Vaadin-provided 'push to browser' mechanism uses `Atmosphere <https://github.com/Atmosphere/atmosphere>`_, and this proved to be challenging for the author of the `vertx-vaadin <https://github.com/mcollovati/vertx-vaadin>`_ library, which Krail uses to run on Vertx.

Vertx also provides a 'push to browser' facility, but one which is an integral part of the `Vertx Event Bus <https://vertx.io/docs/vertx-core/js/#event_bus>`_, with much greater functionality.  In the words of the Vertx documentation::

   The event bus forms a distributed peer-to-peer messaging system spanning multiple server nodes and multiple browsers.

Krail, Push and Vertx
=====================
For good reasons, therefore, `vertx-vaadin <https://github.com/mcollovati/vertx-vaadin>`_ uses the Vertx push mechanism.  In order to accommodate that, some changes are needed for Krail.

The push connection is managed by the Vaadin UI (ScopedUI in Krail), with an embedded helper implementation of ``PushConfiguration``.  The simple task of using a different connection (``SockJSConnection`` for Vertx,  ``AtmospherePushConnection`` for Servlet environments), is made complicated by the closed nature of the Vaadin code structure.

There are two places which need the correct connection to be set, as described in the `related issue <https://github.com/mcollovati/vertx-vaadin/issues/14>`_, namely:

- ``ScopedUI`` constructor or ``init`` method
- the ``PushConfiguration.setPushMode()`` method

The first is perfectly simple.  The second, however, causes problems.

- the ``PushConfigurationImpl.setPushMode()`` method constructs and sets the connection using *new AtmospherePushConnection()* - this would mean that disabling and then re-enabling would switch back to the Atmosphere connector.
- The default implementation of ``PushConfiguration``, ``PushConfigurationImpl`` is constructed in the declaration of the ``pushConfiguration`` field of UI
- the ``pushConfiguration`` field of UI is private and has no setter


Adaptations
===========
Various methods of getting round these restrictions were considered, and all have their pros and cons.  The simplest, if rather nasty hack, of replacing the default ``PushConfiguration`` by reflection was reluctantly considered the better option rather than duplicating a lot of the native Vaadin code.  This is done by calling ``overridePushConfiguration()`` in the ``ScopedUI`` constructor.

This change is complemented by a new ``PushConfiguration`` implementation , ``KrailPushConfiguration``, which is a direct lift of Vaadin code with the ``setPushMode()`` method changed to allow the construction of the correct push connection.

Detecting the Environment
=========================
``KrailPushConfiguration`` needs to know which environment (Servlet or Vertx) it is running in.  The :doc:`Bootstrap <../devguide/devguide-bootstrap#detecting-the-environment>` process provides detection of the runtime environment, which is accessed by Guice injection.
