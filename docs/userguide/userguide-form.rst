=====
Forms
=====

Overview
========

Vaadin provides some support for Forms with ``Binder``, but Krail takes that further.  The process of constructing a form is:

- a ``FormConfiguration`` is specified in using either the Direct or Annotation method of creating a ``Sitemap`` entry,
- the view for that ``Sitemap`` entry must be ``Form.class``

When the ``Form`` is constructed, the configuration is also applied, and the ``FormBuilder`` selects a ``FormTypeBuilder``, from parameters specified in the  ``FormConfiguration`` to do the actual construction of the form.

Form types currently include "Simple" and "Master-Detail"

The ``FormTypeBuilder`` uses the ``FormConfiguration`` to build the form as required - it may construct the UI and bind properties to them, or map existing UI components to those properties.

The binding is carried out by ``KrailBeanValidationBinder``, which also takes care of integrating JSR303 validation and I18N.


Model to Presentation mapping
=============================

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

Ultimately this uses an implementation of ``ConverterFactory`` to instantiate the converter itself. The default implementation ``DefaultConverterFactory``, iterates through all defined ``ConverterSets``s until it finds one to match the desired model and presentation class, or throws an exception if none found.

By default there is just one ``ConverterSet``, ``BaseConverterSet``

Adding / Replacing Converters
-----------------------------

Converters can be added by defining your own ``ConverterSet``.  This can be added in one of two ways:

- sub-class ``ConverterModule`` and override the ``define()`` method to provide your own bindings to ``ConverterSet``s, and replace ``ConverterModule`` in your ``BindingsCollator``
- create a new module using ``ConverterModule`` as an example, and add the new module to your ``BindingsCollator``




