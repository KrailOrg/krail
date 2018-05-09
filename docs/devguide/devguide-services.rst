========
Services
========

Managing the Lifecycle
======================

<a name="state-changes-and-causes"></a>State Changes and Causes
---------------------------------------------------------------

The following table summarises the changes of state and cause, depending
on the old state and the call made. If any call is made that does not
appear in these tables, or listed under **Ignored Calls**, an exception
is thrown.

No errors during call
~~~~~~~~~~~~~~~~~~~~~

This table assumes no errors occur during the call

+--------------------+--------------------+--------------------+--------------------+
| old state          | full call          | short call         | new state & cause  |
+====================+====================+====================+====================+
| INITIAL            | start(STARTED)     | start()            | RUNNING, STARTED   |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(STOPPED)      | stop()             | STOPPED, STOPPED   |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(FAILED)       | fail()             | FAILED, FAILED     |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(DEPENDENCY\_S | dependencyStop()   | STOPPED,           |
|                    | TOPPED)            |                    | DEPENDENCY\_STOPPE |
|                    |                    |                    | D                  |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(DEPENDENCY\_F | dependencyFail()   | STOPPED,           |
|                    | AILED)             |                    | DEPENDENCY\_FAILED |
+--------------------+--------------------+--------------------+--------------------+
| STOPPED            | start(STARTED)     | start()            | RUNNING, STARTED   |
+--------------------+--------------------+--------------------+--------------------+
| FAILED             |                    | reset()            | INITIAL, RESET     |
+--------------------+--------------------+--------------------+--------------------+

Error occurs during call
~~~~~~~~~~~~~~~~~~~~~~~~

This table assumes that an error occurs during the call

+--------------------+--------------------+--------------------+--------------------+
| old state          | full call          | short call         | new state & cause  |
+====================+====================+====================+====================+
| INITIAL            | start(STARTED)     | start()            | FAILED,            |
|                    |                    |                    | FAILED\_TO\_START\ |
|                    |                    |                    | *                  |
+--------------------+--------------------+--------------------+--------------------+
| STOPPED            | start(STARTED)     | start()            | FAILED,            |
|                    |                    |                    | FAILED\_TO\_START\ |
|                    |                    |                    | *                  |
+--------------------+--------------------+--------------------+--------------------+
| INITIAL            | start(STARTED)     | start()            | INITIAL,           |
|                    |                    |                    | DEPENDENCY\_FAILED |
|                    |                    |                    | \*\*               |
+--------------------+--------------------+--------------------+--------------------+
| STOPPED            | start(STARTED)     | start()            | STOPPED,           |
|                    |                    |                    | DEPENDENCY\_FAILED |
|                    |                    |                    | \*\*               |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(STOPPED)      | stop()             | FAILED,            |
|                    |                    |                    | FAILED\_TO\_STOP   |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(FAILED)       | fail()             | FAILED, FAILED     |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(DEPENDENCY\_S | dependencyStop()   | FAILED,            |
|                    | TOPPED)            |                    | FAILED\_TO\_STOP   |
+--------------------+--------------------+--------------------+--------------------+
| RUNNING            | stop(DEPENDENCY\_F | dependencyFail()   | FAILED,            |
|                    | AILED)             |                    | FAILED\_TO\_STOP   |
+--------------------+--------------------+--------------------+--------------------+
| FAILED             |                    | reset()            | FAILED,            |
|                    |                    |                    | FAILED\_TO\_RESET  |
+--------------------+--------------------+--------------------+--------------------+

-  Error in the Service that was called<br>

   -  Error in a dependency of the Service that was called

Ignored Calls
~~~~~~~~~~~~~

A call to start() when the state is STARTING or RUNNING is ignored A
call to stop(), fail(), dependencyStop(), dependencyFail() when the
state is INITIAL, RESETTING, STOPPED or FAILED is ignored A call to
reset() when the state is INITIAL or RESETTING is ignored

Service Instantiation
=====================

When a Service is instantiated through Guice, AOP in the ServicesModule
calls ServicesModel.addService, which also creates all its dependencies
from the 'template' provided by the ServicesModel.classGraph

This means that the instanceGraph should always have a complete set of
dependencies for any Service instantiated through Guice.
