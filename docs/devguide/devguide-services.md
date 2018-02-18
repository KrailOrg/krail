# Introduction


# Managing the Lifecycle

## <a name="state-changes-and-causes"></a>State Changes and Causes

The following table summarises the changes of state and cause, depending on the old state and the call made.  If any call is made that does not appear in these tables, or listed under **Ignored Calls**, an exception is thrown.
 
### No errors during call
 
This table assumes no errors occur during the call
 
| old state | full call                |short call       |new state & cause           |
|-----------|--------------------------|-----------------|----------------------------|
| INITIAL   | start(STARTED)           |start()          | RUNNING, STARTED           |
| RUNNING   | stop(STOPPED)            |stop()           | STOPPED, STOPPED           |
| RUNNING   | stop(FAILED)             |fail()           | FAILED,  FAILED            |
| RUNNING   | stop(DEPENDENCY_STOPPED) |dependencyStop() | STOPPED, DEPENDENCY_STOPPED|
| RUNNING   | stop(DEPENDENCY_FAILED)  |dependencyFail() | STOPPED, DEPENDENCY_FAILED |
| STOPPED   | start(STARTED)           |start()          | RUNNING, STARTED           |
| FAILED    |                          |reset()          | INITIAL, RESET             |

### Error occurs during call

This table assumes that an error occurs during the call

| old state | full call                 |short call       |new state & cause           | 
|-----------|---------------------------|-----------------|----------------------------|
| INITIAL   | start(STARTED)            |start()          | FAILED,  FAILED_TO_START*   |
| STOPPED   | start(STARTED)            |start()          | FAILED,  FAILED_TO_START*   |
| INITIAL   | start(STARTED)            |start()          | INITIAL,  DEPENDENCY_FAILED** |
| STOPPED   | start(STARTED)            |start()          | STOPPED,  DEPENDENCY_FAILED** |
| RUNNING   | stop(STOPPED)             |stop()           | FAILED,  FAILED_TO_STOP    |
| RUNNING   | stop(FAILED)              |fail()           | FAILED,  FAILED            |
| RUNNING   | stop(DEPENDENCY_STOPPED)  |dependencyStop() | FAILED,  FAILED_TO_STOP    |
| RUNNING   | stop(DEPENDENCY_FAILED)   |dependencyFail() | FAILED,  FAILED_TO_STOP    |
| FAILED    |                           |reset()          | FAILED,  FAILED_TO_RESET   |


 \* Error in the Service that was called<br>
** Error in a dependency of the Service that was called

### Ignored Calls

A call to start() when the state is STARTING or RUNNING is ignored
A call to stop(), fail(), dependencyStop(), dependencyFail() when the state is INITIAL, RESETTING, STOPPED or FAILED is ignored
A call to reset() when the state is INITIAL or RESETTING is ignored
 
 
# Service Instantiation

When a Service is instantiated through Guice, AOP in the ServicesModule calls ServicesModel.addService, which also creates all its dependencies from the 'template' provided by the ServicesModel.classGraph
 
This means that the instanceGraph should always have a complete set of dependencies for any Service instantiated through Guice. 