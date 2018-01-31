# Functional Testing

Krail provides some support for Functional Testing out of the box.

For a true functional test, exercised through the user interface, it is likely you would want to use one of the tools available for just that purpose.  Currently these would most likely be one of:

- Vaadin TestBench
- Selenide
- Selenium

Vaadin TestBench is obviously Vaadin aware, but has a licence cost.  Selenide is free and open source.  Both use Selenium underneath to remove some of the issues of testing in an AJAX environment.

## Component Ids
All the above tools provide various methods of detecting an element within a web page.  The most robust is to use a CSS Selector, which in Vaadin;s case is provided by a `Component.id`  

To assist testing , Krail automatically assigns a hierarchical id to selected components.  This is done by an implementation of `ComponentIdGenerator`

The neat thing about using this method is that a functional test can get the same ids statically (that is, from the Class definition)


### Selecting the Components for Id
The default implementation of `ComponentIdGenerator` generates component ids statically, from the `KrailView` or `UI` that contains the components.  
The start point uses its simple class name as an id, and the rest uses field names to construct a hierarchical ids, for example:

``
MyView-layout-label
``

The default implementation uses the following rules to assign ids:

- excludes `Component`s which implement `Layout`
- includes all other `Components`
- does not drill down into a component unless it is annotated with **@AssignComponentId** 
- cannot drilldown into an interface, as id generation takes place by reflection (that is, the class does not need to be instantiated for id generation, only for assignment)

except where modified by an **@AssignComponentId** annotation.

A **@AssignComponentId** annotation has two parameters, *assign* and *drilldown*,  defaulting to *true* and *false* respectively

- if *assign* is true, an id is assigned, even if it would otherwise have been excluded,
- if *assign* is false, an id is not assigned, even if it would otherwise have been included,
- if *drilldown* is true, the generator drills down (by reflection) into the annotated component to look for any other `Component` fields
- if *drilldown* is false, no drilldown occurs

Note that you can get logical, but possibly unexpected results, depending on how you annotate a component container 


```java
public class SpecialLayout extends VerticalLayout{
    private Label label= new Label("Wiggly");
}


public class MyView extends KrailView{

    @AnnotationComponentId(assign=a, drilldown=d)
    private SpecialLayout layout;

    @Override
    protected void doBuild(){
        layout= new SpecialLayout();
    } 
}
```

With the annotation settings of:

- assign true and drill down true, the layout will have an id of "MyView-layout" and the label will have an id of "MyView-layout-label"
- assign true and drill down false, the layout will have an id of "MyView-layout" and the label id will be unchanged, probably null
- assign false and drill down false, the layout and label id will be unchanged, probably null
- assign false and drill down true, the layout id will be unchanged, probably null, the label will have an id of "MyView-label"

Note that in the last case, if you use the default implementation of `ComponentIdGenerator` the alyout will not appear in the output graph


Either the Field or its Class can be annotated with **@AssignComponentId**.  If both are annotated, the field annotation takes precedence

### Affect on Performance
Using CSS selectors makes robust testing through the UI a lot easier, but does have the penalty of incurring additional network traffic for all the extra labels, which you might not want to incur in a production environment.

There is an outstanding [issue](https://github.com/davidsowerby/krail/issues/662) to make it possible to switch this feature off via configuration