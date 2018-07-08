=====
Forms
=====

Overview
========

Vaadin provides some support for Forms with ``Binder``, but Krail takes that further.  It makes the definition of a Form part of the Sitemap by assigning a ``FormConfiguration`` to a ``SitemapNode``.

The Form class takes that configuration and builds the form with UI components (TextField etc) and integrates Krail's I18N and JSR 303 validation.

Two Form types are currently provided:

- **simple**, which displays/edits selected properties form a given entity class
- **list**, which displays a `table` of selected properties, for one more instance of the same entity class


Additional form types can easily be added.


Defining a Form
===============

To construct a form in Krail:

 1. Define your form configuration as a sub-class of ``FormConfiguration``, for example ``PersonFormConfiguration``
 2. using either the Direct or Annotation method of creating a ``Sitemap`` entry, set the *viewClass* to ``Form.class``
 3. set the *viewConfiguration* to ``PersonFormConfiguration.class``


Form construction
=================

As part of Krail's navigation process, the view for a given URI is looked up from the Sitemap.  The *viewClass* is constructed via Guice, and an instance of the *viewConfiguration* passed to it (in this example an instance of ``PersonFormConfiguration``

 1. ``Form`` invokes ``FormTypeSelector`` to acquire the correct ``FormBuilder``
 2. ``FormBuilder`` uses ``FormConfiguration`` in combination with ``FormSupport`` to construct appropriate UI components (TextFields etc) and bind them to enity data. The binding is carried out by ``KrailBeanValidationBinder``, which also takes care of integrating JSR303 validation and I18N.

Validation
----------

Validation can be defined by JSR303 annotations on the entity or directly within the ``FormConfiguration``



Model to Presentation mapping
=============================

``FormSupport`` provides the mappings of data types to presentation Fields, along with data converters.  These mappings are defined in Guice and can therefore be easily extended or overruled.


Defaults
--------

For each property (the Model) that is being bound to the user interface, a component (the Presentation) is needed. To enable the automatic creation of presentation elements, ``FormSupport.fieldFor()`` uses a map of data types to Vaadin Fields - for example, a String is mapped to a ``TextField``.

Default mappings are provided by the ``FormModule``, but these can be overridden for specific instances within ``FormConfiguration``.



Changing defaults
-----------------

To change the default Model to Presentation mappings, sub-class ``FormModule`` override ``bindDefaultDataClassMappings``, and replace ``FormModule`` with your sub-class in your ``BindingsCollator``


Register a new mapping
----------------------
A new data type can be registered, by creating another Guice module which contributes a ``MapBinder`` as below


.. sourcecode:: kotlin
   :caption: Kotlin

   class MyMappingModule : AbstractModule() {

     override fun configure() {
        val fieldLiteral = object : TypeLiteral<AbstractField<*>>() {}
        val dataClassLiteral = object : TypeLiteral<Class<*>>() {}
        val dataClassToFieldMap: MapBinder<Class<*>, AbstractField<*>> = MapBinder.newMapBinder(binder(), dataClassLiteral, fieldLiteral)

        // bind new data types
        dataClassToFieldMap.addBinding(MyDataClass::class.java).to(WidgetField::class.java)
    }


Model to Presentation Converters
================================

Where the model and presentation type are the same, clearly no conversion is needed, although a ``NoConversionConverter`` is actually used to transfer the data.

Where a converter is needed - for example, to display an integer in a TextField, a ``StringToIntegerConverter`` is needed - this converter type is provided by ``FormSupport.converterFor()``

Ultimately this uses an implementation of ``ConverterFactory`` to instantiate the converter itself. The default implementation ``DefaultConverterFactory``, iterates through all ``ConverterSets`` classes defined via Guice until it finds one to match the desired model and presentation class, or throws an exception if none found.

By default there is just one ``ConverterSet``, the ``BaseConverterSet``.

Adding / Replacing Converters
-----------------------------

Converters can be added by defining your own ``ConverterSet``, and adding it in one of two ways:

- sub-class ``ConverterModule`` and override the ``define()`` method to provide bindings to additional ``ConverterSet`` implementations, and replace ``ConverterModule`` in your ``BindingsCollator``
- create a new module using ``ConverterModule`` as an example (in particular the MultiBinder), and add the new module to your ``BindingsCollator``




