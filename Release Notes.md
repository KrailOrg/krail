## Release Notes for krail 0.14.0.0

The main purpose of this release is to move to Vaadin 8.  There have been some other changes as well.

### Vaadin

All components at version 8

### Event bus
The bus is now provided from two sources.  In anticipation of moving to a Vert.x environment it is intended to use a single bus, with handlers responsible for filtering, rather than using different buses with varied scopes.

As a step towards that, the *eventbus-api* and *eventbus-mbassador* library now provide "global" bus instances (Singleton in a standard environment).

#### @SubscribeTo
The behaviour of this annotation changes.  It no longer uses the scope of the target to determine which bus to subscribe to.  Instead, if there is a **@Listener**, but no **@SubscribeTo**, the target will be subscribed to the global MessageBus.

If there is a **@Listener**, but an empty **@SubscribeTo()**, the target will not be subscribed to anything - this can be used to remove subscription from an inherited class.

### BeanFieldGroup and BeanValidator

`BeanFieldGroup` and `BeanValidator`, with their associated classes have been removed completely.  This attempt at support for Forms has been dropped from this release, so that dependencies on Vaadin 7 can be completely removed. Forms support will be redeveloped and re-instated as a priority - possibly using Vaadin addons [EasyBinder](https://vaadin.com/directory/component/easybinder) and/or [Viritin](https://vaadin.com/directory/component/viritin), though that has yet to be decided.
 