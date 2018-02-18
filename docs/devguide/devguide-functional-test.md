# Functional Testing

For a functional test, exercised through the user interface, it is likely you would want to use one of the tools designed for just that purpose.  Currently these would most likely be one of:

* Vaadin TestBench
* Selenide
* Selenium

Vaadin TestBench is obviously Vaadin aware, but has a licence cost.  Selenide is free and open source.  Both use Selenium underneath to remove some of the issues of testing in an AJAX environment.

## Component Ids

All the above tools provide various methods of detecting an element within a web page.  The most robust is to use a CSS Selector, which in Vaadin's case is provided by a `Component.id`

To assist testing, Krail automatically assigns a hierarchical id to selected components.  This is done by an implementation of `ComponentIdGenerator`

This id is in the form of _MyView-component-nestedcomponent-nestedcomponent_ to whatever depth is defined by your views and components.  By default, anything which implements \[Layout\] is ignored, as these do not usually declare any components, and are not usually required for functional testing.

You can, however, use an \[AssignComponentId\] annotation to change this

### Affect on Performance

Using CSS selectors makes robust testing through the UI a lot easier, but does have the penalty of incurring additional network traffic for all the extra labels, which you might not want in a production environment.

There is an outstanding [issue](https://github.com/davidsowerby/krail/issues/662) to make it possible to switch this feature off via configuration.

## Functional Test Support

VaadinTestBench has been replaced by [Selenide](http://selenide.org/) for Functional Testing.  This solution is not as complete as TestBench, but covers many use cases.

Component ids are now generated automatically to support functional testing. 

A `FunctionalTestSupport` object provides a model of route to View / UI, and the components they contain.

To complement this, there is some early but useful work held currently in the test-app project which generates Page Objects for functional testing.  These, along with some framework code enable testing using Selenide, and could be extended easily for use with Vaadin TestBench - the objective it to enable the use of different test tools without changing the tests

See `KotlinPageObjectGenerator` and the other classes in uk.q3c.krail.functest, in the [test-app](https://github.com/davidsowerby/krail-testApp) project

Functional Test Support This will become a separate library in the near future.

