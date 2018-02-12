# Release Notes for krail 0.14.0.0

The main purpose of this release is to move to Vaadin 8.  There have been some other changes as well.

## Vaadin

All components at version 8

## Event bus
The bus is now provided from two sources.  In anticipation of moving to a Vert.x environment it is intended to use a single bus, with handlers responsible for filtering, rather than using different buses with varied scopes.

As a step towards that, the *eventbus-api* and *eventbus-mbassador* library now provide "global" bus instances (Singleton in a standard environment).

### @SubscribeTo
The behaviour of this annotation changes.  It no longer uses the scope of the target to determine which bus to subscribe to.  Instead, if there is a **@Listener**, but no **@SubscribeTo**, the target will be subscribed to the global MessageBus.

If there is a **@Listener**, but an empty **@SubscribeTo()**, the target will not be subscribed to anything - this can be used to remove subscription from an inherited class.

 
## Functional Test Support
VaadinTestBench has been replaced by [Selenide](http://selenide.org/) for Functional Testing.  This solution is not as complete as TestBench, but covers many use cases. 

Component ids are now generated automatically to support functional testing.  There is an [outstanding issue](https://github.com/davidsowerby/krail/issues/662) to control this via configuration.
A `FunctionalTestSupport` object provides a model of route to View / UI, and the components they contain.

To complement this, there is some early but useful work held currently in the test-app project which generates Page Objects for functional testing.  These, along with some framework code enable testing using Selenide, and could be extend easily for use with Vaadin TestBench

Functional Test Support This will become a separate library in the near future  

## Validation and Forms
The previous `BeanFieldGroup` solution has been replaced completely, using [EasyBinder](https://github.com/ljessendk/easybinder).  This has been integrated with Krail's I18N mechanism.
See the `EasyBinder` class for the types of binder available.  There is more work to be done to provide a proper 'Form'.

The EasyBinder addon overcomes some of the limitations of the native Vaadin 8 binder

## Tutorial

The Tutorial has not been updated - this will be done after the move to Vert.x succeeds or fails

