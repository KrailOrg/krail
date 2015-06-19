#Introduction

The previous section provided an extensive description of Krail's I18N mechanism, and gave an example of using annotations to manage the captions of a Vaadin component.  This section addresses the use of I18N annotations with Vaadin Components in more detail.


#Preparation


##Set up a page

We will build a new page:

- in ```MyOtherPages``` add a new page entry
```
addEntry("i18n", I18NView.class, LabelKey.I18N, PageAccessControl.PUBLIC);
```
- in package 'com.example.tutorial.pages', create a new class 'I18NView' extended from ```ViewBase```
- implement the ```doBuild()``` method
- create the enum constant *LabelKey.I18N*

```
package com.example.tutorial.pages;

import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NView extends ViewBase {
    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
    }
}
```

##Translations

- add the following translations to ```Labels_de```, creating keys where necessary

```
put(News, "Nachrichten");
put(Last_Name, "Nachname");
put(First_Name, "Vorname");

```
- in the 'com.example.tutorial.i18n' package, create the 'Descriptions' class

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.EnumResourceBundle;

import static com.example.tutorial.i18n.DescriptionKey.*;

public class Descriptions extends EnumResourceBundle<DescriptionKey> {
    @Override
    protected void loadMap() {
        put(Interesting_Things, "Interesting things that have happened in the world.");
    }
}
```
- also create the Descriptions_de class
```
package com.example.tutorial.i18n;

import static com.example.tutorial.i18n.DescriptionKey.*;

public class Descriptions_de extends Descriptions {
    @Override
    protected void loadMap() {
        put(Interesting_Things, "Interessante Dinge, die in der Welt haben geschehen");
        put(You_just_asked_for_a_pay_increase, "Sie haben für eine Lohnerhöhung gebeten");
    }
}

```


#Add different component types

The mix of components we will use should cover all the situations you will encounter - many of the components are treated the same way for I18N, so we do not need to use every available component.

```
package com.example.tutorial.pages;

import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.example.tutorial.i18n.LabelKey;
import com.vaadin.ui.*;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NView extends ViewBase {

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        textField = new TextField();
        label = new Label();
        table = new Table();
        grid = new Grid();
        setRootComponent(new VerticalLayout(textField, label, table, grid));
    }
}
```
<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">When you sub-class from ViewBase, make sure you set the root component in your doBuild() method</p>
</div>

- Add the same **@Caption** to each field:
```
@Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
```
- The result should be
```
package com.example.tutorial.pages;

import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.example.tutorial.i18n.LabelKey;
import com.vaadin.ui.*;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NView extends ViewBase {
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Grid grid;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Label label;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Table table;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private TextField textField;

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        textField = new TextField();
        label = new Label();
        table = new Table();
        grid = new Grid();
        setRootComponent(new VerticalLayout(textField, label, table, grid));
    }
}
```


- Run the application and go to the 'I18N' page
- All 4 components will be present, each with a caption of 'News' and a tooltip of 'Interesting things that have happened in the world.'
- Changing Locale with the Locale Selector changes the language
- but only the ```TextField``` looks complete



##Labels

Often with ```Label``` components you want to set the value of the component statically, which you can also do with an annotation.  Actually you can do that using Krail's I18N mechanism for any component which implements the ```com.vaadin.data.Property``` interface and accepts a ```String``` value.

We have a choice to make now.  Remember that:

1. The name of an I18N annotation does not matter, it just needs to be annotated with ```@I18NAnnotation```
1. The ```I18NAnnotationProcessor``` can handle multiple annotations on the same component
1. The annotation methods can be any combination of ```caption()```, ```description()```, ```value()``` or ```locale()```
1. We need to specify which ```I18NKey``` we use (that is, the enum class - Java will not allow an interface as a type)
 
We could:

1. Add the value() method to **@Caption**
1. We could create a **@Value** annotation with only the ```value()``` method
1. We could create a caption specifically for Labels


... and quite few more choices, too.  Remember, though, that you cannot specify a default value of **null** in an annotation, so if you want to have an annotation method that is often not used, the best way is to specify a "null key", which should probably return an empty ```String``` from ```Translate``` 
  

```java
DescriptionKey value() default DescriptionKey.NULLKEY;
```

For the Tutorial, we will create a **@Value** annotation, which has only a ```value()``` method.

- in the 'com.example.tutorial.i18n' package create a new annotation 'Value'
- we will use ```DescriptionKey``` for values, as they can be quite long

```
package com.example.tutorial.i18n;

import uk.q3c.krail.i18n.I18NAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@I18NAnnotation
public @interface Value {
    
    DescriptionKey value();
}
```

- Add a @Value ```Annotation``` to the ```Label```
```
@Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
@Value(value = DescriptionKey.You_just_asked_for_a_pay_increase)
private Label label;
    
```
- Run the application and go to the 'I18N' page
    - The ```Label``` now has a value.  Actually, we could have done the same with the ```TextField```, but that isn't usually what you would want.
    - Change the locale with the Locale Selector, and all the captions, tooltips & label value will change language


##Table

A ```Table``` has column headers which may need translation. If a ```Table``` propertyId is an ```I18NKey``` it will be translated - otherwise it is ignored by the Krail ```I18NProcessor```. 

- add a 'setupTable' method to ```I18NView```

```
private void setupTable() {
    table.addContainerProperty(LabelKey.First_Name, String.class, null);
    table.addContainerProperty(LabelKey.Last_Name, String.class, null);
    table.setHeight("100px");
    table.setWidth("200px");
}
```
##Grid

In a very simlar way to Table, Grid may need column headings translated.  If a ```Grid``` propertyId is an ```I18NKey``` it will be translated - otherwise it is ignored by the Krail ```I18NProcessor```. 

- add a 'setupGrid()' method

```
private void setupGrid(){
    grid.addColumn(LabelKey.First_Name, String.class);
    grid.addColumn(LabelKey.Last_Name, Integer.class);
}
```
- call these methods from ```doBuild()```

```
@Override
protected void doBuild(ViewChangeBusMessage busMessage) {
    textField = new TextField();
    label = new Label();
    table = new Table();
    grid = new Grid();
    setupTable();
    setupGrid();
    setRootComponent(new VerticalLayout(textField, label, table, grid));
}
```
- Run the application and go to the I18N page
    - the Table and grid now have column headings
    - Change the locale with the Locale Selector, and all the captions, tooltips, column headings & label value will change language



