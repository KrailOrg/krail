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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.Inject;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

import java.text.Collator;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link MasterSitemap} provides the overall structure of the site, and is Singleton scoped. This class refines
 * that by presenting only those pages that the user is authorised to see, and is therefore {@link
 * VaadinSessionScoped}.
 * It also maintains locale-aware labels and sort order, so that the navigation components are presented to the user in
 * the language and sort order of their choice.
 *
 * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
 * login). Use the isxxxUri methods to test a uri for a match to a standard page
 *
 * @author David Sowerby
 * @date 17 May 2014
 */
@VaadinSessionScoped
@Listener
public class DefaultUserSitemap extends DefaultSitemapBase<UserSitemapNode> implements UserSitemap {
    private static Logger log = LoggerFactory.getLogger(DefaultUserSitemap.class);

    private final Translate translate;

    private final List<UserSitemapChangeListener> changeListeners;


    @Inject
    public DefaultUserSitemap(Translate translate, URIFragmentHandler uriHandler, @SessionBus MBassador<BusMessage> eventBus) {
        super(uriHandler);
        this.translate = translate;
        changeListeners = new LinkedList<>();
        eventBus.subscribe(this); // TODO is this needed if the annotation is there
    }


    /**
     * Iterates through contained nodes and resets the label and collation key properties to reflect a change in
     * {@link CurrentLocale}. There is no need to reload all the nodes, no change of page authorisation is dealt with
     * here}
     */
    @Handler
    public synchronized void localeChanged(LocaleChangeBusMessage busMessage) {
        log.debug("responding to locale change to {}", busMessage.getNewLocale());
        List<UserSitemapNode> nodeList = getAllNodes();
        Collator collator = translate.collator();
        for (UserSitemapNode userNode : nodeList) {
            String label = translate.from(userNode.getMasterNode()
                                                  .getLabelKey());
            userNode.setLabel(label);
            userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
        }

        fireLabelsChanged();
    }

    private void fireLabelsChanged() {
        for (UserSitemapChangeListener listener : changeListeners) {
            listener.labelsChanged();
        }
    }

    /**
     * Returns the userNode which contains {@code masterNode}. Note that this method is not very efficient for larger
     * instances, it has to scan the {@link UserSitemap} until it finds a match. Returns null if no match found (and
     * will have scanned the entire {@link UserSitemap}
     *
     * @param masterNode
     *
     * @return
     */
    @Override
    public synchronized UserSitemapNode userNodeFor(SitemapNode masterNode) {
        for (UserSitemapNode candidate : getAllNodes()) {
            if (candidate.getMasterNode() == masterNode) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * The {@link UserSitemap} never creates a node this way
     */
    @Override
    public UserSitemapNode createNode(String segment) {
        return null;
    }



    @Override
    public synchronized void setLoaded(boolean loaded) {
        super.setLoaded(loaded);
        buildUriMap();
        if (loaded) {
            fireStructureChanged();
        }
    }

    @Override
    public synchronized void buildUriMap() {
        uriMap.clear();
        for (UserSitemapNode node : forest.getAllNodes()) {
            uriMap.put(uri(node), node);
        }

    }

    private void fireStructureChanged() {
        for (UserSitemapChangeListener listener : changeListeners) {
            listener.structureChanged();
        }

    }

    public void addChangeListener(UserSitemapChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(UserSitemapChangeListener listener) {
        changeListeners.remove(listener);
    }

    public Translate getTranslate() {
        return translate;
    }

    @Override
    public void addListener(UserSitemapChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeListener(UserSitemapChangeListener listener) {
        changeListeners.remove(listener);
    }


}
