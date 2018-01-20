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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This provides a more strict interpretation of the UriFragment than Vaadin does by default. It requires that the URI
 * structure is of the form:<br>
 * <br>
 * http://example.com/domain#!finance/report/risk/id=1223/year=2012 (<I>with or without the bang after the hash,
 * depending on the <code>useBang</code> setting)</I> <br>
 * <br>
 * where: <br>
 * <br>
 * finance/report/risk/ <br>
 * <br>
 * is a "virtual page path" and is represented by a View <br>
 * <br>
 * and everything after it is paired parameters. If a segment within what should be paired parameters is malformed, it
 * is ignored, and when the URI is reconstructed, will disappear. So for example: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223/year2012 <br></code> <br>
 * would be treated as: <br>
 * <br>
 * <code>http://example.com/domain#!finance/report/risk/id=1223</code><br>
 * The year parameter has been dropped because it has no "=" <br>
 * <br>
 * Optionally uses hash(#) or hashBang(#!). Some people get excited about hashbangs. Try Googling it<br>
 * <br>
 */
public class StrictURIFragmentHandler implements URIFragmentHandler, Serializable {

    private boolean useBang = false;

    @Inject
    public StrictURIFragmentHandler() {
        super();
    }

    @Override
    public boolean isUseBang() {
        return useBang;
    }

    @Override
    public void setUseBang(boolean useBang) {
        if (this.useBang != useBang) {
            this.useBang = useBang;
        }
    }

    /**
     * Creates and returns a {@link NavigationState} with elements of the {@code fragment} decoded. The "virtual page" is
     * assumed to finish as soon as a paired parameter is found. No attempt is made to validate the actual structure of
     * the path, so for example something like <code>view//subview/a=b</code> will result in a virtual page of
     * <code>view//subview</code>. If <code>uri</code> is null or empty, the {@code fragment} is considered to be an empty String. If
     * <code>navigationState</code> contains only paired parameters, the virtual page is set to an empty string.
     */
    @Override
    public NavigationState navigationState(String fragment) {
        NavigationState navigationState = new NavigationState();
        String updatedFragment = StringUtils.isEmpty(fragment) ? "" : stripBangAndTrailingSlash(fragment);
        navigationState.fragment(updatedFragment);
        updateParts(navigationState);
        return navigationState;
    }

    @Override
    public NavigationState navigationState(URI uri) {
        return navigationState(uri.getFragment());
    }


    @Override
    public void updateParts(NavigationState navigationState) {
        navigationState.setUpdateInProgress(true);
        String fragment = navigationState.getFragment();

        List<String> pathSegments = new ArrayList<>();
        // no parameters, everything is the virtual page path
        // if (!fragment.contains("=")) {
        // navigationState.setVirtualPage(fragment);
        // return navigationState;
        // }

        Iterable<String> segments = Splitter.on('/')
                                            .split(fragment);

        boolean paramsStarted = false;
        Iterator<String> iter = segments.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            if (paramsStarted) {
                addParameter(navigationState, s);
            } else {
                if (s.contains("=")) {
                    paramsStarted = true;
                    addParameter(navigationState, s);
                } else {
                    pathSegments.add(s);
                }
            }
        }
        navigationState.setUpdateInProgress(true);
        // join the virtual page path up again
        navigationState.pathSegments(pathSegments);
        validateSegments(navigationState);
        navigationState.updated();
    }

    private String virtualPageFromSegments(List<String> pathSegments) {
        return
                Joiner.on('/')
                      .join(pathSegments.toArray());
    }

    private void addParameter(NavigationState navigationState, String s) {
        if (s.contains("=")) {
            Iterable<String> segments = Splitter.on('=')
                                                .split(s);
            Iterator<String> iter = segments.iterator();
            String key = iter.next();
            String value = iter.next();
            if (Strings.isNullOrEmpty(key)) {
                return;
            }
            if (Strings.isNullOrEmpty(value)) {
                return;
            }
            navigationState.parameter(key, value);
        }
    }

    private String stripBangAndTrailingSlash(String path) {
        int copyStart = 0;
        int copyEnd = path.length();

        if (path.startsWith("!")) {
            copyStart++;
            if (path.charAt(1) == '/') {
                copyStart++;
            }
        } else {
            if (path.startsWith("/")) {
                copyStart++;
            }
        }

        if (path.endsWith("/")) {
            copyEnd--;
        }
        return path.substring(copyStart, copyEnd);
    }

    /**
     * Updates the fragment in {@code navigationState} from the component parts of {@code navigationState}
     *
     * @see uk.q3c.krail.core.navigate.URIFragmentHandler#updateFragment(uk.q3c.krail.core.navigate.NavigationState)
     */
    @Override
    public void updateFragment(NavigationState navigationState) {
        navigationState.setUpdateInProgress(true);
        navigationState.fragment(fragment(navigationState));
        validateSegments(navigationState);
        navigationState.updated();
    }

    private void validateSegments(NavigationState navigationState) {

        if (navigationState.getVirtualPage() == null) {
            String virtualPage = virtualPageFromSegments(navigationState.getPathSegments());
            navigationState.virtualPage(virtualPage);
        } else if (navigationState.getPathSegments()
                                  .isEmpty()) {
            navigationState.pathSegments(segmentsFromVirtualPage(navigationState.getVirtualPage()));
        }
    }

    private List<String> segmentsFromVirtualPage(String virtualPage) {
        return ImmutableList.copyOf(Splitter.on('/')
                                            .split(virtualPage));
    }

    @Override
    public String fragment(NavigationState navigationState) {
        StringBuilder buf = new StringBuilder();
        if (useBang) {
            buf.append('!');
        }
        String vp = navigationState.getVirtualPage() == null ? virtualPageFromSegments(navigationState.getPathSegments()) : navigationState.getVirtualPage();
        buf.append(vp);

        // append the parameters
        for (Map.Entry<String, String> entry : navigationState.getParameters()
                                                              .entrySet()) {
            buf.append('/');
            buf.append(entry.getKey());
            buf.append('=');
            buf.append(entry.getValue());
        }

        return buf.toString();
    }


}
