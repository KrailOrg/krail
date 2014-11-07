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
package uk.q3c.krail.base.view.layout;

import com.vaadin.ui.Component;
import uk.q3c.krail.base.ui.ScopedUI;
import uk.q3c.krail.base.view.layout.DefaultViewConfig.Split;

import java.util.List;

/**
 * A further level of abstraction from the standard Vaadin Layout components, the implementations of this interface
 * provide some common composite layouts, typical for web (and other) applications. The core Krail library only provide
 * one
 * implementation, {@link VerticalViewLayout}, but it is expected that others will be added as a separate library.
 *
 * @author David Sowerby 14 Aug 2013
 */
public interface ViewLayout {

    /**
     * A safe copy of the components contained by this template
     *
     * @return
     */
    List<Component> orderedComponents();

    /**
     * Adds a component, and allocates it the next available index. If the index exceeds what the layout would expect,
     * no error is raised, and the component held even if it is not used. The order in which components are added must
     * reflect the ordering expected by the implementation. Typically there will be a diagram documenting the layout
     * and
     * component order.
     *
     * @param index
     * @param component
     */
    void addComponent(Component component);

    /**
     * Assembles (or re-assembles) the layout. Uses the config provided by setConfig
     */
    public void assemble();

    /**
     * gets the current config
     */
    public ViewConfig getConfig();

    /**
     * sets the config to use
     */
    public void setConfig(ViewConfig config);

    /**
     * Returns the Component at the root of the layout. This is placed in the {@link ScopedUI} to display the view
     * layout and its contained components.
     *
     * @return
     */
    public Component getLayoutRoot();

    /**
     * Each implementation is required to provide a default configuration object. This ensures that the defaults are
     * relevant to the implementation.
     *
     * @return
     */
    public ViewConfig defaultConfig();

    /**
     * Each layout must define what is a valid split for that specific implementation. Invalid splits are ignored, and
     * do not raise errors.
     *
     * @param split
     *
     * @return
     */
    boolean isValidSplit(Split split);

    /**
     * Checks that splits are 'valid' (as defined by {@link #isValidSplit(Split)}), and retains a set of validated
     * Split
     * instances for layout processing
     *
     * @param config
     */
    void validateSplits();

    /**
     * The number of defined splits which are valid for this layout
     *
     * @return
     */
    int validSplitCount();

}
