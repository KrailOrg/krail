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

package uk.q3c.krail.core.user.opt;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This default implementation has no knowledge of the application being developed.  If hierarchies are
 * required, this implementation will need to be replaced by your own implementation.  A sub-class of {@link
 * OptionModule} should then contain a binding for your new implementation
 * <p>
 * Created by David Sowerby on 05/12/14.
 */
public class DefaultOptionLayerDefinition implements OptionLayerDefinition {
    /**
     * This implementation simply returns an empty list as there is no way of knowing what an application would need to
     * implement
     *
     * @param userId
     *         id for the user, used to lookup layer definitions
     * @param hierarchy
     *         an optional hierarchy, so that options could be from, for example, a geography hierarchy and a company
     *         structure hierarchy
     *
     * @return
     */
    @Override
    public List<String> getLayers(String userId, Optional<String> hierarchy) {
        return new ArrayList<>();
    }
}
