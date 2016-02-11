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

package uk.q3c.krail.core.navigate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents a navigation state (basically a URI fragment, potentially with parameters) but with its component parts
 * already broken out by an implementation of {@link URIFragmentHandler}. It is used to remove the need to repeatedly
 * break down the URI to get to parts of its content, but is entirely passive.
 * <p>
 * Essentially there are two components to the state,
 * <ol>
 * <li>the fragment, which is the entire URI after the '#'</li>
 * <li>a set of component parts (virtualPage, parameters and path segments)</li>
 * </ol>
 * <p>
 * To keep these consistent, you will need to use a {@link URIFragmentHandler}. So for example, if you have a new
 * fragment to use, simply call {@link URIFragmentHandler#navigationState(String)} to create a new NavigationState
 * instance.
 * <p>
 * If you want to modify an existing navigation state, make the modification to one or more of its component parts and
 * call {@link URIFragmentHandler#updateFragment(NavigationState)} to make the fragment consistent again. For example,
 * to redirect to another 'page', but retain the parameters:
 * <p>
 * <code>navigationState.setVirtualPage("another/newpage");<br>
 * uriFragmentHandler.updateFragment(navigationState);</code>
 * <p>
 * This is functionally the same as
 * <p>
 * <code>navigationState.setVirtualPage("another/newpage");<br>
 * navigationState.setFragment(uriFragmentHandler.fragment(navigationState));
 * </code>
 * <p>
 * A NavigationState 'a' is equal to NavigationState 'b' if a.getFragment.equals(b.getFragment())
 *
 * @author David Sowerby
 */
public class NavigationState implements Serializable {
    private static Logger log = getLogger(NavigationState.class);
    private final Map<String, String> parameters = new LinkedHashMap<String, String>();
    // fragment is out of date
    private boolean fragmentChanged;
    private boolean partsChanged;
    private String fragment;
    private List<String> pathSegments;
    private String virtualPage;
    private boolean updateInProgress;

    @Inject
    public NavigationState() {
        super();
    }


    public void setUpdateInProgress(boolean updateInProgress) {
        this.updateInProgress = updateInProgress;
    }

    public String getFragment() {
        validStateCheck();
        return fragment;
    }

    /**
     * use {@link #fragment(String)}
     *
     * @param fragment
     */
    @Deprecated
    public void setFragment(@Nonnull String fragment) {
        fragment(fragment);
    }

    public NavigationState fragment(@Nonnull String fragment) {
        checkNotNull(fragment);
        this.fragment = fragment;
        fragmentChanged = true;
        return this;
    }

    public String getVirtualPage() {
        validStateCheck();
        return virtualPage;
    }

    /**
     * Use virtualPage(String) instead
     */
    @Deprecated
    public void setVirtualPage(@Nonnull String virtualPage) {
        virtualPage(virtualPage);
    }

    public String getUriSegment() {
        validStateCheck();
        return pathSegments.get(pathSegments.size() - 1);
    }

    public boolean isDirty() {
        return fragmentChanged || partsChanged;
    }

    public Map<String, String> getParameters() {
        validStateCheck();
        return ImmutableMap.copyOf(parameters);
    }

    public List<String> getParameterList() {
        validStateCheck();
        return parameters.entrySet()
                         .stream()
                         .map(entry -> entry.getKey() + '=' + entry.getValue())
                         .collect(Collectors.toList());
    }

    private void validStateCheck() {
        if (isDirty() && !(updateInProgress)) {
            throw new NavigationStateException("The navigation state is inconsistent, call update() before accessing");
        }
    }

    public String getParameterValue(@Nonnull String key) {
        validStateCheck();
        checkNotNull(key);
        return parameters.get(key);
    }

    @Nonnull
    public List<String> getPathSegments() {
        validStateCheck();
        return pathSegments == null ? ImmutableList.of() : ImmutableList.copyOf(pathSegments);
    }

    /**
     * use {#virtualPage(String} instead
     */
    @Deprecated
    public void setPathSegments(@Nonnull List<String> pathSegments) {
        pathSegments(pathSegments);
    }

    public NavigationState pathSegments(@Nonnull List<String> pathSegments) {
        checkNotNull(pathSegments);
        this.pathSegments = pathSegments;
        partsChanged = true;
        if (!updateInProgress) {
            virtualPage = null;
        }
        return this;
    }


    /**
     * Use {@link #parameter(String, String)}
     */
    @Deprecated
    public void setParameterValue(@Nonnull String key, @Nonnull String value) {
        parameter(key, value);
    }

    /**
     * Use {@link #parameter(String, String)}
     */
    @Deprecated
    public void addParameter(@Nonnull String key, @Nonnull String value) {
        parameter(key, value);
    }

    public NavigationState removeParameter(@Nonnull String key) {
        checkNotNull(key);
        String result = parameters.remove(key);
        if (result != null) {
            partsChanged = true;
        }
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return prime * result + ((fragment == null) ? 0 : fragment.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        validStateCheck();
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NavigationState other = (NavigationState) obj;
        if (fragment == null) {
            if (other.getFragment() != null) return false;
        } else if (!fragment.equals(other.getFragment())) return false;
        return true;
    }

    public boolean isFragmentChanged() {
        return fragmentChanged;
    }

    public boolean isPartsChanged() {
        return partsChanged;
    }

    public boolean hasParameter(@Nonnull String parameterName) {
        validStateCheck();
        checkNotNull(parameterName);
        return parameters.containsKey(parameterName);
    }

    public NavigationState virtualPage(@Nonnull final String virtualPage) {
        checkNotNull(virtualPage);
        checkNotNull(virtualPage);
        this.virtualPage = virtualPage;
        partsChanged = true;
        if (!updateInProgress) {
            pathSegments = null;
        }
        return this;
    }

    public NavigationState parameter(@Nonnull String key, @Nonnull String value) {
        checkNotNull(key);
        checkNotNull(value);
        parameters.put(key, value);
        partsChanged = true;
        return this;
    }

    /**
     * This appears a bit circular, but the {@link URIFragmentHandler} sets the rules for how fragments are constructed. Updates according to whether
     * {@link #fragmentChanged} or {@link #partsChanged} is true.  If both are true, {@link #fragmentChanged} takes precedence
     *
     * @param handler the fragment handler used to make the update
     * @return this for fluency
     */
    public void update(URIFragmentHandler handler) {
        updateInProgress = true;
        if (fragmentChanged) {
            handler.updateParts(this);
        } else {
            if (partsChanged) {
                handler.updateFragment(this);
            }
        }
        log.debug("State has not been changed, no update needed");
    }


    public void updated() {
        fragmentChanged = false;
        partsChanged = false;
        updateInProgress = false;
    }
}
