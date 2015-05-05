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

package uk.q3c.krail.core.navigate;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a navigation state (basically a URI fragment, potentially with parameters) but with its component parts
 * already broken out by an implementation of {@link URIFragmentHandler}. It is used to remove the need to repeatedly
 * break down the URI to get to parts of its content, but is entirely passive.
 * <p/>
 * Essentially there are two components to the state,
 * <ol>
 * <li>the fragment, which is the entire URI after the '#'</li>
 * <li>a set of component parts (virtualPage, parameters and path segments)</li>
 * </ol>
 * <p/>
 * To keep these consistent, you will need to use a {@link URIFragmentHandler}. So for example, if you have a new
 * fragment to use, simply call {@link URIFragmentHandler#navigationState(String)} to create a new NavigationState
 * instance.
 * <p/>
 * If you want to modify an existing navigation state, make the modification to one or more of its component parts and
 * call {@link URIFragmentHandler#updateFragment(NavigationState)} to make the fragment consistent again. For example,
 * to redirect to another 'page', but retain the parameters:
 * <p/>
 * <code>navigationState.setVirtualPage("another/newpage");<br>
 * uriFragmentHandler.updateFragment(navigationState);</code>
 * <p/>
 * This is functionally the same as
 * <p/>
 * <code>navigationState.setVirtualPage("another/newpage");<br>
 * navigationState.setFragment(uriFragmentHandler.fragment(navigationState));
 * </code>
 * <p/>
 * A NavigationState 'a' is equal to NavigationState 'b' if a.getFragment.equals(b.getFragment())
 *
 * @author David Sowerby
 */
public class NavigationState implements Serializable {
    private final Map<String, String> parameters = new LinkedHashMap<String, String>();
    // fragment is out of date
    private boolean dirty;
    private String fragment;
    private List<String> pathSegments;
    private String virtualPage;

    @Inject
    public NavigationState() {
        super();
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
        dirty = false;
    }

    public String getVirtualPage() {
        return virtualPage;
    }

    public void setVirtualPage(String virtualPage) {
        this.virtualPage = virtualPage;
        dirty = true;
    }

    public String getUriSegment() {
        return pathSegments.get(pathSegments.size() - 1);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<String> getParameterList() {
        List<String> list = new ArrayList<>();
        for (Entry<String, String> entry : parameters.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        return list;
    }

    public String getParameterValue(String key) {
        return parameters.get(key);
    }

    public List<String> getPathSegments() {
        return pathSegments;
    }

    public void setPathSegments(List<String> pathSegments) {
        this.pathSegments = pathSegments;
        dirty = true;
    }

    /**
     * Parameter order is maintained, so if the value of an existing parameter is changed, the parameter will remain in
     * its original position
     *
     * @param key
     * @param value
     */
    public void setParameterValue(String key, String value) {
        addParameter(key, value);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
        dirty = true;
    }

    public void removeParameter(String key) {
        parameters.remove(key);
    }

    @Override
    public String toString() {
        return fragment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NavigationState other = (NavigationState) obj;
        if (fragment == null) {
            if (other.fragment != null) return false;
        } else if (!fragment.equals(other.fragment)) return false;
        return true;
    }

    public boolean hasParameter(String parameterName) {
        return parameters.containsKey(parameterName);
    }

    public NavigationState virtualPage(final String virtualPage) {
        setVirtualPage(virtualPage);
        return this;
    }

    public NavigationState parameter(String key, String value) {
        addParameter(key, value);
        return this;
    }


}
