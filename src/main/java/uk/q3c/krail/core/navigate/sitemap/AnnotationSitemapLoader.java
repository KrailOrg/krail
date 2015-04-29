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
package uk.q3c.krail.core.navigate.sitemap;

/**
 * Implementations take the {@link MasterSitemap} definitions captured by the {@link AnnotationSitemapModule}, then load them into the {@link MasterSitemap}
 * when invoked by the {@link SitemapService}.
 *
 * @author David Sowerby
 * @see DirectSitemapLoader
 * @see FileSitemapLoader
 */
public interface AnnotationSitemapLoader extends SitemapLoader {

    String LABEL_NOT_VALID = "Annotation for View {0}.  {1} is not a valid key value for enum {2}";
    String REDIRECT_FROM_IGNORED = "The @RedirectFrom annotation for {0} has been ignored.  A @RedirectFrom "
            + "must be accompanied by a @View";

}
