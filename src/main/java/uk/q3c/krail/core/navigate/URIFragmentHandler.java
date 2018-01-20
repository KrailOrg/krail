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

import java.net.URI;

/**
 * Handles the decoding and encoding of a URI Fragment (as defined by the Java class {@link URI#getFragment()}.
 *
 * @author david
 */
public interface URIFragmentHandler {

    /**
     * @return
     */
    boolean isUseBang();

    /**
     * If true, use "#!" (hashbang) after the base URI, if false use "#" (hash).
     *
     * @param useBang
     */
    void setUseBang(boolean useBang);

    /**
     * Returns a {@link NavigationState}, which is a representation of a URI fragment, broken down into its constituent
     * parts
     *
     * @return
     */

    NavigationState navigationState(String fragment);

    NavigationState navigationState(URI uri);

    /**
     * Returns a URI fragment encoded from the {@code navigationState}
     *
     * @param navigationState
     *
     * @return
     */
    String fragment(NavigationState navigationState);

    /**
     * Update the navigation state parts to match its fragment.  Use after setting the fragment to ensure consistency
     *
     * @param navigationState the {@link NavigationState} to update
     */
    void updateParts(NavigationState navigationState);

    /**
     * Updates the fragment part of the {@code navigationState} from its component parts
     *
     */
    void updateFragment(NavigationState navigationState);

}
