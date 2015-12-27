/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.util.ID;

import javax.annotation.Nonnull;
import java.util.Optional;

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
public abstract class ViewBase implements KrailView {

    private static Logger log = LoggerFactory.getLogger(ViewBase.class);
    private boolean componentsConstructed;
    private boolean dirty;
    private boolean idsAssigned;
    private Component rootComponent;

    @Inject
    protected ViewBase() {
        super();

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

    }

    /**
     * If {@link #idsAssigned} is false, {@link #setIds()} - the view components have already been constructed in {@link #buildView}
     */
    @Override
    public void afterBuild(AfterViewChangeBusMessage busMessage) {
        if (!idsAssigned) {
            setIds();
            idsAssigned = true;
        }
        loadData(busMessage);
    }

    /**
     * You only need to override / implement this method if you are using TestBench, or another testing tool which looks for debug ids. If you do override it
     * to add your own subclass ids, make sure you call super
     */
    protected void setIds() {
        getRootComponent().setId(ID.getId(Optional.empty(), this, getRootComponent()));
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

    public void setRootComponent(@Nonnull Component rootComponent) {
        checkNotNull(rootComponent);
        this.rootComponent = rootComponent;
    }

    /**
     * Default does nothing, overload to load your data
     *
     * @param busMessage
     */
    protected void loadData(AfterViewChangeBusMessage busMessage) {

    }

    @Override
    public String viewName() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeBuild(ViewChangeBusMessage busMessage) {

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
     * @param busMessage
     *         a message sent by the Event Bus to signify a chnage of View
     */
    protected abstract void doBuild(ViewChangeBusMessage busMessage);

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuild() {
        componentsConstructed = false;
    }
}
