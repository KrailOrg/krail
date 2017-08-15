#Introduction

The previous section provided an extensive description of Krail's I18N mechanism, and gave an example of using annotations to manage the captions of a Vaadin component.  This section addresses the use of I18N annotations with Vaadin Components in more detail.


#Preparation


##Set up a page

We will build a new page:

- in ```MyOtherPages``` add a new page entry
```
addEntry("i18n", I18NDemoView.class, LabelKey.I18N, PageAccessControl.PUBLIC);
```
- in package 'com.example.tutorial.pages', create a new class 'I18NDemoView' extended from ```ViewBase```
- implement the ```doBuild()``` method
- create the enum constant *LabelKey.I18N*

```
package com.example.tutorial.pages;

import com.google.inject.Inject;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NDemoView extends ViewBase {
    
    @Inject
    protected I18NDemoView(Translate translate) {
        super(translate);
    }

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
put(LabelKey.No, "Nein");
put(LabelKey.Yes, "Ja");


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
        put(Yes,"Press for Yes");
        put(No, "Press for No");
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
        put(Yes, "Drücken Sie für Ja");
        put(No, "Drücken Sie für Nein");
    }
}

```


#Add different component types

The mix of components we will use should cover all the situations you will encounter - many of the components are treated the same way for I18N, so we do not need to use every available component.

```
package com.example.tutorial.pages;

import com.vaadin.ui.*;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NDemoView extends ViewBase {

    private Grid grid;
    private Label label;
    private Table table;
    private TextField textField;

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
         textField = new TextField();
        label = new Label();
        table = new Table();
        grid = new Grid();
        VerticalLayout layout = new VerticalLayout(textField, label, table, grid);
        Panel panel = new Panel();
        panel.setContent(layout);
        setRootComponent(panel);
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
import com.google.inject.Inject;
import com.vaadin.ui.*;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.core.view.ViewBase;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

public class I18NDemoView extends ViewBase {
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Grid grid;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Label label;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private Table table;
    @Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
    private TextField textField;

    @Inject
    protected I18NDemoView(Translate translate) {
        super(translate);
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {
        textField = new TextField();
        label = new Label();
        table = new Table();
        grid = new Grid();
        VerticalLayout layout = new VerticalLayout(textField, label, table, grid);
        Panel panel = new Panel();
        panel.setContent(layout);
        setRootComponent(panel);
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

- add a 'setupTable' method to ```I18NDemoView```

```
private void setupTable() {
    table.addContainerProperty(LabelKey.First_Name, String.class, null);
    table.addContainerProperty(LabelKey.Last_Name, String.class, null);
    table.setHeight("100px");
    table.setWidth("200px");
}
```
##Grid

In a very similar way to Table, Grid may need column headings translated.  If a ```Grid``` propertyId is an ```I18NKey``` it will be translated - otherwise it is ignored by the Krail ```I18NProcessor```. 

- add a 'setupGrid()' method

```
private void setupGrid(){
    grid.addColumn(LabelKey.First_Name, String.class);
    grid.addColumn(LabelKey.Last_Name, Integer.class);
}
```
- call these setup methods from ```doBuild()```

```
@Override
protected void doBuild(ViewChangeBusMessage busMessage) {
    textField = new TextField();
    label = new Label();
    table = new Table();
    grid = new Grid();
    setupTable();
    setupGrid();
    VerticalLayout layout = new VerticalLayout(textField, label, table, grid);
    Panel panel = new Panel();
    panel.setContent(layout);
    setRootComponent(panel);
    
}
```
- Run the application and go to the I18N page
    - the Table and grid now have column headings
    - Change the locale with the Locale Selector, and all the captions, tooltips, column headings & label value will change language

#Drilldown and Override

There is another scenario that Krail's I18N processing supports. Assume you have a class which contains components with I18N annotations and you want to make it re-usable.  Let's see how that would work.

- in the 'com.example.tutorial.i18n' package, create a new class 'ButtonBar', with **@Caption** on the buttons
- annotate the class with **@I18N** - this tells the ```I18NProcessor``` to drill down into this class to look for more I18N annotations.  This annotation can be applied to a field or a class, but for a re-usable component it makes more sense to put it on the class.

```
package com.example.tutorial.i18n;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import uk.q3c.krail.core.i18n.I18N;

@Caption(caption = LabelKey.News, description = DescriptionKey.Interesting_Things)
@I18N
public class ButtonBar extends Panel {

    @Caption(caption = LabelKey.Yes, description = DescriptionKey.Yes)
    private Button yesButton;
    @Caption(caption = LabelKey.No, description = DescriptionKey.No)
    private Button noButton;

    public ButtonBar() {
        yesButton = new Button();
        noButton = new Button();
        HorizontalLayout layout = new HorizontalLayout(yesButton,noButton);
        this.setContent(layout);
    }
}
```

- add two instances of this class to our ```I18NDemoView.doBuild()```.  Note that the second still needs to be a field (and not a local variable) for the ```I18NProcessor``` to find the class annotations.
- include them in the layout
```
buttonBar1 = new ButtonBar();
buttonBar2 = new ButtonBar();

VerticalLayout layout= new VerticalLayout(buttonBar1, buttonBar2,textField, label, table, grid);

