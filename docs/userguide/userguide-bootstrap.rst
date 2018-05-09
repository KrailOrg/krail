=========
Bootstrap
=========

:Date:   2018-05-09

In order to allow the same application code to run in both Vertx and
Servlet environments, a bootstrap sequence is used. This is only really
needed so that the Guice Injector can be held in a location suitable for
the environment, so that it can be retrieved during
`deserialisation </userguide/userguide-serialisation.html>`__ As a side
effect, it has the additional benefit of simplifying basic application
configuration.

Bootstrap File
==============

A file called 'krail-bootstrap.yml' should be placed in
*src/main/resources*.

Sample File
-----------

A minimal example file.

.. code:: yaml

    version: 1
    collator: uk.q3c.krail.core.guice.CoreBindingsCollator
    modules:
      - com.example.myapp.MyAppModule1
    servlet:
     modules:
      - uk.q3c.krail.core.guice.ServletEnvironmentModule
    vertx:
      modules:
       - uk.q3c.krail.core.guice.VertxEnvironmentModule

File Content
------------

version
~~~~~~~

Optional, defaults to 1.

collator
~~~~~~~~

Required. A fully qualified reference to your implementation of
``BindingsCollator``, or you could even leave it as
*uk.q3c.krail.core.guice.CoreBindingsCollator* and add your own modules
as below.

modules
~~~~~~~

Optional. Fully qualified references to modules you want to add to the
collator. These will apply to both Servlet and Vertx environments.

servlet
~~~~~~~

Required if you intend to run the application in a Servlet environment.

servlet/modules
^^^^^^^^^^^^^^^

Required if you intend to run the application in a Servlet environment.
Modules to be added to the collator only for the Servlet environment.
Unless its bindings are replaced elsewhere you will need at least
*uk.q3c.krail.core.guice.ServletEnvironmentModule* as shown in the
example.

vertx
~~~~~

Required if you intend to run the application in a Vertx environment.

vertx/modules
^^^^^^^^^^^^^

Required if you intend to run the application in a Vertx environment.
Modules to be added to the collator only for the Vertx environment.
Unless its bindings are replaced elsewhere you will need at least
*uk.q3c.krail.core.guice.VertxEnvironmentModule* as shown in the
example.
