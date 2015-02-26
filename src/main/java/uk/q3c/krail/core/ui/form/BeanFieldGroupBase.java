/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.ui.form;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import uk.q3c.krail.core.data.Entity;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.core.validation.BeanValidator;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.LabelKey;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

//import com.vaadin.data.validator.BeanValidator;

/**
 * This class is a replacement for {@link com.vaadin.data.fieldgroup.BeanFieldGroup}, and for quite a few methods, is a
 * copy of it.
 * <p>
 * Sub-class it and add fields, with I18N annotations as required
 * <p>
 * Unfortunately the way the Vaadin implementation of it is written makes it impossible to change the {@link
 * BeanValidator} implementation. A different {@link BeanValidator} is needed to enable integration with Krail's I18N
 * features.
 * <p>
 * <p>
 * Other features are also added to enable the use of Krail's I18N framework to provide captions and descriptions for
 * the Field components
 * <p>
 * <p>
 * Sub-classes should declare the Fields
 * <p>
 * Created by David Sowerby on 03/02/15.
 */
public abstract class BeanFieldGroupBase<T extends Entity> extends FieldGroup implements BeanFieldGroup<T>,
        OptionContext {

    private final I18NProcessor i18NProcessor;
    private final Map<Field<?>, BeanValidator<T>> defaultValidators;
    private Class<T> beanType;
    private Provider<BeanValidator> beanValidatorProvider;
    private Option option;

    @Inject
    public BeanFieldGroupBase(I18NProcessor i18NProcessor, Provider<BeanValidator> beanValidatorProvider, Option option) {
        this.i18NProcessor = i18NProcessor;
        this.beanValidatorProvider = beanValidatorProvider;
        this.option = option;
        this.defaultValidators = new HashMap<>();
    }

    private static java.lang.reflect.Field getField(Class<?> cls, String propertyId) throws SecurityException,
            NoSuchFieldException {
        if (propertyId.contains(".")) {
            String[] parts = propertyId.split("\\.", 2);
            // Get the type of the field in the "cls" class
            java.lang.reflect.Field field1 = getField(cls, parts[0]);
            // Find the rest from the sub type
            return getField(field1.getType(), parts[1]);
        } else {
            try {
                // Try to find the field directly in the given class
                return cls.getDeclaredField(propertyId);
            } catch (NoSuchFieldException e) {
                // Try super classes until we reach Object
                Class<?> superClass = cls.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    return getField(superClass, propertyId);
                } else {
                    throw e;
                }
            }
        }
    }

    private static String getFieldName(Class<?> cls, String propertyId) throws SecurityException, NoSuchFieldException {
        for (java.lang.reflect.Field field1 : cls.getDeclaredFields()) {
            if (propertyId.equals(minifyFieldName(field1.getName()))) {
                return field1.getName();
            }
        }
        // Try super classes until we reach Object
        Class<?> superClass = cls.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return getFieldName(superClass, propertyId);
        } else {
            throw new NoSuchFieldException();
        }
    }

    @Override
    @Nonnull
    public Option getOption() {
        return option;
    }

    /**
     * Wraps {@code bean} in a new {@link BeanItem} instance and calls {@link #setBeanItem(BeanItem)}
     *
     * @param bean
     */
    public void setBean(T bean) {
        BeanItem item = new BeanItem(bean);
        setBeanItem(item);
    }

    /**
     * Sets the beanItem to use (and therefore the data), translates I18N annotation captions and descriptions and
     * applies them to Fields
     *
     * @param beanItem
     */
    public void setBeanItem(BeanItem<T> beanItem) {
        //noinspection unchecked
        if (beanType == null) {
            beanType = (Class<T>) beanItem.getBean()
                                          .getClass();
            buildAndBindMemberFields(this);
            i18NProcessor.translate(this);
        }

        setItemDataSource(beanItem);
    }

    @Override
    protected Class<?> getPropertyType(Object propertyId) {
        if (getItemDataSource() != null) {
            return super.getPropertyType(propertyId);
        } else {
            // Data source not set so we need to figure out the type manually
            /*
             * toString should never really be needed as propertyId should be of
             * form "fieldName" or "fieldName.subField[.subField2]" but the
             * method declaration comes from parent.
             */
            java.lang.reflect.Field f;
            try {
                f = getField(beanType, propertyId.toString());
                return f.getType();
            } catch (SecurityException e) {
                throw new BindException("Cannot determine type of propertyId '" + propertyId + "'.", e);
            } catch (NoSuchFieldException e) {
                throw new BindException("Cannot determine type of propertyId '" + propertyId + "'. The propertyId was" +
                        " not found in " + beanType.getName(), e);
            }
        }
    }

    @Override
    protected Object findPropertyId(java.lang.reflect.Field memberField) {
        String fieldName = memberField.getName();
        Item dataSource = getItemDataSource();
        if (dataSource != null && dataSource.getItemProperty(fieldName) != null) {
            return fieldName;
        } else {
            String minifiedFieldName = minifyFieldName(fieldName);
            try {
                return getFieldName(beanType, minifiedFieldName);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Helper method for setting the data source directly using a bean. This
     * method wraps the bean in a {@link BeanItem} and calls
     * {@link #setItemDataSource(Item)}.
     *
     * @param bean
     *         The bean to use as data source.
     */
    public void setItemDataSource(T bean) {
        BeanItem<T> beanItem = new BeanItem<T>(bean);
        setItemDataSource(beanItem);
    }

    @Override
    public void bind(Field field, Object propertyId) {
        ensureNestedPropertyAdded(propertyId);
        super.bind(field, propertyId);
    }

    private void ensureNestedPropertyAdded(Object propertyId) {
        if (getItemDataSource() != null) {
            // The data source is set so the property must be found in the item.
            // If it is not we try to add it.
            try {
                getItemProperty(propertyId);
            } catch (BindException e) {
                // Not found, try to add a nested property;
                // BeanItem property ids are always strings so this is safe
                getItemDataSource().addNestedProperty((String) propertyId);
            }
        }
    }

    @Override
    public BeanItem<T> getItemDataSource() {
        //noinspection unchecked
        return (BeanItem<T>) super.getItemDataSource();
    }

    @Override
    public void setItemDataSource(Item item) {
        if (!(item instanceof BeanItem)) {
            throw new RuntimeException(getClass().getSimpleName() + " only supports BeanItems as item data source");
        }
        super.setItemDataSource(item);
    }

    @Override
    public Field<?> buildAndBind(String caption, Object propertyId) throws BindException {
        ensureNestedPropertyAdded(propertyId);
        return super.buildAndBind(caption, propertyId);
    }

    @Override
    public void unbind(Field<?> field) throws BindException {
        super.unbind(field);

        BeanValidator removed = defaultValidators.remove(field);
        if (removed != null) {
            field.removeValidator(removed);
        }
    }

    @Override
    protected void configureField(Field<?> field) {
        super.configureField(field);
        // Add Bean validators if there are annotations
        if (!defaultValidators.containsKey(field)) {

            BeanValidator<T> validator = beanValidatorProvider.get();
            validator.init(beanType, getPropertyId(field).toString(), getOptionUseFieldNameInValidationMessage());
            field.addValidator(validator);
            defaultValidators.put(field, validator);
        }
    }

    public boolean getOptionUseFieldNameInValidationMessage() {
        return option.get(false, LabelKey.Use_Field_Name_in_Validation_Message, this.getClass()
                                                                                                  .getSimpleName());
    }

    public void setOptionUseFieldNameInValidationMessage(boolean useFieldNames) {
        option.set(useFieldNames, LabelKey.Use_Field_Name_in_Validation_Message, this.getClass()
                                                                                                   .getSimpleName());
    }


    //    /**
    //     * Convenience method to bind Fields from a given "field container" to a
    //     * given bean with buffering disabled.
    //     * <p>
    //     * The returned {@link BeanFieldGroup} can be used for further
    //     * configuration.
    //     *
    //     * @see #bindFieldsBuffered(Object, Object)
    //     * @see #bindMemberFields(Object)
    //     * @since 7.2
    //     * @param bean
    //     *            the bean to be bound
    //     * @param objectWithMemberFields
    //     *            the class that contains {@link Field}s for bean properties
    //     * @return the bean field group used to make binding
    //     */
    //    public BeanFieldGroup<T> bindFieldsUnbuffered(T bean,
    //                                                             Object objectWithMemberFields) {
    //        return createAndBindFields(bean, objectWithMemberFields, false);
    //    }
    //
    //    /**
    //     * Convenience method to bind Fields from a given "field container" to a
    //     * given bean with buffering enabled.
    //     * <p>
    //     * The returned {@link BeanFieldGroup} can be used for further
    //     * configuration.
    //     *
    //     * @see #bindFieldsUnbuffered(Object, Object)
    //     * @see #bindMemberFields(Object)
    //     * @since 7.2
    //     * @param bean
    //     *            the bean to be bound
    //     * @param objectWithMemberFields
    //     *            the class that contains {@link Field}s for bean properties
    //     * @return the bean field group used to make binding
    //     */
    //    public   BeanFieldGroup<T> bindFieldsBuffered(T bean,
    //                                                           Object objectWithMemberFields) {
    //        return createAndBindFields(bean, objectWithMemberFields, true);
    //    }
    //
    //    private BeanFieldGroup<T> createAndBindFields(T bean,
    //                                                             Object objectWithMemberFields, boolean buffered) {
    //        @SuppressWarnings("unchecked")
    //        BeanFieldGroup<T> beanFieldGroup = new BeanFieldGroup<T>(
    //                (Class<T>) bean.getClass());
    //        beanFieldGroup.setItemDataSource(bean);
    //        beanFieldGroup.setBuffered(buffered);
    //        beanFieldGroup.bindMemberFields(objectWithMemberFields);
    //        return beanFieldGroup;
    //    }

}
