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

package uk.q3c.krail.core.navigate.sitemap.set;

import uk.q3c.krail.core.navigate.sitemap.Sitemap;
import uk.q3c.krail.core.navigate.sitemap.SitemapLockedException;

import javax.annotation.Nonnull;

/**
 * Created by David Sowerby on 05 Jan 2016
 */
public interface SitemapQueue<T extends Sitemap> {

    /**
     * Returns the {@link Sitemap} currently at the head of the queue.  This call will block if there is no head (that is, the queue is empty), and release
     * when a model is added.
     *
     * @return the {@link Sitemap} currently at the head of the queue
     */
    T getCurrentModel();


    /**
     * Adds a model to the queue.  This will not be current until {@link #publishNextModel} is called
     *
     * @param newModel the model to add
     * @return true of the model added , false if the queue is already full
     * @throws SitemapLockedException if the model is not locked before it is added
     */
    boolean addModel(@Nonnull T newModel);

    /**
     * Removes the current model and replaces it with the next in the queue, thus the next becomes current.  A message is published via the event bus when
     * successful.  Note that this message is not published for the first model to be made available.
     *
     * @return true when next model is published, false if not (because there is none in the queue)
     */
    boolean publishNextModel();

    int size();

}
