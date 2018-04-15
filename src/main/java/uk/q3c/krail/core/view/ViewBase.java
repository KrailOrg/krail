/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.DefaultNavigator;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ComponentIdGenerator;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides default View behaviour suitable for most view implementations.  Override methods as necessary for your needs.  This is the default sequence:
 * <p>
 * <ol><li>{@link #init()} does nothing by default, override if you need to prepare the view in some way</li>
 * <li>{@link #beforeBuild} does nothing by default. Example use might be to reset #componentsConstructed dependent on url parameter values, forcing a rebuild
 * under certain conditions</li>
 * <li>{@link #buildView} delegates to sub-classes to provide component construction in {@link #doBuild}, then sets {@link #componentsConstructed}</li>
 * <li>{@link #afterBuild} calls {@link #setIds} to provide debug Ids, unless {@link #idsAssigned} is true, and then calls {@link #loadData} </li>
 * <li>if you need to load data, one good way to do that is to annotate your sub-class with @Listener, and provide a @Andler annoated method to load the data.
 * data loading process</li>
 * </ol>
 * <p>
 * Note:  The {@link #rootComponent} must be set by sub-classes by an implementation of {@link #doBuild}
 */
public abstract class ViewBase implements KrailView, Serializable {

    private static Logger log = LoggerFactory.getLogger(ViewBase.class);
    protected I18NKey nameKey = LabelKey.Unnamed;
    protected I18NKey descriptionKey = DescriptionKey.No_description_provided;
    private Translate translate;


    private SerializationSupport serializationSupport;
    private boolean componentsConstructed;
    private boolean dirty;
    @Deprecated
    private boolean idsAssigned;
    private Component rootComponent;

    @Inject
    protected ViewBase(Translate translate, SerializationSupport serializationSupport) {
        super();
        this.translate = translate;
        this.serializationSupport = serializationSupport;
    }

    public Translate getTranslate() {
        return translate;
    }

    public boolean isComponentsConstructed() {
        return componentsConstructed;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        log.debug("====> View.init called");
    }


    @Override
    public void afterBuild(AfterViewChangeBusMessage busMessage) {
        log.debug("View.afterBuild called");
        if (!idsAssigned) {
            setIds();
            idsAssigned = true;
        }
        loadData(busMessage);
    }

    /**
     * As of 0.14.0.0 ids are assigned by automatically by the {@link DefaultNavigator}, which invokes {@link ComponentIdGenerator}.
     * <p>
     * You can still use this method if you wish, and any ids set through this method will override those set automatically
     * <p>
     * This method is only invoked if {@link #idsAssigned} is false
     * <p>
     * You only need to override / implement this method if you are using TestBench, or another testing tool which looks for debug ids. If you do override it
     * to add your own subclass ids, make sure you call super
     *
     * @deprecated use the {@link ComponentIdGenerator} implementation to create and apply the Ids consistently
     */
    @Deprecated
    protected void setIds() {

    }

    @Override
    public Component getRootComponent() {
        if (rootComponent == null) {
            throw new ViewBuildException("Root component cannot be null in " + getClass().getName() + ". Has your " +
                    "buildView() method called " +
                    "setRootComponent()?");
        }
        return rootComponent;
    }

    public void setRootComponent(Component rootComponent) {
        checkNotNull(rootComponent);
        this.rootComponent = rootComponent;
    }

    /**
     * Default does nothing, overload to load your data
     *
     * @param busMessage
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    protected void loadData(AfterViewChangeBusMessage busMessage) {
        log.debug("====> View.loadData called");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    @Override
    public void beforeBuild(ViewChangeBusMessage busMessage) {
        log.debug("====> View.beforeBuild called");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildView(ViewChangeBusMessage busMessage) {
        if (!componentsConstructed) {
            doBuild(busMessage);
        }
        componentsConstructed = true;
    }

    /**
     * Implement this method to construct your components.  You must also set {@link #rootComponent} (this is the component which will be placed in the parent
     * {@link UI}, and is usually a layout
     *
     * @param busMessage a message sent by the Event Bus to signify a chnage of View
     */
    protected abstract void doBuild(ViewChangeBusMessage busMessage);

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuild() {
        componentsConstructed = false;
    }

    public I18NKey getNameKey() {
        return nameKey;
    }

    public void setNameKey(I18NKey nameKey) {
        this.nameKey = nameKey;
    }

    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(I18NKey descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public String getName() {
        return translate.from(nameKey);
    }

    public String getDescription() {
        return translate.from(descriptionKey);
    }

    public SerializationSupport getSerializationSupport() {
        return serializationSupport;
    }

    @SuppressWarnings("Duplicates")
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        beforeDeserialization();
        inputStream.defaultReadObject();
        beforeTransientInjection();
        serializationSupport.injectTransientFields(this);
        afterTransientInjection();
        serializationSupport.checkForNullTransients();
    }


    /**
     * By default does nothing but can be overridden to execute code before any other action is taken for deserialization.
     * It cannot be used to set exclusions for [serializationSupport], as [serializationSupport] will be deserialized
     * after this call
     */
    protected void beforeDeserialization() {

    }


    /**
     * By default does nothing but can be overridden to populate transient fields before {@link #serializationSupport}
     * injects Guice dependencies. It could be used to set exclusions for {@link #serializationSupport}
     */
    protected void beforeTransientInjection() {

    }

    /**
     * By default does nothing but can be overridden to populate transient fields after {@link #serializationSupport}
     * has injected Guice dependencies.
     */
    protected void afterTransientInjection() {

    }


}