```
- on the buttonBar1 field, annotate with a different **@Caption**

```
@Caption(caption = LabelKey.CEO_News_Channel,description = DescriptionKey.Interesting_Things)
private ButtonBar buttonBar1;
```
- Run the application and the two button bars will be at the top of the page
    - button bar 1 displays the caption you set at field level (overriding the class annotations)
    - button bar 2 displays the caption set at class level
    
    
You could also override the drilldown specified by the ButtonBar class, simply by annotating the field with **@I18N**(drilldown=false) - although we cannot think why you might want to do that !

#Form

Vaadin replaced its original Form with a ```BeanFieldGroup```, which is essentially a form without the layout.  Krail replaces that with its own ```BeanFieldGroupBase```, which also provides integration with Krail's I18N.

To demonstrate this we need to create an entity.
 
- create a new package 'com.example.tutorial.form'
- in this new package create a class 'Person', and include some familiar javax validation annotations, **@Min** and **@Size** 

```
package com.example.tutorial.form;

import uk.q3c.krail.persist.KrailEntity;

import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class Person implements KrailEntity<Long,Integer> {

    @Min(0) @Max(150)
    private int age;
    @Size(min = 3)
    private String firstName;
    @Id
    private Long id;

    @Size(min=3)
    private String lastName;
    @Version
    private Integer version;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public String getLastName() {
        return lastName;
    }
}

```
- Modify *build.gradle* to include javax.persistence - we have not yet introduced persistence, but we need the API for the entity
- Depending on the IDE you are using, you may need to refresh Gradle

```groovy
dependencies {
    // remember to update the Vaadin version below if this version is changed
    compile(group: 'uk.q3c.krail', name: 'krail', version: '0.9.9')
    compile 'javax.persistence:persistence-api:1.0.2'
}
```

- in package 'com.example.tutorial.form', create 'PersonForm'

```
package com.example.tutorial.form;

import com.example.tutorial.i18n.Caption;
import com.example.tutorial.i18n.DescriptionKey;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import uk.q3c.krail.core.i18n.I18N;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.core.ui.form.BeanFieldGroupBase;
import uk.q3c.krail.core.validation.BeanValidator;

import static com.example.tutorial.i18n.LabelKey.*;


@I18N
public class PersonForm extends BeanFieldGroupBase<Person> {
    @Caption(caption = Submit, description = DescriptionKey.Submit)
    private final Button submitButton;
    private final Person person;
    @Caption(caption = First_Name, description = DescriptionKey.Enter_your_first_name)
    private TextField firstName;

    @Caption(caption = Last_Name, description = DescriptionKey.Enter_your_last_name)
    private TextField lastName;
    @Caption(caption = Age, description = DescriptionKey.Age_of_the_Person)
    private TextField age;
    @Caption(caption = Person_Form, description = DescriptionKey.Person_Details_Form)
    private Panel layout;


    @Inject
    public PersonForm(I18NProcessor i18NProcessor, Provider<BeanValidator> beanValidatorProvider, Option option) {
        super(i18NProcessor, beanValidatorProvider, option);
        firstName = new TextField();
        lastName = new TextField();
        age = new TextField();


        person = new Person();
        person.setAge(44);
        person.setFirstName("Mango");
        person.setLastName("Chutney");
        submitButton = new Button();
        submitButton.addClickListener(event -> {
            try {
                this.commit();
            } catch (CommitException e) {
                e.printStackTrace();
            }
        });
        layout = new Panel(new VerticalLayout(firstName, lastName, age, submitButton));
        layout.setStyleName(ValoTheme.PANEL_WELL);
        setBean(person);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {

    }

    public Panel getLayout() {
        return this.layout;
    }
}

```
##About the form

The class simply extends ```BeanFieldGroupBase``` , with the required entity type as a generic parameter - in this case, ```Person```.  Like its Vaadin counterpart, ```BeanFieldGroupBase``` does not concern itself with the presentation of data, or the layout of that presentation.  That is the part we must provide.

You will recognise the fields and captions from the earlier part of this Tutorial section - they are just Vaadin components with **@Caption** annotations.  However, it should be noted that the names of the components must match the field names of the entity to enable automatic transfer of data between the presentation layer and data model.

The constructor simply extends ```BeanFieldGroupBase``` and your IDE will probably auto-complete the necessary parameters.  Don't forget the **@Inject** annotation though.

Within the constructor we simply build the presentation components, and define the submit button to invoke the commit() method, which will transfer data from the presentation layer back to the model - in this case the person bean.

Finally, the getLayout() method just enables a consumer class to identify the base component to place within a View.

There is an [open ticket](https://github.com/davidsowerby/krail/issues/431) to provide more support for Forms.

- Now we need to use the form, by injecting it in to ```I18NDemoView```

```
@Inject
protected I18NDemoView(Translate translate, PersonForm personForm) {
    super(translate);
    this.personForm = personForm;
}
```
- and add it to the layout in ```doBuild()```:
```
VerticalLayout layout = new VerticalLayout(personForm.getLayout(), buttonBar1, buttonBar2, textField, label, table, grid);
```

- Run the application, and navigate to the I18N page
       - The form will display at the top of the page with the values we have set
       - change a value which breaks validation (for example, age = 443), and a validation message will appear
       - change language with the Locale selector, and the language of the captions etc will change, including the validation message.
        

<div class="admonition note">
<p class="first admonition-title">Note</p>
<p class="last">Apache BVal provides language bundles for Locales de, en, es and it.  If you require others you will currently need to provide your own translations</p>
</div>

There is a more information about the Apache Bval validation integration in the [Developer Guide](devguide-validation.md)

#Summary

In this section we have:

- used I18N **@Caption** and **@Value** annotations
- seen how to manage ```Table``` and ```Grid``` column names for I18N
- created a re-usable I18N enabled component
- seen how to override a class I18N annotation 
- created a form, with I18N integrated validation

#Download from GitHub
To get to this point straight from GitHub, [clone](https://github.com/davidsowerby/krail-tutorial) using branch **step08a**



